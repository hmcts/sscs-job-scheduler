variable "product" {
  type    = "string"
}

variable "microservice" {
  type    = "string"
}

variable "location" {
  type    = "string"
  default = "UK South"
}

variable "location_db" {
  type    = "string"
  default = "West Europe"
}

variable "env" {
  type    = "string"
}

variable "ilbIp" {}

variable "component" {}

variable "tenant_id" {}

variable "client_id" {}

variable "subscription" {}

variable "jenkins_AAD_objectId" {
  type        = "string"
}

variable "idam_s2s_auth_microservice" {
  default = "sscs"
}

variable "infrastructure_env" {
  default = "dev"
}