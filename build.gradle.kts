/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2019 Tomasz Linkowski.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

plugins {
  `java-gradle-plugin`
  `kotlin-dsl` apply false
  id("org.kordamp.gradle.kotlindoc")

  // https://github.com/koral--/jacoco-gradle-testkit-plugin
  id("pl.droidsonroids.jacoco.testkit") version "1.0.4" apply false

  /**
   * ATTENTION: The same plugins must be included in the `dependencies` block in `my-superpom-gradle-plugin.gradle.kts`.
   */
  //region SHARED PLUGINS
  kotlin("jvm") apply false // for test code
  id("org.kordamp.gradle.project")
  id("org.javamodularity.moduleplugin") apply false
  //endregion
}

/**
 * ATTENTION: The contents of the `SHARED BUILD SCRIPT` region are copied to `MySuperpomGradlePlugin.kt`.
 * As a result, all configuration here should be explicit (no imports, no auto-generated Kotlin DSL accessors).
 */
//region SHARED BUILD SCRIPT
apply {
  plugin("org.kordamp.gradle.project")
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

  groovydoc {
    enabled = false // Groovy used only for tests
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
  apply(plugin = "idea")

  repositories {
    mavenCentral()
  }
}

subprojects {
  apply {
    plugin("org.jetbrains.kotlin.jvm") // for test code
    plugin("groovy") // for Spock
  }

  dependencies {
    val testImplementation by configurations

    val kotlinVersion: String by project
    val spockVersion: String by project
    val groovyVersion: String by project

    testImplementation(group = "org.jetbrains.kotlin", name = "kotlin-stdlib-jdk8", version = kotlinVersion)
    testImplementation(group = "org.spockframework", name = "spock-core", version = spockVersion)
    testImplementation(group = "org.codehaus.groovy", name = "groovy-all", version = groovyVersion)
  }

  tasks {
    withType<org.gradle.api.tasks.testing.Test>().configureEach {
      testLogging {
        events("PASSED", "FAILED", "SKIPPED")
      }
    }
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
      kotlinOptions.jvmTarget = "1.8"
    }

    //region TEST-GROOVY CAN ACCESS TEST-KOTLIN: https://stackoverflow.com/a/37851957/2032415
    val compileTestGroovy by existing(org.gradle.api.tasks.compile.GroovyCompile::class)
    val compileTestKotlin by existing(org.jetbrains.kotlin.gradle.tasks.KotlinCompile::class)

    compileTestGroovy {
      classpath += files(compileTestKotlin.get().destinationDir)
      dependsOn(compileTestKotlin)
    }
    //endregion

    //region https://docs.gradle.org/current/userguide/jacoco_plugin.html
    val jacocoTestReport by existing
    val jacocoTestCoverageVerification by existing(org.gradle.testing.jacoco.tasks.JacocoCoverageVerification::class)

    jacocoTestCoverageVerification {
      violationRules {
        rule {
          limit {
            counter = "LINE"
            minimum = "0.95".toBigDecimal()
          }
        }
      }
      shouldRunAfter(jacocoTestReport)
    }

    "check" {
      dependsOn(jacocoTestReport, jacocoTestCoverageVerification)
    }
    //endregion

    //region DEPENDENCY UPDATES: https://github.com/ben-manes/gradle-versions-plugin#revisions
    val dependencyUpdates by existing(com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask::class)

    dependencyUpdates {
      resolutionStrategy {
        componentSelection {
          all {
            val rejected = listOf("alpha", "beta", "rc", "cr", "m", "preview", "b", "ea").any { qualifier ->
              candidate.version.matches(Regex("(?i).*[.-]$qualifier[.\\d-+].*"))
            }
            if (rejected) {
              reject("Release candidate")
            }
          }
        }
      }
    }
    //endregion
  }
}
//endregion

//region PRIVATE BUILD SCRIPT
subprojects {
  apply {
    plugin("java-gradle-plugin")
    plugin("org.gradle.kotlin.kotlin-dsl")
    plugin("pl.droidsonroids.jacoco.testkit")

    // WORKAROUND FOR: https://github.com/koral--/jacoco-gradle-testkit-plugin/issues/9
    from("$rootDir/gradle/workAroundJacocoGradleTestKitIssueOnWindows.gradle.kts")
  }

  dependencies {
    implementation(kotlin("stdlib-jdk8"))
  }

  gradlePlugin {
    plugins {
      create(project.name) {
        val pluginId: String by project
        id = pluginId

        val pluginImplementationClass: String by project
        implementationClass = pluginImplementationClass
      }
    }
  }
}

allprojects {
  //region WORKAROUND FOR: https://github.com/aalmiray/kordamp-gradle-plugins/issues/139
  configurations {
    create("dokkaRuntime")
  }
  repositories {
    gradlePluginPortal {
      content {
        includeGroup("org.jetbrains.dokka")
      }
    }
  }
  //endregion
}

config {
  info {
    name = "tlinkowski-superpom"
    description = """Gradle SuperPOM plugin & Gradle Settings plugin for all projects in "pl.tlinkowski" group."""
    inceptionYear = "2019"

    links {
      website = "https://github.com/tlinkowski/tlinkowski-superpom"
      issueTracker = "https://github.com/tlinkowski/tlinkowski-superpom/issues"
      scm = "https://github.com/tlinkowski/tlinkowski-superpom.git"
    }
  }

  kotlindoc {
    replaceJavadoc = true
    jdkVersion = 8
  }
}
//endregion
