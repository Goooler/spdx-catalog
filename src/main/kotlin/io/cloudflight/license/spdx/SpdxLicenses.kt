package io.cloudflight.license.spdx

import com.fasterxml.jackson.databind.ObjectMapper

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
            if (map.containsKey(it.name.toLowerCase())) {
                // unfortunately there are some really few entires in licenses.json with duplicate name
                //throw IllegalArgumentException("Duplicate License name ${it.name}")
            } else {
                map[it.name.toLowerCase()] = it
            }
        }
        licenseSynonyms.forEach { entry ->
            val license = licensesById[entry.key]
                ?: throw IllegalArgumentException("Unknown license ${entry.key} in license-synoyms.json")
            entry.value.forEach { syn ->
                if (map.containsKey(syn.toLowerCase())) {
                    //throw IllegalArgumentException("Duplicate License name ${syn}")
                } else {
                    map[syn.toLowerCase()] = license
                }
            }
        }
        licenseByDescription = map.toMap()
    }

    fun getById(licenseId: String): SpdxLicense? {
        return licensesById[licenseId]
    }

    fun getByDescription(description: String): SpdxLicense? {
        return licenseByDescription[description.toLowerCase()]
    }
}
