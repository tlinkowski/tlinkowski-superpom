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

package pl.tlinkowski.gradle.my.buildsrc.task

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.*
import pl.tlinkowski.gradle.my.superpom.internal.shared.SuperpomFileSharing
import pl.tlinkowski.gradle.my.superpom.internal.shared.TaskGroupNames

/**
 * Exports a plugin version to a text file.
 *
 * @author Tomasz Linkowski
 */
open class PluginVersionExportTask : DefaultTask() {

  private val filename = SuperpomFileSharing.PLUGIN_VERSION_FILENAME

  @Input
  val pluginVersion = project.version.toString()

  @OutputFile
  val pluginVersionFile = SuperpomFileSharing.exportedResourceDir(project).resolve(filename)

  init {
    group = TaskGroupNames.FILE_SHARING
    description = "Exports the plugin version to a text file ($filename)"
  }

  /**
   * Exports plugin version to a file exported as a resource.
   */
  @TaskAction
  fun exportVersionToFile() {
    pluginVersionFile.writeText(pluginVersion)
  }
}
