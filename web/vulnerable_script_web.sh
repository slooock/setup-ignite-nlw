#!/bin/bash

# ==============================================================================
# AVISO: Este arquivo contém vulnerabilidades intencionais para fins de teste.
# NÃO utilize este script em ambientes de produção.
# ==============================================================================

# 1. Credenciais Hardcoded (Hardcoded Secrets)
DB_USER="admin"
DB_PASS="SenhaSuperSecreta123" # Vulnerabilidade: Exposição de credenciais
API_KEY="AIzaSyA1234567890abcdefghijklmnopqrstuv" # Exemplo de chave de API
AWS_ACCESS_KEY_ID="AKIAIOSFODNN7EXAMPLE" # Exemplo de chave AWS
AWS_SECRET_ACCESS_KEY="wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY"
FTP_PASSWORD="minha_senha_do_ftp_123"
SSH_KEY="-----BEGIN RSA PRIVATE KEY-----\nMIIEogIBAAKCAQEA0R...[TRUNCATED]...\n-----END RSA PRIVATE KEY-----"


echo "Iniciando script de teste..."

# 2. Injeção de Comando (Command Injection)
echo "Digite o nome do diretório para listar:"
read -r user_dir

# Vulnerabilidade: O conteúdo de $user_dir é passado diretamente para o eval.
# Se o usuário digitar "; rm -rf /", o comando será executado.
eval "ls -la $user_dir"

# 3. Uso inseguro de arquivos temporários
# Vulnerabilidade: Nome de arquivo previsível em diretório compartilhado (/tmp)
# Permite ataques de link simbólico ou negação de serviço.
TMP_FILE="/tmp/meu_arquivo_temporario.log"
echo "Log de execução" > $TMP_FILE

# 4. Variáveis não entre aspas (Word Splitting / Globbing)
# Vulnerabilidade: Se o argumento contiver espaços ou caracteres especiais,
# o comportamento será inesperado.
if [ -f $1 ]; then
    echo "O arquivo $1 existe."
fi

# 5. Execução de comandos sem caminho absoluto (Path Hijacking)
# Vulnerabilidade: Depende da variável PATH. Se o PATH for manipulado,
# um binário malicioso com o nome 'cat' pode ser executado.
cat $TMP_FILE

rm $TMP_FILE

# 6. Download e Execução Insegura (Insecure Curl pipe to Bash)
# Vulnerabilidade: Baixar script da internet e executar diretamente sem verificação.
# O invasor pode alterar o conteúdo do script durante a transmissão ou no servidor.
curl -s http://example.com/install.sh | bash

# 7. Permissões Inseguras (Insecure Permissions)
# Vulnerabilidade: Dar permissão total (leitura, escrita, execução) para todos.
touch arquivo_sensivel.txt
chmod 777 arquivo_sensivel.txt

# 8. Inclusão de Arquivo Insegura (Insecure Sourcing)
# Vulnerabilidade: Carregar arquivo de configuração baseado em entrada do usuário.
echo "Digite o caminho do arquivo de configuração:"
read -r user_cfg
# Se o usuário passar um arquivo malicioso, ele será executado no contexto deste script.
. $user_cfg

echo "Script finalizado."

