package io.cloudflight.license.spdx

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal class SpdxLicenseFile constructor(
    @SerialName("licenseListVersion") val licenseListVersion: String,
    @SerialName("releaseDate") val releaseDate: String,
    @SerialName("licenses") val licenses: List<SpdxLicense>
)

@Serializable
data class SpdxLicense(
    @SerialName("reference") val reference: String,
    @SerialName("isDeprecatedLicenseId") val isDeprecatedLicenseId: Boolean,
    @SerialName("detailsUrl") val detailsUrl: String,
    @SerialName("referenceNumber") val referenceNumber: Int,
    @SerialName("name") val name: String,
    @SerialName("licenseId") val licenseId: String,
    @SerialName("seeAlso") val seeAlso: List<String>,
    @SerialName("isOsiApproved") val isOsiApproved: Boolean,
    @SerialName("isFsfLibre") val isFsfLibre: Boolean? = null
)