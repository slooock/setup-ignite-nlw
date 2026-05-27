package com.example;

import java.io.*;
import java.net.*;
import java.security.*;
import java.util.*;
import javax.net.ssl.*;

/**
 * Insecure network communication and cryptography vulnerabilities.
 *
 * SpotBugs bug patterns triggered:
 *   - UNVALIDATED_REDIRECT (open redirect)
 *   - SSL_BAD_CERT_TRUST (trust all certificates)
 *   - INSECURE_COOKIE (cookie without secure flag)
 *   - PREDICTABLE_RANDOM (insecure session ID generation)
 *   - STATIC_IV (reused / static IV in encryption)
 */
public class InsecureNetworkVuln {

    // SpotBugs: SSL_BAD_CERT_TRUST — trusts ALL certificates (MITM vulnerable)
    public static HttpsURLConnection createInsecureTlsConnection(String urlStr) throws Exception {
        TrustManager[] trustAll = new TrustManager[]{
            new X509TrustManager() {
                public java.security.cert.X509Certificate[] getAcceptedIssuers() { return null; }
                public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {}
                public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {}
            }
        };
        SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null, trustAll, new java.security.SecureRandom());
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        // SpotBugs: SSL_BAD_HOSTNAME — bypasses hostname verification
        HttpsURLConnection.setDefaultHostnameVerifier((hostname, session) -> true);

        URL url = new URL(urlStr);
        return (HttpsURLConnection) url.openConnection();
    }

    // SpotBugs: PREDICTABLE_RANDOM — insecure session token
    public static String generateSessionId() {
        Random rng = new Random(System.currentTimeMillis()); // predictable seed
        return Long.toHexString(rng.nextLong());
    }

    // SpotBugs: STATIC_IV — static/reused IV in AES-CBC
    public static byte[] encryptAesCbc(String plaintext, byte[] keyBytes) throws Exception {
        byte[] iv = new byte[16]; // All-zero IV — critical flaw
        javax.crypto.spec.IvParameterSpec ivSpec = new javax.crypto.spec.IvParameterSpec(iv);
        javax.crypto.spec.SecretKeySpec keySpec = new javax.crypto.spec.SecretKeySpec(keyBytes, "AES");
        javax.crypto.Cipher cipher = javax.crypto.Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(javax.crypto.Cipher.ENCRYPT_MODE, keySpec, ivSpec);
        return cipher.doFinal(plaintext.getBytes());
    }

    // SpotBugs: UNVALIDATED_REDIRECT
    public static String redirect(String location) {
        // No validation on 'location' — open redirect
        return "Location: " + location;
    }

    // SpotBugs: SSRF + unvalidated input for server connection
    public static void connectToServer(String host, int port) throws IOException {
        Socket socket = new Socket(host, port); // user-controlled host:port
        socket.close();
    }

    public static void main(String[] args) throws Exception {
        System.out.println("Session: " + generateSessionId());
        System.out.println("Redirect: " + redirect("http://evil.com"));
    }
}
