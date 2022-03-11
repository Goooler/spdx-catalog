package io.cloudflight.license.spdx

import com.fasterxml.jackson.databind.ObjectMapper
import java.util.*

/**
 * Singleton object which parses the SPDX catalog and provides all data in a structured way.
 *
 * @see [SpdxLicenses.getById]
 * @see [SpdxLicenses.getByDescription]
 *
 * @author Klaus Lehner, Cloudflight
 */
object SpdxLicenses {
    private val objectMapper = ObjectMapper()
    private val licenseFile = objectMapper.readValue(
        // https://github.com/spdx/license-list-data/tree/master/json
        SpdxLicenseFile::class.java.classLoader.getResourceAsStream("licenses/spdx/licenses.json"),
        SpdxLicenseFile::class.java
    )
    private val licensesById = licenseFile.licenses.associateBy { it.licenseId }
    private val licenseByDescription: Map<String, SpdxLicense>

    init {
        val map = mutableMapOf<String, SpdxLicense>()
        val licenseSynonyms = objectMapper.readValue(
            SpdxLicenseFile::class.java.classLoader.getResourceAsStream("licenses/spdx/license-synonyms.json"),
            object : com.fasterxml.jackson.core.type.TypeReference<Map<String, List<String>>>() {}
        )

        licenseFile.licenses.forEach {
            if (map.containsKey(it.name.lowercase())) {
                // unfortunately there are some really few entires in licenses.json with duplicate name
                //throw IllegalArgumentException("Duplicate License name ${it.name}")
            } else {
                map[it.name.lowercase(Locale.getDefault())] = it
            }
        }
        licenseSynonyms.forEach { entry ->
            val license = licensesById[entry.key]
                ?: throw IllegalArgumentException("Unknown license ${entry.key} in license-synoyms.json")
            entry.value.forEach { syn ->
                if (map.containsKey(syn.lowercase())) {
                    //throw IllegalArgumentException("Duplicate License name ${syn}")
                } else {
                    map[syn.lowercase()] = license
                }
            }
        }
        licenseByDescription = map.toMap()
    }

    /**
     * Access a [SpdxLicense] by the given [licenseId]
     *
     * @return `null` if the license does not exist
     */
    fun getById(licenseId: String): SpdxLicense? {
        return licensesById[licenseId]
    }

    /**
     * Access a [SpdxLicense] by the given [description]
     *
     * @return `null` if the license does not exist
     */
    fun getByDescription(description: String): SpdxLicense? {
        return licenseByDescription[description.lowercase()]
    }
}
