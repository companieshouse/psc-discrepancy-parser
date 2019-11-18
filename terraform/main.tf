provider "aws" {
  region = "${var.aws_region}"
}

terraform {
    backend "s3" {}
}

module "s3" {
    source                     = "module-s3"
    psc_discrepancy_bucket     = "${var.psc_discrepancy_bucket}"
    execution_role             = "${module.lambda-roles.execution_role}"
    psc_discrepancy_kms_alias  = "${var.psc_discrepancy_kms_alias}"
}

module "lambda" {
    source                   = "module-lambda"
    project_name             = "${var.project_name}"
    handler                  = "${var.handler}"
    memory_megabytes         = "${var.memory_megabytes}"
    runtime                  = "${var.runtime}"
    timeout_seconds          = "${var.timeout_seconds}"
    psc_discrepancy_bucket   = "${var.psc_discrepancy_bucket}"
    release_version          = "${var.release_version}"
    release_bucket_name      = "${var.release_bucket_name}"
    execution_role           = "${module.lambda-roles.execution_role}"
}

module "lambda-roles" {
    source                   = "module-lambda-roles"
    project_name             = "${var.project_name}"
    psc_discrepancy_bucket   = "${var.psc_discrepancy_bucket}"
}

module "ses" {
    source                          = "module-ses"
    psc_discrepancy_bucket          = "${var.psc_discrepancy_bucket}"
    psc_discrepancy_bucket_prefix   = "${var.psc_discrepancy_bucket_prefix}"
    psc_email_recipient             = "${var.psc_email_recipient}"
}