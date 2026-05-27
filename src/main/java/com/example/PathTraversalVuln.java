package com.example;

import java.io.*;
import java.net.*;
import java.nio.file.*;

/**
 * Path Traversal, SSRF, XXE and deserialization vulnerabilities.
 *
 * SpotBugs bug patterns triggered:
 *   - PATH_TRAVERSAL_IN (file path from user input)
 *   - URLCONNECTION_SSRF_FD (Server Side Request Forgery)
 *   - OBJECT_DESERIALIZATION (insecure deserialization)
 *   - HARD_CODE_PASSWORD (hardcoded credentials)
 */
public class PathTraversalVuln {

    // Hardcoded credentials — SpotBugs: HARD_CODE_PASSWORD
    private static final String DB_PASSWORD = "admin123";
    private static final String SECRET_KEY   = "MyS3cr3tK3y!";
    private static final String API_TOKEN    = "Bearer eyJhbGciOiJSUzI1NiJ9.secret";

    // SpotBugs: PATH_TRAVERSAL_IN
    public static String readFile(String filename) throws IOException {
        File f = new File("/var/data/" + filename);   // user-controlled path
        BufferedReader reader = new BufferedReader(new FileReader(f));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        reader.close();
        return sb.toString();
    }

    // SpotBugs: PATH_TRAVERSAL_IN (NIO variant)
    public static byte[] readFileNio(String filename) throws IOException {
        Path path = Paths.get("/uploads/", filename);   // traversal possible
        return Files.readAllBytes(path);
    }

    // SpotBugs: URLCONNECTION_SSRF_FD
    public static String fetchUrl(String userUrl) throws IOException {
        URL url = new URL(userUrl);                     // SSRF: user-controlled URL
        URLConnection con = url.openConnection();
        InputStream is = con.getInputStream();
        return new String(is.readAllBytes());
    }

    // SpotBugs: OBJECT_DESERIALIZATION
    public static Object deserialize(byte[] data) throws Exception {
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        ObjectInputStream ois = new ObjectInputStream(bais);
        return ois.readObject();                        // Insecure deserialization
    }

    // SpotBugs: COMMAND_INJECTION / PATH_TRAVERSAL via exec
    public static void processUpload(String filename) throws IOException {
        Runtime.getRuntime().exec(new String[]{"convert", "/uploads/" + filename, "/out/" + filename});
    }

    public static void main(String[] args) throws Exception {
        System.out.println("DB password used: " + DB_PASSWORD);
        System.out.println("Secret key: " + SECRET_KEY);
        System.out.println("API Token: " + API_TOKEN);
    }
}
