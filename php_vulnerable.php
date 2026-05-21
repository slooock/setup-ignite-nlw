<?php
// PHP_CS / SAST Vulnerabilities

// Hardcoded Password/Secret
$db_password = "PHPCS_Super_Secret_Password_1234!";

// SQL Injection via direct user input
$userId = $_GET['id'];
$query = "SELECT * FROM users WHERE id = " . $userId;

// Unsafe Execution / Command Injection
$cmd = $_POST['cmd'];
exec("ping -c 4 " . $cmd);

// Cross-Site Scripting (XSS) via raw output
echo "<h1>Welcome " . $_GET['name'] . "</h1>";

// Insecure Cryptographic Hashing
$weak_hash = md5($_GET['pass']);

// Eval vulnerability
eval($_GET['code']);
