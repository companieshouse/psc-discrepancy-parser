# ------------------------------------------------------------------------------
# Lambdas
# ------------------------------------------------------------------------------
resource "aws_lambda_function" "psc_discrepancy_parser" {
  s3_bucket     = "${var.release_bucket_name}"
  s3_key        = "${var.project_name}/${var.project_name}-${var.release_version}.zip"
  function_name = "${var.project_name}"
  role          = "${var.execution_role}"
  handler       = "${var.handler}"
  memory_size   = "${var.memory_megabytes}"
  timeout       = "${var.timeout_seconds}"
  runtime       = "${var.runtime}"
}

resource "aws_lambda_permission" "apigw" {
  action        = "lambda:InvokeFunction"
  function_name = "${aws_lambda_function.psc_discrepancy_parser.arn}"
  principal     = "s3.amazonaws.com"

  # The /*/* portion grants access from any method on any resource
  # within the API Gateway "REST API".
  source_arn = "arn:aws:s3:::${var.psc_discrepancy_bucket}/psc-discrepancy-reports/source/*"
}