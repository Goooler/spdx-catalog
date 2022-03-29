package io.cloudflight.license.spdx

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class SpdxLicensesTest {

    @Test
    fun findLicense() {
        assertEquals("BSD Zero Clause License", SpdxLicenses.findById("0BSD")?.name)
    }

    @Test
    fun missingLicense() {
        assertNull(SpdxLicenses.findById("ANY"))
    }

    @Test
    fun findLicenseByDescription() {
        val license = SpdxLicenses.findByName("The Apache Software License, Version 2.0")
        assertNotNull(license)
        assertEquals("Apache-2.0", license?.licenseId)
    }

    @Test
    fun findLicenseByHttpUrl() {
        val license = SpdxLicenses.findByUrl("http://www.apache.org/licenses/LICENSE-2.0.txt")
        assertNotNull(license)
        assertEquals("Apache-2.0", license?.licenseId)
    }

    @Test
    fun findLicenseByHttpsUrl() {
        val license = SpdxLicenses.findByUrl("https://www.apache.org/licenses/LICENSE-2.0.txt")
        assertNotNull(license)
        assertEquals("Apache-2.0", license?.licenseId)
    }

    @Test
    fun findByQuery_findUrl() {
        val license = SpdxLicenses.findLicense(
            LicenseQuery(
                name = "The Apache Software License",
                url = "https://www.apache.org/licenses/LICENSE-2.0.txt"
            )
        )
        assertNotNull(license)
        assertEquals("Apache-2.0", license?.licenseId)
    }

    @Test
    fun findByQuery_findId() {
        val license = SpdxLicenses.findLicense(
            LicenseQuery(
                id = "Apache-2.0",
                name = "something",
                url = "something else"
            )
        )
        assertNotNull(license)
        assertEquals("Apache-2.0", license?.licenseId)
    }

    @Test
    fun checkMIT() {
        val license = SpdxLicenses.findByUrl("http://opensource.org/licenses/MIT")
        assertNotNull(license)
        assertEquals("MIT", license?.licenseId)
    }

    @Test
    fun order() {
        val license = SpdxLicenses.findByUrl("https://opensource.org/licenses/MPL-2.0")
        assertEquals("MPL-2.0", license?.licenseId)
    }

    @Test
    fun findByMainId() {
        val license = SpdxLicenses.findByUrl("https://spdx.org/licenses/GD.html")
        assertEquals("GD", license?.licenseId)
    }
}