resource "aws_security_group" "psc_discrepancy_parser" {
  name        = "${var.environment}-${var.project_name}-lambda-into-vpc"
  description = "Outbound rules for psc discrepancy parser lambda"
  vpc_id = "${var.vpc_id}"

  egress {
    from_port       = 0
    to_port         = 0
    protocol        = "-1"
    cidr_blocks     = ["0.0.0.0/0"]
  }
}

output "lambda_into_vpc_id" {
  value = "${aws_security_group.psc_discrepancy_parser.id}"
}