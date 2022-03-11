 # SPDX Catalog

Bundles the latest version of the [SPDX Licence List](https://spdx.org/licenses/) in a JSON forma
as provided [here](https://github.com/spdx/license-list-data/tree/master/json).

Additionaly, it comes with a small [Kotlin](https://kotlinlang.org/) library powered by Jackson ObjectMapper
which provides a convenient and typed access to that catalog.

For example:

````kotlin
import io.cloudflight.license.spdx.SpdxLicenses

println(SpdxLicenses.getById("0BSD"))

val license = SpdxLicenses.getByDescription("The Apache Software License, Version 2.0")
println(license?.licenseId)
````

will print `BSD Zero Clause License` 

and

````kotlin
import io.cloudflight.license.spdx.SpdxLicenses

val license = SpdxLicenses.getByDescription("The Apache Software License, Version 2.0")
println(license?.licenseId)
````

will print `Apache-2.0`.

The library also normalizes different variants of license description names, therefore also the following code works:

````kotlin
import io.cloudflight.license.spdx.SpdxLicenses

val license = SpdxLicenses.getByDescription("Apache License v2.0")
println(license?.licenseId)
````

This will print `Apache-2.0` as well.

## How to contribute

Always keep the version of that [SPDX list](https://github.com/spdx/license-list-data/tree/master/json) in sync with the version of this module.
