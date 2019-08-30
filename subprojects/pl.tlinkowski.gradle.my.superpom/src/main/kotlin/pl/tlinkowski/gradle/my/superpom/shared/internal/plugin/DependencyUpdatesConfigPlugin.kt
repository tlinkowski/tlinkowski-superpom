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

import com.github.benmanes.gradle.versions.VersionsPlugin
import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import org.gradle.api.Project
import org.gradle.kotlin.dsl.*

/**
 * Applies and configures [Gradle Versions Plugin](https://github.com/ben-manes/gradle-versions-plugin].
 *
 * @author Tomasz Linkowski
 */
internal class DependencyUpdatesConfigPlugin : AbstractRootPlugin() {

  override fun Project.configureRootProject() {
    apply<VersionsPlugin>()

    tasks {
      configureDependencyUpdates()
    }
  }

  /**
   * Configure `dependencyUpdates` to skip Release Candidates.
   *
   * See: https://github.com/ben-manes/gradle-versions-plugin#revisions
   */
  private fun TaskContainerScope.configureDependencyUpdates() {
    // https://docs.gradle.org/current/userguide/kotlin_dsl.html#using_kotlin_delegated_properties
    val dependencyUpdates by existing(DependencyUpdatesTask::class)

    dependencyUpdates {
      gradleReleaseChannel = "current"

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
  }
}
