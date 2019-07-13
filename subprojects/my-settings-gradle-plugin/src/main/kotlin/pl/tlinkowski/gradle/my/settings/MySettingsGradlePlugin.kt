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

package pl.tlinkowski.gradle.my.settings

import org.gradle.api.Plugin
import org.gradle.api.initialization.Settings
import org.gradle.kotlin.dsl.*
import java.io.File

/**
 * A `Settings` plugin that:
 * - configures plugin management for `MySuperpomGradlePlugin`
 * - configures the project structure as described in
 * [Kordamp project structure](https://aalmiray.github.io/kordamp-gradle-plugins/#_project_structure)
 *
 * @author Tomasz Linkowski
 */
class MySettingsGradlePlugin : Plugin<Settings> {

  /**
   * Applies the plugin (can be used in `settings.gradle.kts` only).
   */
  override fun apply(settings: Settings) {
    settings.configurePluginManagement()
    settings.configureProjectStructure()
  }

  private fun Settings.configurePluginManagement() {
    pluginManagement {
      //region WHY: this SuperPOM plugin won't be deployed to Gradle Plugin Portal (it's not a generic-use plugin)
      repositories {
        mavenCentral {
          content {
            includeGroup("pl.tlinkowski.gradle.my")
          }
        }
        gradlePluginPortal()
      }
      //endregion

      //region WHY: so that the SuperPOM plugin can be applied in `build.gradle.kts` without specifying its version
      resolutionStrategy {
        eachPlugin {
          if (requested.id.namespace == "pl.tlinkowski.gradle.my") {
            val mySuperpomVersion: String by settings
            useVersion(mySuperpomVersion)
          }
        }
      }
      //endregion
    }
  }

  //region ADAPTED FROM: https://aalmiray.github.io/kordamp-gradle-plugins/#_project_structure
  private fun Settings.configureProjectStructure() {
    listOf("subprojects").forEach { includeSubprojects(rootDir.resolve(it)) }
  }

  private fun Settings.includeSubprojects(groupDir: File) {
    requireNotNull(groupDir.listFiles(), { groupDir }).forEach { tryIncludeSubproject(it) }
  }

  private fun Settings.tryIncludeSubproject(subprojectDir: File) {
    val subprojectName = subprojectDir.name
    val buildFile = subprojectDir.resolve("$subprojectName.gradle.kts")

    include(subprojectName)

    val subproject = project(":$subprojectName")
    subproject.projectDir = subprojectDir
    subproject.buildFileName = buildFile.name
  }
  //endregion
}
