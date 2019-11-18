resource "aws_kms_key" "psc_discrepancy_encryption_key" {
    description             = "Encrypt user uploaded psc discrepancy report data."
    deletion_window_in_days = 30
}

resource "aws_kms_alias" "psc_discrepancy_encryption_key_alias" {
  name          = "${var.psc_discrepancy_kms_alias}"
  target_key_id = "${aws_kms_key.psc_discrepancy_encryption_key.key_id}"
}

resource "aws_s3_bucket" "psc_discrepancy_report_bucket" {
    bucket = "${var.psc_discrepancy_bucket}"

    server_side_encryption_configuration {
        rule {
            apply_server_side_encryption_by_default {
                kms_master_key_id = "${aws_kms_key.psc_discrepancy_encryption_key.arn}"
                sse_algorithm     = "aws:kms"
            }
        }
    }
}

resource "aws_s3_bucket_policy" "allow_ses_access" {
    bucket = "${aws_s3_bucket.psc_discrepancy_report_bucket.id}"

    policy = <<POLICY
{
    "Version": "2012-10-17",
    "Id": "Policy1573740923507",
    "Statement": [
        {
            "Sid": "Stmt1573740920752",
            "Effect": "Allow",
            "Principal": {
                "Service": "ses.amazonaws.com"
            },
            "Action": "s3:PutObject",
            "Resource": "${aws_s3_bucket.psc_discrepancy_report_bucket.arn}/*"
        }
    ]
}
POLICY
}

resource "aws_s3_bucket_public_access_block" "block_public_access" {
    bucket = "${aws_s3_bucket.psc_discrepancy_report_bucket.id}"

    block_public_acls = true
}

resource "aws_kms_grant" "psc_discrepancy_encryption_key_grant" {
    name                = "psc-discrepancy-key-grant"
    key_id              = "${aws_kms_key.psc_discrepancy_encryption_key.key_id}"
    grantee_principal   = "${var.execution_role}"
    operations          = ["Encrypt", "Decrypt", "GenerateDataKey"]
}