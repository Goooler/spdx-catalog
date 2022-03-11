package io.cloudflight.license.spdx

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

class SpdxLicenseFile @JsonCreator constructor(
    @JsonProperty("licenseListVersion") val licenseListVersion: String,
    @JsonProperty("releaseDate") val releaseDate: String,
    @JsonProperty("licenses") val licenses: List<SpdxLicense>
)

class SpdxLicense @JsonCreator constructor(
    @JsonProperty("reference") val reference: String,
    @JsonProperty("isDeprecatedLicenseId") val isDeprecatedLicenseId: Boolean,
    @JsonProperty("detailsUrl") val detailsUrl: String,
    @JsonProperty("referenceNumber") val referenceNumber: String,
    @JsonProperty("name") val name: String,
    @JsonProperty("licenseId") val licenseId: String,
    @JsonProperty("seeAlso") val seeAlso: List<String>,
    @JsonProperty("isOsiApproved") val isOsiApproved: Boolean,
    @JsonProperty("isFsfLibre") val isFsfLibre: Boolean?
)