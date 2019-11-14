# ------------------------------------------------------------------------------
# SES
# ------------------------------------------------------------------------------
resource "aws_ses_receipt_rule_set" "psc_discrepancy_report_email" {
    rule_set_name   = "email-to-s3-rule"
}

resource "aws_ses_receipt_rule" "store" {
    name            = "psc_discrepancy_s3_rule"
    rule_set_name   = "${aws_ses_receipt_rule_set.psc_discrepancy_report_email.rule_set_name}"
    recipients      = ["${var.psc_email_recipient}"]
    enabled         = true
    scan_enabled    = true
    
    s3_action {
        bucket_name         = "${var.psc_discrepancy_bucket}"
        object_key_prefix   = "${var.psc_discrepancy_bucket_prefix}"
        position            = 1
    }
}