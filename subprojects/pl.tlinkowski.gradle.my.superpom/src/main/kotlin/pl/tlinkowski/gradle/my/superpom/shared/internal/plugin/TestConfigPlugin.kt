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
package pl.tlinkowski.gradle.my.superpom.shared.internal.plugin

import org.gradle.api.Project
import org.gradle.api.plugins.GroovyPlugin
import org.gradle.api.tasks.compile.GroovyCompile
import org.gradle.api.tasks.testing.Test
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.gradle.kotlin.dsl.*
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import pl.tlinkowski.gradle.my.superpom.shared.internal.superpom
import java.util.*

/**
 * Configures testing as follows:
 * - test results are logged
 * - test languages: Kotlin (for helper classes) + Groovy (for [Spock Framework](http://spockframework.org/))
 *
 * @author Tomasz Linkowski
 */
internal class TestConfigPlugin : AbstractRootPlugin() {

  override fun Project.configureRootProject() {
    subprojects {
      configureSubproject()
    }
  }

  //region SUBPROJECT CONFIG
  private fun Project.configureSubproject() {
    applyPlugins()
    configureDependencies()
    tasks {
      configureTasks()
    }
  }

  private fun Project.applyPlugins() {
    // https://kotlinlang.org/docs/reference/using-gradle.html
    apply(plugin = "org.jetbrains.kotlin.jvm") // for test helpers

    // https://docs.gradle.org/current/userguide/groovy_plugin.html
    apply<GroovyPlugin>() // for Spock
  }

  /**
   * [Project.afterEvaluate] due to [Project.superpom].
   */
  private fun Project.configureDependencies() {
    afterEvaluate {
      dependencies {
        val kotlinVersion: String by project
        val spockVersion: String by project
        val groovyVersion: String by project

        val scope = if (superpom.isTestProject) "api" else "testImplementation"

        scope(kotlin(module = "stdlib-jdk8", version = kotlinVersion))
        scope(group = "org.spockframework", name = "spock-core", version = spockVersion)
        scope(group = "org.codehaus.groovy", name = "groovy-all", version = groovyVersion)
      }
    }
  }

  private fun TaskContainerScope.configureTasks() {
    configureTestLogging()
    configureKotlinCompileTasks()
    configureAccessToTestKotlinFromTestGroovy()
  }

  private fun TaskContainerScope.configureTestLogging() {
    withType<Test>().configureEach {
      // https://docs.gradle.org/current/dsl/org.gradle.api.tasks.testing.logging.TestLoggingContainer.html
      testLogging {
        events = EnumSet.of(
                // standard events
                TestLogEvent.PASSED,
                TestLogEvent.SKIPPED,
                TestLogEvent.FAILED,
                // useful e.g. with Gradle TestKit
                TestLogEvent.STANDARD_OUT,
                TestLogEvent.STANDARD_ERROR
        )
      }
    }
  }

  private fun TaskContainerScope.configureKotlinCompileTasks() {
    withType<KotlinCompile>().configureEach {
      // https://kotlinlang.org/docs/reference/using-gradle.html#attributes-specific-for-jvm
      kotlinOptions.jvmTarget = "1.8"
    }
  }

  /**
   * `testGroovy` can access `testKotlin`.
   *
   * See: https://stackoverflow.com/a/37851957/2032415
   */
  private fun TaskContainerScope.configureAccessToTestKotlinFromTestGroovy() {
    // https://docs.gradle.org/current/userguide/kotlin_dsl.html#using_kotlin_delegated_properties
    val compileTestGroovy by existing(GroovyCompile::class)
    val compileTestKotlin by existing(KotlinCompile::class)

    compileTestGroovy {
      val testKotlinDestinationDir = compileTestKotlin.get().destinationDir

      logger.debug("Appending {} to the classpath of {}", testKotlinDestinationDir, this)
      classpath += project.files(testKotlinDestinationDir)

      dependsOn(compileTestKotlin)
    }
  }
}
