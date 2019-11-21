provider "aws" {
  region = "${var.aws_region}"
}

terraform {
    backend "s3" {}
}

# Configure the remote state data source to acquire configuration
# created through the code in ch-service-terraform/aws-mm-networks.
data "terraform_remote_state" "networks" {
  backend = "s3"

  config = {
    bucket = "${var.aws_bucket}"
    key    = "${var.state_prefix}/${var.deploy_to}/${var.deploy_to}.tfstate"
    region = "${var.aws_region}"
  }
}

module "s3" {
    source                     = "./module-s3"
    psc_discrepancy_bucket     = "${var.psc_discrepancy_bucket}"
    execution_role             = "${module.lambda-roles.execution_role}"
    psc_discrepancy_kms_alias  = "${var.psc_discrepancy_kms_alias}"
}

module "security-group" {
  source                    = "./module-security-group"
  vpc_id                    = "${lookup(var.vpc_id, var.aws_region)}"
  environment               = "${var.environment}"
  project_name              = "${var.project_name}"
} 

module "lambda" {
    source                   = "./module-lambda"
    project_name             = "${var.project_name}"
    handler                  = "${var.handler}"
    memory_megabytes         = "${var.memory_megabytes}"
    runtime                  = "${var.runtime}"
    timeout_seconds          = "${var.timeout_seconds}"
    psc_discrepancy_bucket   = "${var.psc_discrepancy_bucket}"
    security_group_ids       = "${module.security-group.lambda_into_vpc_id}"
    release_version          = "${var.release_version}"
    release_bucket_name      = "${var.release_bucket_name}"
    execution_role           = "${module.lambda-roles.execution_role}"
    subnet_ids               = "${data.terraform_remote_state.networks.outputs.application_ids}"
}

module "lambda-roles" {
    source                   = "./module-lambda-roles"
    project_name             = "${var.project_name}"
    psc_discrepancy_bucket   = "${var.psc_discrepancy_bucket}"
}

module "ses" {
    source                          = "./module-ses"
    psc_discrepancy_bucket          = "${var.psc_discrepancy_bucket}"
    psc_discrepancy_bucket_prefix   = "${var.psc_discrepancy_bucket_prefix}"
    psc_email_recipient             = "${var.psc_email_recipient}"
    s3_bucket_kms_arn               = "${module.s3.s3_bucket_kms_arn}"
    rule_set_name                   = "${var.rule_set_name}"
}