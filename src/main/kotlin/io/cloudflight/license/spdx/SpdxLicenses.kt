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
            if (!mapByName.containsKey(it.name.lowercase())) {
                mapByName[it.name.lowercase(Locale.getDefault())] = it
            }
            addUrl(mapByUrl, it.detailsUrl, it)
            it.seeAlso.forEach { url ->
                addUrl(mapByUrl, url, it)
            }
        }
        licenseSynonyms.idToName.forEach { entry ->
            val license = licensesById[entry.key]
                ?: throw IllegalArgumentException("Unknown license ${entry.key} in license-synonyms.json")
            entry.value.forEach { syn ->
                if (!mapByName.containsKey(syn.lowercase())) {
                    mapByName[syn.lowercase()] = license
                }
            }
        }
        licenseSynonyms.idToUrl.forEach { entry ->
            val license = licensesById[entry.key]
                ?: throw IllegalArgumentException("Unknown license ${entry.key} in license-synonyms.json")
            entry.value.forEach { syn ->
                if (mapByUrl.containsKey(syn.removeProtocol())) {
                    throw IllegalArgumentException("Duplicate License URL ${syn}")
                } else {
                    mapByUrl[syn.removeProtocol()] = license
                }
            }
        }
        licenseByName = mapByName.toMap()
        licenseByUrl = mapByUrl.toMap()
    }

    private fun addUrl(
        mapByUrl: MutableMap<String, SpdxLicense>,
        url: String,
        it: SpdxLicense
    ) {
        if (mapByUrl.containsKey(url.removeProtocol())) {
            println(
                "Duplicate License URL ${url.removeProtocol()} for ${it.licenseId} already exists for ${
                    mapByUrl.getValue(url.removeProtocol()).licenseId
                }"
            )
        } else {
            mapByUrl[url.removeProtocol()] = it
        }
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
     * This query can be seen as an `OR` query, and the first match wins.
     *
     * @return `null` if no license could be found
     */
    fun findLicense(query: LicenseQuery): SpdxLicense? {
        query.id?.let { findById(it)?.let { l-> return l } }
        query.url?.let { findByUrl(it)?.let { l-> return l } }
        query.name?.let { findByName(it)?.let { l-> return l } }
        return null
    }
}

data class LicenseQuery(
    val id: String? = null,
    val url: String? = null,
    val name: String? = null
)
