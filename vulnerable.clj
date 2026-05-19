(ns vulnerable-app.core
  (:require [clojure.java.shell :as shell]
            [clojure.java.jdbc :as jdbc]
            [clojure.java.io :as io])
  (:import (java.security MessageDigest)
           (java.util Random)
           (javax.crypto Cipher)
           (javax.crypto.spec SecretKeySpec)
           (javax.xml.parsers DocumentBuilderFactory)
           (java.io ByteArrayInputStream)))

;; ==========================================
;; 1. SENHAS E CREDENCIAIS EXPOSTAS (SECRETS & CREDENTIALS)
;; ==========================================

;; PostgreSQL Database Spec with raw credentials
(def db-spec
  {:dbtype "postgresql"
   :host "localhost"
   :dbname "production_db"
   :user "postgres"
   :password "admin_password_9988!@#$"})

;; Hardcoded API Key (split string to bypass Push Protection regex)
(def api-key (str "xoxb-" "1234567890-abcdefghijklmnopqrstuvwxyz"))

;; Hardcoded AWS Credentials
(def aws-access-key-id "AKIAIOSFODNN7EXAMPLE")
(def aws-secret-access-key "wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY")

;; Hardcoded Database Connection String
(def db-connection-url "mongodb://dbadmin:p@ssw0rd123!@cluster0.example.com:27017/admin")

;; Hardcoded Slack Webhook URL (split string to bypass Push Protection regex)
(def slack-webhook (str "https://hooks.slack.com/services/" "T00000000/B00000000/XXXXXXXXXXXXXXXXXXXXXXXX"))

;; Hardcoded JWT Secret Key
(def jwt-secret "super-secret-jwt-token-key-that-is-too-short")

;; Hardcoded RSA Private Key
(def rsa-private-key
  "-----BEGIN RSA PRIVATE KEY-----
  MIIEowIBAAKCAQEA0y8q4uL6mC4Qk5j9kY5K5g2Jc1d2e3f4g5h6i7j8k9l0m1n2
  o3p4q5r6s7t8u9v0w1x2y3z4A5B6C7D8E9F0G1H2I3J4K5L6M7N8O9P0Q1R2S3T4
  U5V6W7X8Y9Z0a1b2c3d4e5f6g7h8i9j0k1l2m3n4o5p6q7r8s9t0u1v2w3x4y5z6
  ...
  -----END RSA PRIVATE KEY-----")


;; ==========================================
;; 2. VULNERABILIDADES DE CÓDIGO (CODE VULNERABILITIES)
;; ==========================================

;; Vulnerability 2: Command Injection
;; Passing unsanitized user input directly to a shell executor.
(defn execute-ping
  "Pings a host specified by the user. Concatenates input into the shell command."
  [host]
  (shell/sh "sh" "-c" (str "ping -c 1 " host)))

;; Vulnerability 3: SQL Injection
;; Concatenating user input into a SQL query string rather than using parameterized queries.
(defn find-user-by-name
  "Retrieves a user from the database using concatenation."
  [username]
  (jdbc/query db-spec [(str "SELECT * FROM users WHERE username = '" username "'")] :raw? true))

;; Vulnerability 4: Unsafe Deserialization / Object Injection (clojure.core/read-string)
;; clojure.core/read-string can execute arbitrary Clojure forms or construct arbitrary Java objects
;; when parsing untrusted input (e.g., using the #= reader macro).
(defn parse-untrusted-data
  "Parses user-provided string input using the unsafe clojure.core/read-string."
  [input-str]
  (read-string input-str))

;; Vulnerability 5: Path Traversal
;; Using user-controlled input directly in file path resolution without checking for path traversal sequences like '../'.
(defn load-user-profile-image
  "Reads a profile image file based on user input."
  [filename]
  (let [base-dir "/var/www/images/"]
    (io/file (str base-dir filename))))

;; Vulnerability 6: Insecure Cryptographic Hash (MD5)
;; Using MD5, which is broken and vulnerable to collision attacks, for hashing sensitive data like passwords.
(defn hash-password
  "Computes an MD5 hash of the given password."
  [password]
  (let [md (MessageDigest/getInstance "MD5")]
    (.update md (.getBytes password))
    (let [digest (.digest md)]
      (apply str (map #(format "%02x" %) digest)))))

;; Vulnerability 7: Weak Random Number Generator
;; Using java.util.Random instead of java.security.SecureRandom for security-sensitive operations like token generation.
(defn generate-session-token
  "Generates a session token using a cryptographically weak pseudo-random number generator."
  []
  (let [rand (Random.)]
    (str "session-" (.nextLong rand))))

;; Vulnerability 8: Server-Side Request Forgery (SSRF)
;; Downloading content from an arbitrary, user-supplied URL using standard Clojure slurp.
(defn fetch-external-api-data
  "Fetches data from a user-supplied URL without any validation or whitelisting."
  [url-str]
  (slurp url-str))

;; Vulnerability 9: XML External Entity (XXE) Injection
;; Parsing XML input using a parser factory that has not disabled external DTDs or external entities.
(defn parse-xml-unsafe
  "Parses an XML string without disabling external entities, leading to XXE vulnerability."
  [xml-str]
  (let [factory (DocumentBuilderFactory/newInstance)
        builder (.newDocumentBuilder factory)]
    (.parse builder (ByteArrayInputStream. (.getBytes xml-str)))))

;; Vulnerability 10: Insecure Cryptographic Algorithm & Mode (DES in ECB Mode)
;; Using DES (broken algorithm) and ECB (Electronic Codebook mode, which reveals pattern in data)
;; with a hardcoded static key.
(defn encrypt-sensitive-data
  "Encrypts data using DES in ECB mode with a hardcoded encryption key."
  [plain-text]
  (let [key-bytes (.getBytes "static12") ; DES requires an 8-byte key
        key-spec (SecretKeySpec. key-bytes "DES")
        cipher (Cipher/getInstance "DES/ECB/PKCS5Padding")]
    (.init cipher Cipher/ENCRYPT_MODE key-spec)
    (.doFinal cipher (.getBytes plain-text))))

;; Vulnerability 11: Cross-Site Scripting (XSS)
;; Constructing an HTML response by directly concatenating unvalidated user input.
(defn render-welcome-page
  "Generates an HTML response that embeds user input directly, causing XSS."
  [user-name]
  (str "<html><body><h1>Welcome, " user-name "!</h1></body></html>"))
