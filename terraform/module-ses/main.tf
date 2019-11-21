# ------------------------------------------------------------------------------
# SES
# ------------------------------------------------------------------------------
resource "aws_ses_receipt_rule" "store" {
    name            = "psc_discrepancy_forward_to_s3"
    rule_set_name   = "${var.rule_set_name}"
    recipients      = ["${var.psc_email_recipient}"]
    enabled         = true
    scan_enabled    = true
    
    s3_action {
        bucket_name         = "${var.psc_discrepancy_bucket}"
        object_key_prefix   = "${var.psc_discrepancy_bucket_prefix}"
        kms_key_arn         = "${var.s3_bucket_kms_arn}"
        position            = 1
    }
}