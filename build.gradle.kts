plugins {
  /**
   * ATTENTION: The same plugins must be included as dependencies in `plugin.gradle.kts`.
   */
  //region SHARED PLUGINS
  id("org.kordamp.gradle.project") apply false
  //endregion
}

allprojects {
  repositories {
    mavenCentral()
  }
}

/**
 * ATTENTION: The contents of the `SHARED BUILD SCRIPT` region are copied to `TLinkowskiSuperpomPlugin.kt`.
 * As a result, all configuration here should be explicit (no imports, no auto-generated Kotlin DSL accessors).
 */
//region SHARED BUILD SCRIPT
apply<org.kordamp.gradle.plugin.base.BasePlugin>()

configure<org.kordamp.gradle.plugin.base.ProjectConfigurationExtension> {
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

  licensing {
    licenses {
      license {
        id = "Apache-2.0"
      }
    }
  }
}

subprojects {
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
}
//endregion

apply(from = "$rootDir/gradle/ide.gradle.kts")
