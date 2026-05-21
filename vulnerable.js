const crypto = require('crypto');
const exec = require('child_process').exec;

// ESLint / NodeJS: Unsafe eval
function runCode(userInput) {
  eval(userInput);
}

// ESLint / NodeJS: Command injection via exec
function runCommand(cmd) {
  exec(cmd, (err, stdout) => {
    console.log(stdout);
  });
}

// ESLint / NodeJS: Insecure crypto hash (MD5)
function getHash(text) {
  return crypto.createHash('md5').update(text).digest('hex');
}

// ESLint / NodeJS: Hardcoded secrets
const DB_CONN_STRING = "mongodb://dbuser:MySuperPassword123!@localhost:27017/admin";

module.exports = { runCode, runCommand, getHash, DB_CONN_STRING };
