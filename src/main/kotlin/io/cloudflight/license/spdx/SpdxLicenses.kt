package io.cloudflight.license.spdx

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.util.*

/**
 * Singleton object which parses the SPDX catalog and provides all data in a structured way.
 *
 * @see [SpdxLicenses.findById]
 * @see [SpdxLicenses.findByName]
 * @see [SpdxLicenses.findByUrl]
 * @see [SpdxLicenses.findLicense]
 *
 * @author Klaus Lehner, Cloudflight
 */
object SpdxLicenses {
    private val licenseFile: SpdxLicenseFile = Json.decodeFromString(SpdxLicenseFile.serializer(),
        SpdxLicenseFile::class.java.classLoader.getResourceAsStream("licenses/spdx/licenses.json")!!
            .use { it.reader().readText() })

    private val licenseSynonyms: LicenseMappings = Json.decodeFromString(LicenseMappings.serializer(),
        LicenseMappings::class.java.classLoader.getResourceAsStream("licenses/spdx/license-synonyms.json")!!
            .use { it.reader().readText() })

    private val licensesById = licenseFile.licenses.associateBy { it.licenseId }
    private val licenseByName: Map<String, SpdxLicense>
    private val licenseByUrl: Map<String, SpdxLicense>

    @Serializable
    private class LicenseMappings(
        @SerialName("idToName") val idToName: Map<String, List<String>>,
        @SerialName("idToUrl") val idToUrl: Map<String, List<String>>
    )

    private fun String.removeProtocol(): String {
        return removePrefix("https://").removePrefix("http://")
    }

    init {
        val mapByName = mutableMapOf<String, SpdxLicense>()
        val mapByUrl = mutableMapOf<String, SpdxLicense>()

        licenseFile.licenses.forEach {
            if (mapByName.containsKey(it.name.lowercase())) {
                // unfortunately there are some really few entires in licenses.json with duplicate name
                //throw IllegalArgumentException("Duplicate License name ${it.name}")
            } else {
                mapByName[it.name.lowercase(Locale.getDefault())] = it
            }
            if (mapByUrl.containsKey(it.detailsUrl.removeProtocol())) {
                throw IllegalArgumentException("Duplicate License URL name ${it.name}")
            } else {
                mapByUrl[it.detailsUrl.removeProtocol()] = it
            }
        }
        licenseSynonyms.idToName.forEach { entry ->
            val license = licensesById[entry.key]
                ?: throw IllegalArgumentException("Unknown license ${entry.key} in license-synonyms.json")
            entry.value.forEach { syn ->
                if (mapByName.containsKey(syn.lowercase())) {
                    //throw IllegalArgumentException("Duplicate License name ${syn}")
                } else {
                    mapByName[syn.lowercase()] = license
                }
            }
        }
        licenseSynonyms.idToUrl.forEach { entry ->
            val license = licensesById[entry.key]
                ?: throw IllegalArgumentException("Unknown license ${entry.key} in license-synonyms.json")
            entry.value.forEach { syn ->
                if (mapByUrl.containsKey(syn.removeProtocol())) {
                    throw IllegalArgumentException("Duplicate License name ${syn}")
                } else {
                    mapByUrl[syn.removeProtocol()] = license
                }
            }
        }
        licenseByName = mapByName.toMap()
        licenseByUrl = mapByUrl.toMap()
    }

    /**
     * Access a [SpdxLicense] by the given [licenseId]
     *
     * @return `null` if the license does not exist
     */
    fun findById(licenseId: String): SpdxLicense? {
        return licensesById[licenseId]
    }

    /**
     * Access a [SpdxLicense] by the given [name]
     *
     * @return `null` if the license does not exist
     */
    fun findByName(name: String): SpdxLicense? {
        return licenseByName[name.lowercase()]
    }

    /**
     * Access a [SpdxLicense] by the given [url]
     *
     * @return `null` if the license does not exist
     */
    fun findByUrl(url: String): SpdxLicense? {
        return licenseByUrl[url.removeProtocol()]
    }

    /**
     * Searches a license given the attached [LicenseQuery] in the following order:
     * - first we check [LicenseQuery.id]
     * - then, if ID could not be found, we check [LicenseQuery.url]
     * - and last, we search for  [LicenseQuery.name]
     *
     * @return `null` if no license could be found
     */
    fun findLicense(query: LicenseQuery): SpdxLicense? {
        if (query.id != null) {
            val l = findById(query.id)
            if (l != null) {
                return l
            }
        }
        if (query.url != null) {
            val l = findByUrl(query.url)
            if (l != null) {
                return l
            }
        }
        if (query.name != null) {
            val l = findByName(query.name)
            if (l != null) {
                return l
            }
        }
        return null
    }
}

data class LicenseQuery(
    val id: String? = null,
    val url: String? = null,
    val name: String? = null
)
