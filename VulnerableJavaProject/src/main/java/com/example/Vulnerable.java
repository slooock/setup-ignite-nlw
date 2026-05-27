package com.example;

import java.io.*;
import java.net.*;
import java.security.*;
import java.sql.*;
import java.util.Random;
import javax.crypto.*;
import javax.crypto.spec.*;

/**
 * Vulnerable.java — Intentionally insecure code for SpotBugs SAST scanning.
 *
 * This class covers all major SpotBugs security bug patterns:
 *   - SQL_NONCONSTANT_STRING_PASSED_TO_EXECUTE  (SQL Injection)
 *   - COMMAND_INJECTION / RUNTIME_EXEC_CMD      (OS Command Injection)
 *   - PATH_TRAVERSAL_IN                         (Path Traversal)
 *   - OBJECT_DESERIALIZATION                    (Insecure Deserialization)
 *   - WEAK_MESSAGE_DIGEST_MD5/SHA1              (Weak Hashing)
 *   - DES_USAGE / ECB_MODE                      (Weak Encryption)
 *   - STATIC_IV                                 (Reused IV in AES-CBC)
 *   - PREDICTABLE_RANDOM                        (Insecure Randomness)
 *   - HARD_CODE_PASSWORD                        (Hardcoded Credentials)
 *   - SSL_BAD_CERT_TRUST / SSL_BAD_HOSTNAME     (TLS Bypass)
 *   - URLCONNECTION_SSRF_FD                     (SSRF)
 *   - HTTP_RESPONSE_SPLITTING                   (Response Splitting)
 *   - NULL_DEREFERENCE                          (NPE)
 */
public class Vulnerable {

    // -----------------------------------------------------------------------
    // HARD_CODE_PASSWORD: Credentials hardcoded in source
    // -----------------------------------------------------------------------
    private static final String DB_URL      = "jdbc:mysql://localhost/prod";
    private static final String DB_USER     = "root";
    private static final String DB_PASSWORD = "SuperSecret@2024";     // SpotBugs: HARD_CODE_PASSWORD
    private static final String API_KEY     = "sk-live-abc123XYZ789";  // SpotBugs: HARD_CODE_KEY
    private static final String SECRET      = "MyHardcodedSecret!";

    private static Connection conn;

    public static void main(String[] args) throws Exception {
        System.out.println("Vulnerable app starting...");
        System.out.println("Weak token: " + generateToken());
        loginUser("admin' OR '1'='1", "ignored");
    }

    // -----------------------------------------------------------------------
    // SQL_NONCONSTANT_STRING_PASSED_TO_EXECUTE: Classic SQL Injection
    // -----------------------------------------------------------------------
    public static void loginUser(String username, String password) throws SQLException {
        String sql = "SELECT * FROM users WHERE username='" + username +
                     "' AND password='" + password + "'";
        Statement stmt = conn.createStatement();
        stmt.executeQuery(sql);   // SpotBugs: SQL_NONCONSTANT_STRING_PASSED_TO_EXECUTE
    }

    public static void deleteRecord(String id) throws SQLException {
        Statement stmt = conn.createStatement();
        stmt.execute("DELETE FROM logs WHERE id=" + id);  // SpotBugs: SQL_NONCONSTANT_STRING_PASSED_TO_EXECUTE
    }

    // -----------------------------------------------------------------------
    // COMMAND_INJECTION: User-controlled OS command execution
    // -----------------------------------------------------------------------
    public static void runCommand(String userInput) throws IOException {
        Runtime.getRuntime().exec(userInput);       // SpotBugs: COMMAND_INJECTION
    }

    public static void runShell(String arg) throws IOException {
        Runtime.getRuntime().exec(new String[]{"sh", "-c", arg}); // SpotBugs: COMMAND_INJECTION
    }

    // -----------------------------------------------------------------------
    // PATH_TRAVERSAL_IN: Filename from user input used in file open
    // -----------------------------------------------------------------------
    public static String readFile(String filename) throws IOException {
        File f = new File("/var/app/uploads/" + filename);  // SpotBugs: PATH_TRAVERSAL_IN
        BufferedReader br = new BufferedReader(new FileReader(f));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) sb.append(line).append("\n");
        br.close();
        return sb.toString();
    }

    // -----------------------------------------------------------------------
    // OBJECT_DESERIALIZATION: Untrusted deserialization
    // -----------------------------------------------------------------------
    public static Object deserialize(byte[] data) throws Exception {
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data));
        return ois.readObject();   // SpotBugs: OBJECT_DESERIALIZATION
    }

    // -----------------------------------------------------------------------
    // WEAK_MESSAGE_DIGEST_MD5 and WEAK_MESSAGE_DIGEST_SHA1
    // -----------------------------------------------------------------------
    public static byte[] hashMd5(String input) throws NoSuchAlgorithmException {
        return MessageDigest.getInstance("MD5").digest(input.getBytes()); // SpotBugs: WEAK_MESSAGE_DIGEST_MD5
    }

    public static byte[] hashSha1(String input) throws NoSuchAlgorithmException {
        return MessageDigest.getInstance("SHA-1").digest(input.getBytes()); // SpotBugs: WEAK_MESSAGE_DIGEST_SHA1
    }

    // -----------------------------------------------------------------------
    // DES_USAGE: Weak encryption algorithm
    // -----------------------------------------------------------------------
    public static byte[] encryptDes(String data) throws Exception {
        KeyGenerator kg = KeyGenerator.getInstance("DES");   // SpotBugs: DES_USAGE
        SecretKey key = kg.generateKey();
        Cipher c = Cipher.getInstance("DES");
        c.init(Cipher.ENCRYPT_MODE, key);
        return c.doFinal(data.getBytes());
    }

    // -----------------------------------------------------------------------
    // ECB_MODE: AES in ECB mode (no IV, deterministic output)
    // -----------------------------------------------------------------------
    public static byte[] encryptAesEcb(String data, byte[] keyBytes) throws Exception {
        SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding"); // SpotBugs: ECB_MODE
        cipher.init(Cipher.ENCRYPT_MODE, keySpec);
        return cipher.doFinal(data.getBytes());
    }

    // -----------------------------------------------------------------------
    // STATIC_IV: Zero / hardcoded IV in AES-CBC
    // -----------------------------------------------------------------------
    public static byte[] encryptAesCbc(String data, byte[] keyBytes) throws Exception {
        byte[] iv = new byte[16];  // All-zero IV — SpotBugs: STATIC_IV
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
        return cipher.doFinal(data.getBytes());
    }

    // -----------------------------------------------------------------------
    // PREDICTABLE_RANDOM: java.util.Random is not cryptographically secure
    // -----------------------------------------------------------------------
    public static String generateToken() {
        Random rng = new Random();   // SpotBugs: PREDICTABLE_RANDOM
        return Long.toHexString(rng.nextLong());
    }

    // -----------------------------------------------------------------------
    // SSL_BAD_CERT_TRUST + SSL_BAD_HOSTNAME: Trust-all TLS bypass
    // -----------------------------------------------------------------------
    public static void insecureTlsCall(String url) throws Exception {
        javax.net.ssl.TrustManager[] tm = new javax.net.ssl.TrustManager[]{
            new javax.net.ssl.X509TrustManager() {
                public java.security.cert.X509Certificate[] getAcceptedIssuers() { return null; }
                public void checkClientTrusted(java.security.cert.X509Certificate[] c, String a) {}
                public void checkServerTrusted(java.security.cert.X509Certificate[] c, String a) {} // SpotBugs: SSL_BAD_CERT_TRUST
            }
        };
        javax.net.ssl.SSLContext sc = javax.net.ssl.SSLContext.getInstance("TLS");
        sc.init(null, tm, null);
        javax.net.ssl.HttpsURLConnection conn = (javax.net.ssl.HttpsURLConnection) new URL(url).openConnection();
        conn.setSSLSocketFactory(sc.getSocketFactory());
        conn.setHostnameVerifier((h, s) -> true);  // SpotBugs: SSL_BAD_HOSTNAME
    }

    // -----------------------------------------------------------------------
    // URLCONNECTION_SSRF_FD: SSRF — user-controlled URL fetched
    // -----------------------------------------------------------------------
    public static String fetchRemote(String userUrl) throws IOException {
        URL u = new URL(userUrl);                   // SpotBugs: URLCONNECTION_SSRF_FD
        return new String(u.openStream().readAllBytes());
    }

    // -----------------------------------------------------------------------
    // HTTP_RESPONSE_SPLITTING
    // -----------------------------------------------------------------------
    public static String buildHeader(String location) {
        return "HTTP/1.1 302 Found\r\nLocation: " + location + "\r\n\r\n"; // SpotBugs: HTTP_RESPONSE_SPLITTING
    }

    // -----------------------------------------------------------------------
    // NULL_DEREFERENCE: Potential NPE on method call chain
    // -----------------------------------------------------------------------
    public static int processInput(String input) {
        String result = null;
        if (!input.isEmpty()) result = input.trim();
        return result.length(); // SpotBugs: NULL_DEREFERENCE when input is " "
    }
}
