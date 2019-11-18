# ------------------------------------------------------------------------------
# Lambdas
# ------------------------------------------------------------------------------
resource "aws_lambda_function" "psc_discrepancy_parser" {
  s3_bucket     = "${var.release_bucket_name}"
  s3_key        = "${var.project_name}/${var.project_name}-${var.release_version}.jar"
  function_name = "${var.project_name}"
  role          = "${var.execution_role}"
  handler       = "${var.handler}"
  memory_size   = "${var.memory_megabytes}"
  timeout       = "${var.timeout_seconds}"
  runtime       = "${var.runtime}"
}

resource "aws_lambda_permission" "allow_bucket" {
  statement_id  = "AllowExecutionFromS3Bucket"
  action        = "lambda:InvokeFunction"
  function_name = "${aws_lambda_function.psc_discrepancy_parser.arn}"
  principal     = "s3.amazonaws.com"
  source_arn    = "arn:aws:s3:::${var.psc_discrepancy_bucket}"
}

resource "aws_s3_bucket_notification" "bucket_notification" {
  bucket = "${var.psc_discrepancy_bucket}"

  lambda_function {
    lambda_function_arn = "${aws_lambda_function.psc_discrepancy_parser.arn}"
    events              = ["s3:ObjectCreated:*"]
    filter_prefix       = "source/"
    filter_suffix       = ".csv"
  }
}
