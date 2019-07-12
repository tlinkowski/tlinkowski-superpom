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

package pl.tlinkowski.superpom

import org.gradle.api.Plugin
import org.gradle.api.initialization.Settings
import java.io.File

/**
 * A plugin that applies the convention described in
 * [Kordamp project structure](https://aalmiray.github.io/kordamp-gradle-plugins/#_project_structure).
 *
 * @author Tomasz Linkowski
 */
class StandardSettingsPlugin : Plugin<Settings> {

  override fun apply(settings: Settings) {
    settings.configure()
  }

  //region ADAPTED FROM: https://aalmiray.github.io/kordamp-gradle-plugins/#_project_structure
  private fun Settings.configure() {
    listOf("subprojects").forEach { includeSubprojects(rootDir.resolve(it)) }
  }

  private fun Settings.includeSubprojects(groupDir: File) {
    requireNotNull(groupDir.listFiles(), { groupDir }).forEach { tryIncludeSubproject(it) }
  }

  private fun Settings.tryIncludeSubproject(subprojectDir: File) {
    val subprojectName = subprojectDir.name
    val buildFile = subprojectDir.resolve("$subprojectName.gradle.kts")

    if (buildFile.isFile) {
      include(subprojectName)

      val subproject = project(":$subprojectName")
      subproject.projectDir = subprojectDir
      subproject.buildFileName = buildFile.name
    }
  }
  //endregion
}