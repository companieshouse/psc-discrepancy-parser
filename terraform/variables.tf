variable aws_region {
    default = "eu-west-1"
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
