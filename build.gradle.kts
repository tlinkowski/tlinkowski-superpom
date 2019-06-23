import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  //region MAIN
  kotlin("jvm") version "1.3.40"
  `java-gradle-plugin`
  `kotlin-dsl`
  //endregion

  //region TEST
  groovy
  //endregion

  /**
   * ATTENTION: The same plugins must be included in the `dependencies` block below.
   */
  //region SHARED PLUGINS
  id("org.kordamp.gradle.project") apply false
  //endregion
}

//region gradle.properties
val kordampVersion: String by project
val spockVersion: String by project
val groovyVersion: String by project
//endregion

dependencies {
  /**
   * ATTENTION: The same plugins must be included in the `plugins` block above.
   */
  //region SHARED PLUGINS
  compile(group = "org.kordamp.gradle", name = "project-gradle-plugin", version = kordampVersion)
  //endregion

  implementation(kotlin("stdlib-jdk8"))

  //region TEST
  testImplementation(group = "org.spockframework", name = "spock-core", version = spockVersion)
  testImplementation(group = "org.codehaus.groovy", name = "groovy-all", version = groovyVersion)
  //endregion
}

tasks.withType<KotlinCompile> {
  kotlinOptions.jvmTarget = "1.8"
}

apply(from = "gradle/generateTLinkowskiSuperpomPluginKt.gradle.kts")

repositories {
  gradlePluginPortal() // for other Gradle plugins
}

/**
 * ATTENTION: The contents of the `SHARED BUILD SCRIPT` region are copied to `TLinkowskiSuperpomPlugin.kt`.
 * As a result, all configuration here should be explicit (no imports, no auto-generated Kotlin DSL accessors).
 */
//region SHARED BUILD SCRIPT
apply(plugin = "org.kordamp.gradle.base")

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

//region PRIVATE CONFIG SCRIPT
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
