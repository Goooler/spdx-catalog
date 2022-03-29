package io.cloudflight.license.spdx

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Bundles all fields of a license as per https://github.com/spdx/license-list-XML/blob/master/DOCS/license-fields.md
 *
 * @author Klaus Lehner, Cloudflight
 */
@Serializable
data class SpdxLicense(
    /**
     * - Short identifier to be used to identify a license or exception match to licenses or exceptions in the context of an SPDX file, a source file, or elsewhere
     * - Short identifiers consist of ASCII letters (A-Za-z), digits (0-9), full stops (.) and hyphen or minus signs (-)
     * - Short identifiers consist of an abbreviation based on a common short name or acronym for the license or exception
     * - Where applicable, the abbreviation will be followed by a dash and then the version number, in X.Y format
     * - Where applicable, and if possible, the short identifier should be harmonized with other well-known open source naming sources (i.e., OSI, Fedora, etc.)
     * - Short identifiers should be as short in length as possible while staying consistent with all other naming criteria
     *
     * @return the short identifier of the license
     */
    @SerialName("licenseId") val licenseId: String,
    /**
     * - The full name may omit certain word, such as "the," for alphabetical sorting purposes
     * - No commas in full name of license or exception
     * - The word "version" is not spelled out; "v" or nothing is used to indicate license version (for space reasons)
     * - For version, use lower case v and no period or space between v and the number
     * - No abbreviations are included (in parenthesis) after the full name
     *
     * @return the full name of the license
     */
    @SerialName("name") val name: String,
    /**
     * The URL to the full license
     */
    @SerialName("reference") val reference: String,

    /**
     * - Include URL for the official text of the license or exception
     * - If the license is OSI approved, also include URL for OSI license page
     * - Include another URL that has text version of license, if neither of the first two options are available
     * - Note that the source URL may refer to an original URL for the license which is no longer active. We don't remove inactive URLs. New or best available URLs may be added.
     * - Link to the license or exception in its native language is used where specified (e.g. French for CeCILL). Link to English version where multiple, equivalent official translations (e.g. EUPL)
     */
    @SerialName("seeAlso") val seeAlso: List<String>,
    @SerialName("isDeprecatedLicenseId") val isDeprecatedLicenseId: Boolean,
    /**
     * The URL to the details of the license in JSON format
     */
    @SerialName("detailsUrl") val detailsUrl: String,
    @SerialName("referenceNumber") val referenceNumber: Int,
    /**
     * @return If the license is [OSI-approved](https://opensource.org/licenses)
     */
    @SerialName("isOsiApproved") val isOsiApproved: Boolean,
    /**
     * @return If the license is [listed as free by the FSF](https://www.gnu.org/licenses/license-list.en.html), this field will return `true`.
     * If the license is listed as not free by the FSF, the field will indicate `false`.
     * Otherwise, the field will be left blank.
     */
    @SerialName("isFsfLibre") val isFsfLibre: Boolean? = null
)