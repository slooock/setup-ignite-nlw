import subprocess
import hashlib
import os

# Bandit: B105 - Hardcoded password/secret
DATABASE_PASSWORD = "my-super-secret-password-123!"
API_KEY = "xoxb-1234567890-abcdefghijklmnopqrstuv"

def execute_user_command(user_input):
    # Bandit: B602 - Subprocess with shell=True (Command Injection)
    subprocess.Popen(user_input, shell=True)

def insecure_hash(data):
    # Bandit: B303 - Use of insecure MD5 hash
    return hashlib.md5(data.encode()).hexdigest()

def unsafe_execution(code):
    # Bandit: B307 - Use of eval
    return eval(code)

def start_server():
    # Bandit: B104 - Hardcoded bind all interfaces
    import socket
    s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    s.bind(('0.0.0.0', 8080))
