package com.example;

import java.io.*;
import java.net.*;
import java.sql.*;
import java.security.*;
import java.util.Random;
import javax.crypto.*;
import javax.crypto.spec.*;

/**
 * SQL Injection and other SpotBugs-detectable vulnerabilities.
 * This class intentionally contains security flaws for SAST scanning purposes.
 *
 * SpotBugs bug patterns triggered:
 *   - SQL_NONCONSTANT_STRING_PASSED_TO_EXECUTE (SQL Injection)
 *   - PREDICTABLE_RANDOM (insecure randomness)
 *   - WEAK_MESSAGE_DIGEST_MD5 / WEAK_MESSAGE_DIGEST_SHA1
 *   - DES_USAGE (weak cipher)
 *   - NULL_DEREFERENCE
 *   - HTTP_RESPONSE_SPLITTING
 */
public class SqlInjectionVuln {

    private static Connection conn;

    // SpotBugs: SQL_NONCONSTANT_STRING_PASSED_TO_EXECUTE
    public static void login(String username, String password) throws SQLException {
        String query = "SELECT * FROM users WHERE name = '" + username +
                       "' AND password = '" + password + "'";
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(query);  // Direct SQL injection
    }

    // SpotBugs: SQL_NONCONSTANT_STRING_PASSED_TO_EXECUTE
    public static void deleteUser(String userId) throws SQLException {
        Statement stmt = conn.createStatement();
        stmt.execute("DELETE FROM users WHERE id = " + userId);
    }

    // SpotBugs: PREDICTABLE_RANDOM
    public static String generateToken() {
        Random random = new Random();  // Not cryptographically secure
        return Integer.toHexString(random.nextInt());
    }

    // SpotBugs: WEAK_MESSAGE_DIGEST_MD5
    public static byte[] hashPassword(String password) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        return md.digest(password.getBytes());
    }

    // SpotBugs: WEAK_MESSAGE_DIGEST_SHA1
    public static byte[] hashSha1(String data) throws NoSuchAlgorithmException {
        MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
        return sha1.digest(data.getBytes());
    }

    // SpotBugs: DES_USAGE (weak cipher algorithm)
    public static byte[] encryptDES(String data) throws Exception {
        KeyGenerator kg = KeyGenerator.getInstance("DES");
        SecretKey key = kg.generateKey();
        Cipher cipher = Cipher.getInstance("DES");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(data.getBytes());
    }

    // SpotBugs: HTTP_RESPONSE_SPLITTING
    public static String buildRedirect(String location) {
        return "HTTP/1.1 302 Found\r\nLocation: " + location + "\r\n\r\n";
    }

    // SpotBugs: NULL_DEREFERENCE
    public static int getLength(String s) {
        String trimmed = s.trim();
        if (trimmed.equals("")) {
            trimmed = null;
        }
        return trimmed.length();  // NPE if trimmed is null
    }

    // SpotBugs: COMMAND_INJECTION via Runtime.exec
    public static void runCommand(String userInput) throws IOException {
        Runtime rt = Runtime.getRuntime();
        rt.exec("sh -c " + userInput);
    }

    public static void main(String[] args) throws Exception {
        System.out.println("Token: " + generateToken());
        System.out.println("MD5 hash computed.");
    }
}
