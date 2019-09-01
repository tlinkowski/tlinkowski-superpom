# Releases

## Version 0.3.0

Release date: 01/09/2019.

### Enhancements

-   [#68](https://github.com/tlinkowski/tlinkowski-superpom/issues/68): Automatic module name validation
-   [#67](https://github.com/tlinkowski/tlinkowski-superpom/issues/67): Update dependencies

---

## Version 0.2.0

Release date: 30/08/2019.

### Enhancements

-   [#65](https://github.com/tlinkowski/tlinkowski-superpom/issues/65): Enforce project name = module name convention
-   [#64](https://github.com/tlinkowski/tlinkowski-superpom/issues/64): Change artifact names to module names (BREAKING CHANGE)
-   [#61](https://github.com/tlinkowski/tlinkowski-superpom/issues/61): Use Java Library Gradle Plugin
-   [#59](https://github.com/tlinkowski/tlinkowski-superpom/issues/59): Support for superpom.automaticModuleName
-   [#56](https://github.com/tlinkowski/tlinkowski-superpom/issues/56): Update dependencies
-   [#55](https://github.com/tlinkowski/tlinkowski-superpom/issues/55): Support for superpom.isTestProject
-   [#54](https://github.com/tlinkowski/tlinkowski-superpom/issues/54): Add shared per-subproject `superpom` extension
-   [#17](https://github.com/tlinkowski/tlinkowski-superpom/issues/17): Add full Lombok support

### Bug Fixes

-   [#66](https://github.com/tlinkowski/tlinkowski-superpom/issues/66): MySettingsGradlePlugin fails if there's a file in `subprojects` directory
-   [#60](https://github.com/tlinkowski/tlinkowski-superpom/issues/60): Signing commits on JGit fails
-   [#44](https://github.com/tlinkowski/tlinkowski-superpom/issues/44): Sync to Maven Central didn't work for 0.1.0

---

## Version 0.1.4

Release date: 15/08/2019.

### Bug Fixes

-   [#53](https://github.com/tlinkowski/tlinkowski-superpom/issues/53): Release fails for a project in which some subprojects are not published

---

## Version 0.1.3

Release date: 12/08/2019.

### Bug Fixes

-   [#52](https://github.com/tlinkowski/tlinkowski-superpom/issues/52): JavaDocs missing in 0.1.2 (present in 0.1.0)

---

## Version 0.1.2

Release date: 11/08/2019.

### Bug Fixes

-   [#50](https://github.com/tlinkowski/tlinkowski-superpom/issues/50): Can't sync Gradle marker publications to Maven Central

---

## Version 0.1.1

Release date: 09/08/2019.

### Bug Fixes

-   [#49](https://github.com/tlinkowski/tlinkowski-superpom/issues/49): Dependency updates plugin reports Gradle release candidates
-   [#48](https://github.com/tlinkowski/tlinkowski-superpom/issues/48): Gren keeps only the last release in CHANGELOG.md
-   [#47](https://github.com/tlinkowski/tlinkowski-superpom/issues/47): Append missing issues for changelog and release notes of 0.1.0
-   [#46](https://github.com/tlinkowski/tlinkowski-superpom/issues/46): Remove redundant Manifest entries
-   [#45](https://github.com/tlinkowski/tlinkowski-superpom/issues/45): Missing Gradle plugin marker artifacts in Bintray

---

## Version 0.1.0

Release date: 07/08/2019.

### Enhancements

-   [#38](https://github.com/tlinkowski/tlinkowski-superpom/issues/38): Remove redundant plugins (especially Kordamp)
-   [#34](https://github.com/tlinkowski/tlinkowski-superpom/issues/34): Introduce buildSrc
-   [#32](https://github.com/tlinkowski/tlinkowski-superpom/issues/32): Configure one-click releasing
-   [#29](https://github.com/tlinkowski/tlinkowski-superpom/issues/29): Configure AppVeyor to test the build on Windows
-   [#26](https://github.com/tlinkowski/tlinkowski-superpom/issues/26): Smoke test for my-settings-gradle-plugin
-   [#25](https://github.com/tlinkowski/tlinkowski-superpom/issues/25): Rename plugins and packages
-   [#24](https://github.com/tlinkowski/tlinkowski-superpom/issues/24): Configure Kotlin to be always available in test code
-   [#23](https://github.com/tlinkowski/tlinkowski-superpom/issues/23): Come back to a multi-project layout (two subprojects)
-   [#21](https://github.com/tlinkowski/tlinkowski-superpom/issues/21): Mechanism that will allow to specify superpom version only once
-   [#20](https://github.com/tlinkowski/tlinkowski-superpom/issues/20): Configure JaCoCo for GradleRunner
-   [#19](https://github.com/tlinkowski/tlinkowski-superpom/issues/19): Copy all dependency version from gradle.properties to the extra properties of the target project
-   [#16](https://github.com/tlinkowski/tlinkowski-superpom/issues/16): Configure dependencyUpdates
-   [#15](https://github.com/tlinkowski/tlinkowski-superpom/issues/15): Publish tlinkowski-superpom-settings plugin
-   [#14](https://github.com/tlinkowski/tlinkowski-superpom/issues/14): Configure publishing to Bintray + Maven Central
-   [#13](https://github.com/tlinkowski/tlinkowski-superpom/issues/13): Complete README.md
-   [#12](https://github.com/tlinkowski/tlinkowski-superpom/issues/12): Add a module-info.class
-   [#11](https://github.com/tlinkowski/tlinkowski-superpom/issues/11): Configure code coverage
-   [#10](https://github.com/tlinkowski/tlinkowski-superpom/issues/10): Switch to a single-project build to make things easier
-   [#9](https://github.com/tlinkowski/tlinkowski-superpom/issues/9): Configure test logging for all events
-   [#8](https://github.com/tlinkowski/tlinkowski-superpom/issues/8): Add support for publishing to Mavel local
-   [#7](https://github.com/tlinkowski/tlinkowski-superpom/issues/7): Add support for transfering IntelliJ settings using the plugin
-   [#6](https://github.com/tlinkowski/tlinkowski-superpom/issues/6): Remove redundancy in shared.build.gradle.kts and TLinkowskiSuperpomPlugin
-   [#5](https://github.com/tlinkowski/tlinkowski-superpom/issues/5): Apply more Kordamp plugins as necessary
-   [#3](https://github.com/tlinkowski/tlinkowski-superpom/issues/3): Add basic shared Gradle config
-   [#2](https://github.com/tlinkowski/tlinkowski-superpom/issues/2): Add tests using Gradle TestKit
-   [#1](https://github.com/tlinkowski/tlinkowski-superpom/issues/1): Initial project configuration

### Bug Fixes

-   [#43](https://github.com/tlinkowski/tlinkowski-superpom/issues/43): No coverage report found by codecov-python
-   [#42](https://github.com/tlinkowski/tlinkowski-superpom/issues/42): Smoke test fails on dry-run Git push
-   [#41](https://github.com/tlinkowski/tlinkowski-superpom/issues/41): Dirty repo in "gradle release" smoke test on CI
-   [#40](https://github.com/tlinkowski/tlinkowski-superpom/issues/40): Make Travis and AppVeyor print/upload failed Gradle TestKit test details
-   [#39](https://github.com/tlinkowski/tlinkowski-superpom/issues/39): Task publishToMavenLocal fails
-   [#36](https://github.com/tlinkowski/tlinkowski-superpom/issues/36): Warning about unsupported Kotlin plugin version
-   [#35](https://github.com/tlinkowski/tlinkowski-superpom/issues/35): File sharing doesn't work when trying to export a file in root directory
-   [#31](https://github.com/tlinkowski/tlinkowski-superpom/issues/31): Windows build failed again due to `test.exec` lock
-   [#30](https://github.com/tlinkowski/tlinkowski-superpom/issues/30): Javadoc error: "No public or protected classes found to document" for a Kotlin modularized project
-   [#28](https://github.com/tlinkowski/tlinkowski-superpom/issues/28): Work around too eager modification of `compileJava.destinationDir` by Java Modularity Plugin
-   [#27](https://github.com/tlinkowski/tlinkowski-superpom/issues/27): The code being documented uses modules but the packages defined in Guava are in the unnamed module.
-   [#22](https://github.com/tlinkowski/tlinkowski-superpom/issues/22): :project1:test in sample-project reports NO-SOURCE
