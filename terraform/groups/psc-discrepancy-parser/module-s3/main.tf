resource "aws_s3_bucket" "psc_discrepancy_report_bucket" {
    bucket = var.psc_discrepancy_bucket
}

resource "aws_s3_bucket_policy" "allow_ses_access" {
    bucket = aws_s3_bucket.psc_discrepancy_report_bucket.id

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
    bucket = aws_s3_bucket.psc_discrepancy_report_bucket.id

    block_public_acls = true
    block_public_policy = true
    ignore_public_acls = true
    restrict_public_buckets = true
}
