# Gradle SuperPOM of Tomasz Linkowski

[![Build (Linux)](https://img.shields.io/travis/com/tlinkowski/tlinkowski-superpom/master.svg?logo=linux)](https://travis-ci.com/tlinkowski/tlinkowski-superpom)
[![Build (Windows)](https://img.shields.io/appveyor/ci/tlinkowski/tlinkowski-superpom/master.svg?logo=windows)](https://ci.appveyor.com/project/tlinkowski/tlinkowski-superpom/branch/master)
[![Code coverage](https://img.shields.io/codecov/c/github/tlinkowski/tlinkowski-superpom.svg)](https://codecov.io/gh/tlinkowski/tlinkowski-superpom)
[![Codacy grade](https://img.shields.io/codacy/grade/81a0cef956a34083accd2f8e401a66de.svg)](https://app.codacy.com/project/tlinkowski/tlinkowski-superpom/dashboard)

This project is inspired by [The Gradle SuperPOM](http://andresalmiray.com/the-gradle-superpom/) post by
[Andres Almiray](https://twitter.com/aalmiray).

This projects provides two plugins:

1.  A Gradle [`Project`](https://docs.gradle.org/current/javadoc/org/gradle/api/Project.html)
    plugin (id: [`pl.tlinkowski.gradle.my.superpom`](subprojects/my-superpom-gradle-plugin))

2.  A Gradle [`Settings`](https://docs.gradle.org/current/javadoc/org/gradle/api/initialization/Settings.html)
    plugin (id: [`pl.tlinkowski.gradle.my.settings`](subprojects/my-settings-gradle-plugin))

Together, those two plugins preconfigure Gradle builds for each of my projects.

## Usage

### by Tomasz Linkowski

`gradle.properties`:

```properties
mySuperpomVersion=0.1
```

`settings.gradle.kts`:

```kotlin
buildscript {
  repositories {
    mavenCentral()
  }
  dependencies {
    val mySuperpomVersion: String by settings
    classpath(group = "pl.tlinkowski.gradle.my", name = "my-settings-gradle-plugin", version = mySuperpomVersion)
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

    -   configures:

        -   Maven Central repository

        -   [dependency updates](https://github.com/ben-manes/gradle-versions-plugin): skipping Release Candidates

2.  for the root project:

    -   applies: [`org.kordamp.gradle.project`](https://aalmiray.github.io/kordamp-gradle-plugins/#_org_kordamp_gradle_project) plugin

    -   configures:

        -   main project properties using
            [Kordamp DSL](https://aalmiray.github.io/kordamp-gradle-plugins/#_org_kordamp_gradle_base_dsl)

        -   shared file import tasks (see [shared file export/import](#shared-file-exportimport))

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

#### Shared File Export/Import

Selected files in this project can be directly exported to projects that apply this SuperPOM plugin. It can be viewed
as a "sync" operation between this (*source*) project and all *target* projects.

Currently, the following files are taken into account
(usually, it's a good idea to git-ignore these files in *target* projects):

-   parts of IntelliJ configuration from `.idea` directory
    (subdirectories `codeStyles`, `copyright`, `inspectionProfiles`)

This feature is implemented:

-   in [`SuperpomSharedFileExportPlugin`](buildSrc/src/main/kotlin/pl/tlinkowski/gradle/my/buildsrc/plugin/SuperpomSharedFileExportPlugin.kt),
    by registering a special `exportSharedFiles` task for this (*source*) project

    -   the task zips files to be exported and places the resulting archive in the
        [resources](subprojects/my-superpom-gradle-plugin/src/main/resources) of the SuperPOM plugin

-   in [`SuperpomSharedFileImportPlugin`](subprojects/my-superpom-gradle-plugin/src/main/kotlin/pl/tlinkowski/gradle/my/superpom/internal/plugin/SuperpomSharedFileImportPlugin.kt),
    by registering a special `importSharedFiles` task for a *target* project

    -   the task reads the archive as a resource and unzips it in the corresponding location

#### Gradle Property Sharing

Gradle properties at [`gradle/shared-gradle.properties`](gradle/shared-gradle.properties) are also shared by
[`SuperpomSharedFileExportPlugin`](buildSrc/src/main/kotlin/pl/tlinkowski/gradle/my/buildsrc/plugin/SuperpomSharedFileExportPlugin.kt).
Then, these properties are imported by:

-   [`shared-gradle-properties.gradle.kts`](gradle/shared-gradle-properties.gradle.kts) in:
    -   [`buildSrc`](buildSrc) build
    -   root [`settings.gradle.kts`](settings.gradle.kts)
    -   root [`build.gradle.kts`](build.gradle.kts)

-   [`SuperpomSharedGradlePropertyImportPlugin`](subprojects/my-superpom-gradle-plugin/src/main/kotlin/pl/tlinkowski/gradle/my/superpom/internal/plugin/SuperpomSharedGradlePropertyImportPlugin.kt)
    in: all *target* projects

#### Gradle Configuration Sharing

A large part of the build configuration for:

-   this (*source*) project
    (defined mostly in the included [`buildSrc`](buildSrc) build), and

-   *target* projects
    (defined in [`pl.tlinkowski.gradle.my.superpom`](subprojects/my-superpom-gradle-plugin) plugin project)

is *shared* as [`pl.tlinkowski.gradle.my.internal.shared`](subprojects/my-superpom-gradle-plugin/src/main/kotlin/pl/tlinkowski/gradle/my/superpom/internal/shared)
package (see [`MyCompleteSharedConfigPlugin`](subprojects/my-superpom-gradle-plugin/src/main/kotlin/pl/tlinkowski/gradle/my/superpom/internal/shared/plugin/MyCompleteSharedConfigPlugin.kt)).

Thanks to this, we don't have to:

-   duplicate large portions of configuration between the *source* and *target* projects, nor

-   apply the previous version of this plugin to itself to avoid the duplication mentioned above
    (as Andres Almiray suggests in [his post](http://andresalmiray.com/the-gradle-superpom/))

    -   such approach would be problematic for [shared file export/import](#shared-file-exportimport)

This is achieved by synchronizing the contents of the SuperPOM's
[`shared`](subprojects/my-superpom-gradle-plugin/src/main/kotlin/pl/tlinkowski/gradle/my/superpom/internal/shared) 
package with a corresponding `shared` package in `buildSrc`
(see [`buildSrc/build.gradle.kts`](buildSrc/build.gradle.kts) for details).

## Requirements

Gradle 5+, JDK 11+.

## About the Author

See my webpage ([tlinkowski.pl](https://tlinkowski.pl/)) or
find me on Twitter ([@t_linkowski](https://twitter.com/t_linkowski)).
