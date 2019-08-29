# Gradle SuperPOM of Tomasz Linkowski

[![Build (Linux)](https://img.shields.io/travis/com/tlinkowski/tlinkowski-superpom/master.svg?logo=linux)](https://travis-ci.com/tlinkowski/tlinkowski-superpom)
[![Build (Windows)](https://img.shields.io/appveyor/ci/tlinkowski/tlinkowski-superpom/master.svg?logo=windows)](https://ci.appveyor.com/project/tlinkowski/tlinkowski-superpom/branch/master)
[![Code coverage](https://img.shields.io/codecov/c/github/tlinkowski/tlinkowski-superpom.svg)](https://codecov.io/gh/tlinkowski/tlinkowski-superpom)
[![Codacy grade](https://img.shields.io/codacy/grade/81a0cef956a34083accd2f8e401a66de.svg)](https://app.codacy.com/project/tlinkowski/tlinkowski-superpom/dashboard)

[![Maven Central](https://img.shields.io/maven-central/v/pl.tlinkowski.gradle.my/pl.tlinkowski.gradle.my.superpom?label=Maven%20Central)](https://search.maven.org/search?q=g:pl.tlinkowski.gradle.my)
[![Javadocs](https://javadoc.io/badge/pl.tlinkowski.gradle.my/pl.tlinkowski.gradle.my.superpom.svg?color=blue)](https://javadoc.io/doc/pl.tlinkowski.gradle.my/pl.tlinkowski.gradle.my.superpom)
[![Semantic Versioning](https://img.shields.io/badge/-semantic%20versioning-333333)](https://semver.org/)
[![Automated Release Notes by gren](https://img.shields.io/badge/%F0%9F%A4%96-release%20notes-00B2EE.svg)](https://github-tools.github.io/github-release-notes/)

This project is inspired by [The Gradle SuperPOM](http://andresalmiray.com/the-gradle-superpom/) post by
[Andres Almiray](https://twitter.com/aalmiray).

This projects provides two plugins:

1.  A Gradle [`Project`](https://docs.gradle.org/current/javadoc/org/gradle/api/Project.html)
    plugin (id: [`pl.tlinkowski.gradle.my.superpom`](subprojects/pl.tlinkowski.gradle.my.superpom))

2.  A Gradle [`Settings`](https://docs.gradle.org/current/javadoc/org/gradle/api/initialization/Settings.html)
    plugin (id: [`pl.tlinkowski.gradle.my.settings`](subprojects/pl.tlinkowski.gradle.my.settings))

Together, those two plugins preconfigure Gradle builds for each of my projects.

## Usage

### by Tomasz Linkowski

`gradle.properties`:

```properties
# Release scopes: [major, minor, patch]
reckon.scope=minor
# Dependencies
mySuperpomVersion=x.y.z
```

`settings.gradle.kts`:

```kotlin
buildscript {
  repositories {
    mavenCentral()
  }
  dependencies {
    val mySuperpomVersion: String by settings
    classpath(group = "pl.tlinkowski.gradle.my", name = "pl.tlinkowski.gradle.my.settings", version = mySuperpomVersion)
  }
}
apply(plugin = "pl.tlinkowski.gradle.my.settings")
```

`build.gradle.kts`:

```kotlin
plugins {
  id("pl.tlinkowski.gradle.my.superpom")
}
```

For a complete usage example, see [sample-project](test-data/sample-project).

### by others

If you like what this plugin does, you can:

1.  [Fork](https://github.com/tlinkowski/tlinkowski-superpom/fork) this project.
2.  Change data related to Tomasz Linkowski to match your person / organization (especially classes with `My` prefix).
3.  Set up your Bintray and Maven Central accounts.
4.  Release **your own** version of the Gradle Settings & SuperPOM plugin.

## Features

### Settings Plugin (id: `pl.tlinkowski.gradle.my.settings`)

Configures:

1.  plugin management:

    -   Maven Central repository for `pl.tlinkowski.gradle.my.superpom` (this plugin is not deployed to Gradle Plugin
        Portal as it's not a general-use plugin)

    -   automatic version resolution for `pl.tlinkowski.gradle.my.superpom`
        (using `mySuperpomVersion` property in `gradle.properties`)

2.  project structure (inspired by [Kordamp project structure](https://aalmiray.github.io/kordamp-gradle-plugins/#_project_structure)):

    -   subprojects in `subprojects` directory
    -   build file names changed from `build.gradle.kts` to `<subproject-name>.gradle.kts`

### Project Plugin (id: `pl.tlinkowski.gradle.my.superpom`)

#### Project Preconfiguration

This is the basic feature described by Andres Almiray in [his post](http://andresalmiray.com/the-gradle-superpom/).

This SuperPOM plugin can be applied to the **root** project only, and it does the following:

1.  for all projects:

    -   applies: [`idea`](https://docs.gradle.org/current/userguide/idea_plugin.html) plugin

    -   configures: Maven Central repository

2.  for the root project:

    -   applies:

        -   [`org.kordamp.gradle.project`](https://aalmiray.github.io/kordamp-gradle-plugins/#_org_kordamp_gradle_project) plugin
        -   [`org.kordamp.gradle.bintray`](https://aalmiray.github.io/kordamp-gradle-plugins/#_org_kordamp_gradle_bintray) plugin
        -   [`org.ajoberstar.grgit`](https://github.com/ajoberstar/grgit) plugin
        -   [`org.ajoberstar.reckon`](https://github.com/ajoberstar/reckon) plugin
        -   [`com.github.ben-manes.versions`](https://github.com/ben-manes/gradle-versions-plugin) plugin

    -   configures:

        -   main project properties using
            [Kordamp DSL](https://aalmiray.github.io/kordamp-gradle-plugins/#_org_kordamp_gradle_base_dsl)

        -   shared file import tasks (see [direct file sharing](#direct-file-sharing))

        -   `SNAPSHOT`/`FINAL` release stages for [reckon](https://github.com/ajoberstar/reckon)

        -   a [comprehensive release process](#comprehensive-release-process)

        -   [dependency updates](https://github.com/ben-manes/gradle-versions-plugin): skipping Release Candidates

3.  for subprojects:

    -   applies:

        -   [`org.javamodularity.moduleplugin`](https://github.com/java9-modularity/gradle-modules-plugin) plugin
            (for JPMS support)

        -   [`groovy`](https://docs.gradle.org/current/userguide/groovy_plugin.html) plugin
            (for test code: [Spock](http://spockframework.org/))

        -   [`kotlin("jvm")`](https://kotlinlang.org/docs/reference/using-gradle.html) plugin
            (for test code: custom helpers)

    -   configures:

        -   logging of test events

        -   test dependencies on Kotlin, Groovy, and [Spock](http://spockframework.org/)

        -   [running tests on classpath](https://github.com/java9-modularity/gradle-modules-plugin#fall-back-to-classpath-mode)
            (necessary as Groovy isn't JPMS-compatible)

        -   `compileTestGroovy` dependency on `compileTestKotlin` (so that Spock can access Kotlin helpers)

        -   minimum line code coverage = **95%** ([JaCoCo](https://www.jacoco.org/jacoco/))

        -   project name and module name validation (see [Naming Convention](#naming-convention))
        
        -   [`Automatic-Module-Name`](https://docs.oracle.com/en/java/javase/12/docs/specs/jar/jar.html#modular-jar-files)
            equal to `project.name` (if `module-info.java` is absent)

        -   publishing to JCenter and Maven Central

#### Comprehensive Release Process

This plugin configures a comprehensive release process by:
 
-   exposing `release` Gradle task (which serves as the root of a complex task chain)

-   providing [shared](#direct-file-sharing) `release.bat` script
    (which simply calls `gradle clean` followed by `gradle release -Preckon.stage=final`)

The comprehensive release process is configured by
[MyComprehensiveReleaseConfigurator](subprojects/pl.tlinkowski.gradle.my.superpom/src/main/kotlin/pl/tlinkowski/gradle/my/superpom/shared/internal/configurator/MyComprehensiveReleaseConfigurator.kt)
and includes:

1.  Release validation (requirements: clean repo, pushed `master` branch, `reckon.stage=final` property)
2.  Full clean build (to make 100% sure we can release)
3.  Confirmations to make sure the release is going fine
4.  Changelog generation (by [gren](https://github.com/github-tools/github-release-notes), requires Node.js)
5.  Tagging the release in Git
6.  Publishing to GitHub (by [gren](https://github.com/github-tools/github-release-notes), requires Node.js)
7.  Publishing to central repos (JCenter & Maven Central)
8.  Post-release reset of the release scope for [reckon](https://github.com/ajoberstar/reckon) in `gradle.properties`

This is how a Gradle build log of such a release process looks:

```text
> Task :validateReleasePossible        // 1

> Task :<subproject-1>:[...]           // 2
> Task :<subproject-1>:build           // 2

> Task :<subproject-2>:[...]           // 2
> Task :<subproject-2>:build           // 2

> Task :confirmReleaseProcessLaunch    // 3
=== Do you want to begin the release process for version 0.1.0 of 'sample-project'? [y/N] ===
y

> Task :addTemporaryVersionTag         // 4 (required by gren)
> Task :generateChangelog              // 4 (gren)
> Task :removeTemporaryVersionTag      // 4 (no longer needed)

> Task :confirmChangelogPush           // 3
=== Do you want to push the updated CHANGELOG.md and continue with the release process? [y/N] ===
y

> Task :pushUpdatedChangelog           // 4
> Task :addFinalVersionTag             // 5

> Task :confirmFinalPublication        // 3
=== Are you SURE you want to publish the code at 0.1.0 tag to GitHub, JCenter & MavenCentral? [y/N] ===
y

> Task :releaseToGitHub                // 6 (gren)

> Task :<subproject-1>:[...]                               // 7
> Task :<subproject-1>:publishMainPublicationToMavenLocal  // 7

> Task :<subproject-2>:[...]                               // 7
> Task :<subproject-2>:publishMainPublicationToMavenLocal  // 7

> Task :injectReleasePasswords         // 7

> Task :<subproject-1>:[...]           // 7
> Task :<subproject-1>:bintrayUpload   // 7

> Task :<subproject-2>:[...]           // 7
> Task :<subproject-2>:bintrayUpload   // 7

> Task :bintrayPublish                 // 7
> Task :releaseToCentralRepos          // 7
> Task :release
> Task :resetScopeInGradleProperties   // 8
> Task :pushUpdatedGradleProperties    // 8
```

Note the
[`injectReleasePasswords`](subprojects/pl.tlinkowski.gradle.my.superpom/src/main/kotlin/pl/tlinkowski/gradle/my/superpom/shared/internal/task/InjectReleasePasswordsTask.kt)
task, which obtains the following passwords for performing a release:

-   `bintrayApiKey`: from Gradle properties (i.e. `~/.gradle/gradle.properties`),
-   `gnupgPassphrase`, `sonatypePassword`: by requesting them in a Swing dialog (not suitable for CI)

Also, note that thanks to [reckon](https://github.com/ajoberstar/reckon) plugin, we don't need to do the classic
"pre-release version bumps". Instead, we:

-   automatically reset the version scope after a release to `patch` (= "post-release version bump")
-   manually change the scope to `minor` or `major` whenever we commit any changes that are in such scope

#### Gradle Configuration Sharing

A large part of the build configuration for:

-   this (*source*) project
    (defined mostly in the included [`buildSrc`](buildSrc) build), and

-   *target* projects
    (defined in [`pl.tlinkowski.gradle.my.superpom`](subprojects/pl.tlinkowski.gradle.my.superpom) plugin project)

is *shared* as [`pl.tlinkowski.gradle.my.superpom.shared`](subprojects/pl.tlinkowski.gradle.my.superpom/src/main/kotlin/pl/tlinkowski/gradle/my/superpom/shared)
package (see [`MyCompleteSharedConfigPlugin`](subprojects/pl.tlinkowski.gradle.my.superpom/src/main/kotlin/pl/tlinkowski/gradle/my/superpom/shared/internal/plugin/MyCompleteSharedConfigPlugin.kt)).

Thanks to this, we don't have to:

-   duplicate large portions of configuration between the *source* and *target* projects, nor

-   apply the previous version of this plugin to itself to avoid the duplication mentioned above
    (as Andres Almiray suggests in [his post](http://andresalmiray.com/the-gradle-superpom/))

    -   such approach would be problematic for [direct file sharing](#direct-file-sharing)

This is achieved by synchronizing the contents of the SuperPOM plugin's
[`shared`](subprojects/pl.tlinkowski.gradle.my.superpom/src/main/kotlin/pl/tlinkowski/gradle/my/superpom/shared) 
package with a corresponding `shared` package in `buildSrc`
(see [`buildSrc/build.gradle.kts`](buildSrc/build.gradle.kts) for details).

#### Gradle Property Sharing

Gradle properties at [`gradle/shared-gradle.properties`](gradle/shared-gradle.properties) are shared by
[`SuperpomSharedFileExportPlugin`](buildSrc/src/main/kotlin/pl/tlinkowski/gradle/my/buildsrc/plugin/SuperpomSharedFileExportPlugin.kt)
(a part of [direct file sharing](#direct-file-sharing) mechanism). Then, these properties are imported by:

-   by [`shared-gradle-properties.gradle.kts`](gradle/shared-gradle-properties.gradle.kts),
    for [`buildSrc`](buildSrc), root [`settings.gradle.kts`](settings.gradle.kts),
    and root [`build.gradle.kts`](build.gradle.kts)

-   by [`SuperpomSharedGradlePropertyImportPlugin`](subprojects/pl.tlinkowski.gradle.my.superpom/src/main/kotlin/pl/tlinkowski/gradle/my/superpom/internal/plugin/SuperpomSharedGradlePropertyImportPlugin.kt),
    for all *target* projects

#### Direct File Sharing

Selected files in this project can be directly exported to projects that apply this SuperPOM plugin. It can be viewed
as a "sync" operation between this (*source*) project and all *target* projects.

The files to be shared are specified in
[`SuperpomFileSharing`](subprojects/pl.tlinkowski.gradle.my.superpom/src/main/kotlin/pl/tlinkowski/gradle/my/superpom/shared/internal/SuperpomFileSharing.kt)
(usually, it's a good idea to git-ignore them in *target* projects). Currently, the following files are shared *directly*:

-   `idea`: parts of IntelliJ configuration from `.idea` directory
    (subdirectories `codeStyles`, `copyright`, `inspectionProfiles`)

-   `release`: files related to releasing, like `release.bat` script and Node.js configuration for
    [gren](https://github.com/github-tools/github-release-notes)

-   `ci`: configuration for Continuous Integration environments: `.appveyor.yml` and `.travis.yml`
    (these files should *not* be git-ignored in *target* projects)

This feature is implemented:

-   in [`SuperpomSharedFileExportPlugin`](buildSrc/src/main/kotlin/pl/tlinkowski/gradle/my/buildsrc/plugin/SuperpomSharedFileExportPlugin.kt),
    by registering a special `exportSharedFiles` task for this (*source*) project

    -   the task zips files to be exported and places the resulting archive in the
        [resources](subprojects/pl.tlinkowski.gradle.my.superpom/src/main/resources) of the SuperPOM plugin

-   in [`SuperpomSharedFileImportPlugin`](subprojects/pl.tlinkowski.gradle.my.superpom/src/main/kotlin/pl/tlinkowski/gradle/my/superpom/internal/plugin/SuperpomSharedFileImportPlugin.kt),
    by registering a special `importSharedFiles` task for a *target* project

    -   the task reads the archive as a resource and unzips it in the corresponding location

## Naming Convention

This project applies a
[naming convention for Maven & JPMS by Christian Stein](https://sormuras.github.io/blog/2019-08-04-maven-coordinates-and-java-module-names).
In short:
 
> Gradle project name = JPMS module name

The SuperPOM plugin enforces this convention by ensuring that the Gradle project name (i.e. Maven `artifactId`):

-   starts with Maven `groupId`
-   equals JPMS module name (only if `module-info.java` is present)
-   is a prefix of every package in the project

## Requirements

Gradle 5+, JDK 11+.

## About the Author

See my webpage ([tlinkowski.pl](https://tlinkowski.pl/)) or
find me on Twitter ([@t_linkowski](https://twitter.com/t_linkowski)).
