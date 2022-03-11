package io.cloudflight.license.spdx

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class SpdxLicensesTest {

    @Test
    fun findLicense() {
        assertEquals("BSD Zero Clause License", SpdxLicenses.getById("0BSD")?.name)
    }

    @Test
    fun missingLicense() {
        assertNull(SpdxLicenses.getById("ANY"))
    }

    @Test
    fun findLicenseByDescription() {
        val license = SpdxLicenses.getByDescription("The Apache Software License, Version 2.0")
        assertNotNull(license)
        assertEquals("Apache-2.0", license?.licenseId)
    }
}