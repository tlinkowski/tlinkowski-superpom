import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  kotlin("jvm") version "1.3.40"
  `java-gradle-plugin`
  `kotlin-dsl`

  /**
   * ATTENTION: The same plugins must be included in the `dependencies` block below.
   */
  //region SHARED PLUGINS
  id("org.kordamp.gradle.project") apply false
  //endregion
}

/**
 * ATTENTION: The same plugins must be included in the `plugins` block above.
 */
//region SHARED PLUGINS
dependencies {
  val kordampVersion: String by project

  compile(group = "org.kordamp.gradle", name = "project-gradle-plugin", version = kordampVersion)
}

repositories {
  gradlePluginPortal() // for shared Gradle plugins
}
//endregion

/**
 * ATTENTION: The contents of the `SHARED BUILD SCRIPT` region are copied to `TLinkowskiSuperpomPlugin.kt`.
 * As a result, all configuration here should be explicit (no imports, no auto-generated Kotlin DSL accessors).
 */
//region SHARED BUILD SCRIPT
apply(plugin = "org.kordamp.gradle.project")
apply(plugin = "groovy") // for Spock

dependencies {
  val testImplementation by configurations
  val spockVersion: String by project
  val groovyVersion: String by project

  testImplementation(group = "org.spockframework", name = "spock-core", version = spockVersion)
  testImplementation(group = "org.codehaus.groovy", name = "groovy-all", version = groovyVersion)
}

configure<org.kordamp.gradle.plugin.base.ProjectConfigurationExtension> {
  release = rootProject.findProperty("release") == "true"

  info {
    vendor = "Tomasz Linkowski"

    people {
      person {
        id = "tlinkowski"
        name = "Tomasz Linkowski"
        url = "https://tlinkowski.pl/"
        roles = listOf("developer")
      }
    }
  }

  buildInfo {
    // for reproducible builds: https://aalmiray.github.io/kordamp-gradle-plugins/#_reproducible_builds
    skipBuildBy = true
    skipBuildDate = true
    skipBuildTime = true
  }

  licensing {
    licenses {
      license {
        id = "Apache-2.0"
      }
    }
  }
}

configure<nl.javadude.gradle.plugins.license.LicenseExtension> {
  mapping("kt", "SLASHSTAR_STYLE")
  mapping("kts", "SLASHSTAR_STYLE")
}

allprojects {
  repositories {
    mavenCentral()
  }

  tasks.withType<org.gradle.api.tasks.testing.Test> {
    testLogging {
      events("PASSED", "FAILED", "SKIPPED")
    }
  }
}
//endregion

//region PRIVATE BUILD SCRIPT
apply(from = "gradle/generateTLinkowskiSuperpomPluginKt.gradle.kts")

dependencies {
  implementation(kotlin("stdlib-jdk8"))
}

tasks.withType<KotlinCompile> {
  kotlinOptions.jvmTarget = "1.8"
}

configure<org.kordamp.gradle.plugin.base.ProjectConfigurationExtension> {
  info {
    name = "tlinkowski-superpom"
    description = """A Gradle SuperPOM for all projects in "pl.tlinkowski" group."""
    inceptionYear = "2019"

    links {
      website = "https://github.com/tlinkowski/tlinkowski-superpom"
      issueTracker = "https://github.com/tlinkowski/tlinkowski-superpom/issues"
      scm = "https://github.com/tlinkowski/tlinkowski-superpom.git"
    }
  }

  plugin {
    id = "pl.tlinkowski.tlinkowski-superpom"
    implementationClass = "pl.tlinkowski.superpom.TLinkowskiSuperpomPlugin"
  }
}
//endregion

apply(from = "$rootDir/gradle/ide.gradle.kts")
