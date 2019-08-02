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
import org.gradle.kotlin.dsl.*
import org.gradle.testing.jacoco.plugins.JacocoPlugin
import org.gradle.testing.jacoco.tasks.JacocoCoverageVerification
import org.gradle.testing.jacoco.tasks.JacocoReport

/**
 * Applies Kordamp's JaCoCo plugin and configures automatic code coverage reports and minimum code coverage.
 *
 * @author Tomasz Linkowski
 */
internal class JacocoConfigPlugin : AbstractRootPlugin() {

  private val minCodeCoverage = "0.95"

  override fun Project.configureRootProject() {
    subprojects {
      apply {
        // https://aalmiray.github.io/kordamp-gradle-plugins/#_org_kordamp_gradle_jacoco
        plugin(JacocoPlugin::class)
      }
      tasks {
        configureJacocoTasks()
      }
    }
  }

  /*
   * https://docs.gradle.org/current/userguide/jacoco_plugin.html
   */
  private fun TaskContainerScope.configureJacocoTasks() {
    // https://docs.gradle.org/current/userguide/kotlin_dsl.html#using_kotlin_delegated_properties
    val jacocoTestReport by existing(JacocoReport::class)
    val jacocoTestCoverageVerification by existing(JacocoCoverageVerification::class)

    jacocoTestCoverageVerification {
      configureMinCodeCoverage()
      shouldRunAfter(jacocoTestReport)
    }

    "check" {
      dependsOn(jacocoTestReport, jacocoTestCoverageVerification)
    }
  }

  private fun JacocoCoverageVerification.configureMinCodeCoverage() {
    // https://docs.gradle.org/current/userguide/jacoco_plugin.html#sec:jacoco_report_violation_rules
    violationRules {
      rule {
        limit {
          counter = "LINE"
          minimum = minCodeCoverage.toBigDecimal()
        }
      }
    }
  }
}
