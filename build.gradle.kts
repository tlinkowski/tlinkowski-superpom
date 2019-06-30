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

import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  kotlin("jvm") version "1.3.40"
  `java-gradle-plugin`
  `kotlin-dsl`
  id("org.kordamp.gradle.kotlindoc")

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

configure<nl.javadude.gradle.plugins.license.LicenseExtension> {
  listOf("gradle", "kt", "kts").forEach {
    mapping(it, "SLASHSTAR_STYLE")
  }
}

allprojects {
  repositories {
    mavenCentral()
  }
}

tasks {
  //region https://github.com/hierynomus/license-gradle-plugin#running-on-a-non-java-project
  val licenseGradle by registering(com.hierynomus.gradle.license.tasks.LicenseCheck::class) {
    source = fileTree(rootDir) {
      include("**/*.gradle")
      include("**/*.gradle.kts")
    }
  }
  "license" {
    dependsOn(licenseGradle)
  }
  //endregion
}

/**
 * Configures every subproject.
 *
 * This method is called in a `subprojects` block below, but - in case of single-project builds like this one - it may
 * also be called for the root project.
 */
fun configureSubproject() {
  apply(plugin = "groovy") // for Spock

  dependencies {
    val testImplementation by configurations
    val spockVersion: String by project
    val groovyVersion: String by project

    testImplementation(group = "org.spockframework", name = "spock-core", version = spockVersion)
    testImplementation(group = "org.codehaus.groovy", name = "groovy-all", version = groovyVersion)
  }

  tasks {
    withType<org.gradle.api.tasks.testing.Test> {
      testLogging {
        events("PASSED", "FAILED", "SKIPPED")
      }
    }

    //region https://docs.gradle.org/current/userguide/jacoco_plugin.html
    val jacocoTestReport by existing
    val jacocoTestCoverageVerification by existing(org.gradle.testing.jacoco.tasks.JacocoCoverageVerification::class) {
      violationRules {
        rule {
          limit {
            counter = "LINE"
            minimum = "0.99".toBigDecimal()
          }
        }
      }
      shouldRunAfter(jacocoTestReport)
    }

    "check" {
      dependsOn(jacocoTestReport)
      dependsOn(jacocoTestCoverageVerification)
    }
    //endregion
  }
}

subprojects {
  configureSubproject() // without rootProject
}
//endregion

//region PRIVATE BUILD SCRIPT
configureSubproject() // rootProject (this is a single-project build)

apply(from = "gradle/generateTLinkowskiSuperpomPluginKt.gradle.kts")

dependencies {
  implementation(kotlin("stdlib-jdk8"))
}

tasks.withType<KotlinCompile> {
  kotlinOptions.jvmTarget = "1.8"
}

//region WORKAROUND FOR: https://github.com/aalmiray/kordamp-gradle-plugins/issues/139
configurations {
  create("dokkaRuntime")
}
//endregion

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

  kotlindoc {
    replaceJavadoc = true
    jdkVersion = 8
  }

  plugin {
    id = "pl.tlinkowski.tlinkowski-superpom"
    implementationClass = "pl.tlinkowski.superpom.TLinkowskiSuperpomPlugin"
  }
}
//endregion

apply(from = "$rootDir/gradle/ide.gradle.kts")
