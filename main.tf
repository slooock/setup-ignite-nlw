provider "aws" {
  region = "us-east-1"
}

resource "aws_s3_bucket" "public_bucket" {
  bucket = "exemplo-bucket-inseguro"
  acl    = "public-read" # ❌ Acesso público
}

resource "aws_security_group" "insecure_sg" {
  name        = "insecure-sg"
  description = "Grupo de segurança com SSH aberto"
  vpc_id      = "vpc-12345678"

  ingress {
    from_port   = 22
    to_port     = 22
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"] # ❌ SSH acessível a qualquer IP
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}

resource "aws_iam_policy" "insecure_policy" {
  name        = "policy-com-permissao-total"
  description = "Permite todas as ações em todos os recursos" # ❌ Privilégio excessivo

  policy = jsonencode({
    Version = "2012-10-17",
    Statement = [
      {
        Effect   = "Allow",
        Action   = "*",
        Resource = "*"
      }
    ]
  })
}