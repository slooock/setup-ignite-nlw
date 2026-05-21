package main

import (
	"crypto/md5"
	"crypto/sha1"
	"database/sql"
	"fmt"
	"os/exec"
)

// Gosec: G104 - Unhandled errors
// Gosec: G204 - Audit use of command execution
func runCommand(input string) {
	cmd := exec.Command("sh", "-c", "echo "+input)
	cmd.Run() // Unhandled error & command execution vulnerability
}

// Gosec: G401 & G501 - Use of weak cryptographic primitive (MD5 / SHA1)
func weakHash(data string) {
	h5 := md5.New()
	h5.Write([]byte(data))
	fmt.Printf("MD5: %x\n", h5.Sum(nil))

	h1 := sha1.New()
	h1.Write([]byte(data))
	fmt.Printf("SHA1: %x\n", h1.Sum(nil))
}

// Gosec: G201 & G202 - SQL injection / string concatenation
func getUser(db *sql.DB, username string) {
	query := fmt.Sprintf("SELECT id, name FROM users WHERE username = '%s'", username)
	db.Query(query) // SQL injection via direct concatenation
}

// Gosec: G101 - Hardcoded credentials
const DBPassword = "GosecSuperSecurePassword123!"

func main() {
	fmt.Println("Gosec target")
}
