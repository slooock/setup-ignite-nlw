provider "aws" {
  region = "us-east-1"
}

# 1. S3 bucket with multiple vulnerabilities: public access, no encryption, no versioning, public ACL
resource "aws_s3_bucket" "vulnerable_bucket" {
  bucket = "my-very-vulnerable-bucket-12345"
  acl    = "public-read-write"

  tags = {
    Name = "Vulnerable Bucket"
  }
}

# 2. Security group with SSH (22) and RDP (3389) and all egress open to the world
resource "aws_security_group" "vulnerable_sg" {
  name        = "vulnerable-sg"
  description = "Security group with wide open ports"
  vpc_id      = "vpc-123456"

  ingress {
    description = "SSH open to world"
    from_port   = 22
    to_port     = 22
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  ingress {
    description = "RDP open to world"
    from_port   = 3389
    to_port     = 3389
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  ingress {
    description = "All traffic open"
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}

# 3. RDS DB Instance that is publicly accessible and unencrypted
resource "aws_db_instance" "vulnerable_db" {
  allocated_storage    = 10
  engine               = "mysql"
  engine_version       = "5.7"
  instance_class       = "db.t2.micro"
  db_name              = "mydb"
  username             = "admin"
  password             = "supersecretpassword123" # Hardcoded password
  parameter_group_name = "default.mysql5.7"
  skip_final_snapshot  = true

  publicly_accessible = true
  storage_encrypted   = false
}

# 4. IAM policy with full wildcard privileges (admin) on all resources
resource "aws_iam_policy" "vulnerable_policy" {
  name        = "vulnerable-policy"
  path        = "/"
  description = "Policy with full wildcard access"

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Action = [
          "*"
        ]
        Effect   = "Allow"
        Resource = "*"
      }
    ]
  })
}

# 5. EBS volume with encryption disabled
resource "aws_ebs_volume" "vulnerable_ebs" {
  availability_zone = "us-east-1a"
  size              = 40
  encrypted         = false

  tags = {
    Name = "Vulnerable EBS"
  }
}