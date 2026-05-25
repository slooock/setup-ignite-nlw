package com.example;

import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;
import java.util.Random;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class Vulnerable {
    public static void main(String[] args) throws Exception {
        System.out.println("Vulnerable class initialized!");
        
        // 1. Weak Cryptographic Hash (MD5 / SHA-1)
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
        
        // 2. Command Injection
        String command = System.getProperty("cmd");
        if (command != null) {
            Runtime.getRuntime().exec(command);
        }
        
        // 3. Predictable Random
        Random r = new Random();
        int randomValue = r.nextInt();
        System.out.println("Random value: " + randomValue);
        
        // 4. Insecure ECB mode / Static Key
        SecretKeySpec key = new SecretKeySpec("staticKey1234567".getBytes(), "AES");
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        
        // 5. Path Traversal
        String filename = System.getProperty("filename");
        if (filename != null) {
            File f = new File("/tmp/" + filename);
            FileInputStream fis = new FileInputStream(f);
            fis.close();
        }
    }
}
