variable aws_region {
    default = "eu-west-2"
}

variable aws_ses_region {
    default = "eu-west-1"
}

variable networks_state_bucket {}
variable state_prefix {}
variable deploy_to {}
variable aws_bucket {}

# These vpcs are configured for development. Preprod and prod are overridden in the vars file.
variable "vpc_id" {
  default = {
    eu-west-1 = "vpc-83c425e7" #Ireland
    eu-west-2 = "vpc-074ff55ed5182e144" #London
  }
}

variable handler {
    default = "handler.Handler::handleRequest"
}

variable memory_megabytes {
    default = "512"
}

variable "runtime" {
    default = "java8"
}

variable timeout_seconds {
    default = "60"
}

variable project_name {
    default = "psc-discrepancy-parser"
}

variable psc_discrepancy_bucket {}

variable release_version {}

variable release_bucket_name {
    default = "release.ch.gov.uk"
}

variable psc_email_recipient {}
variable psc_discrepancy_bucket_prefix {}

variable environment {}

variable rule_set_name {}

variable chips_rest_interface_endpoint {}
  
variable workspace_key_prefix {}

variable state_file_name {}
