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

package pl.tlinkowski.gradle.my.buildsrc.plugin

import org.gradle.api.*
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.Delete
import org.gradle.api.tasks.bundling.Zip
import org.gradle.api.tasks.bundling.ZipEntryCompression
import org.gradle.kotlin.dsl.*
import pl.tlinkowski.gradle.my.superpom.internal.shared.SuperpomFileSharing
import pl.tlinkowski.gradle.my.superpom.internal.shared.TaskGroupNames
import java.io.File

/**
 * Facilitates export of some files from this SuperPOM project to all target projects.
 *
 * Counterpart of `SuperpomSharedFileImportPlugin` in `my-superpom-gradle-plugin` project.
 *
 * @author Tomasz Linkowski
 */
class SuperpomSharedFileExportPlugin : Plugin<Project> {

  private lateinit var exportedDir: File

  override fun apply(superpomProject: Project) {
    superpomProject.configureExport()
  }

  private fun Project.configureExport() {
    exportedDir = file("src/main/resources/${SuperpomFileSharing.RESOURCE_PATH}")
    tasks {
      configureTasks()
    }
  }

  private fun TaskContainerScope.configureTasks() {
    // https://docs.gradle.org/current/userguide/kotlin_dsl.html#using_kotlin_delegated_properties
    val exportPluginVersion by registering {
      configureExportPluginVersion()
    }
    val exportSharedGradleProperties by registering(Copy::class) {
      configureExportSharedGradleProperties()
    }
    val exportSharedIdeaFiles by registering(Zip::class) {
      configureExportSharedIdeaFiles()
    }

    //region MAIN TASKS
    val exportSharedFiles by registering {
      configureExportSharedFiles()
      dependsOn(exportPluginVersion, exportSharedGradleProperties, exportSharedIdeaFiles)
    }
    val cleanExportSharedFiles by existing(Delete::class) {
      configureCleanExportSharedFiles()
    }
    //endregion

    //region LIFECYCLE HOOKS
    "processResources" {
      dependsOn(exportSharedFiles)
    }
    "clean" {
      dependsOn(cleanExportSharedFiles)
    }
    //endregion
  }

  private fun Task.configureExportPluginVersion() {
    group = TaskGroupNames.FILE_SHARING

    val filename = SuperpomFileSharing.PLUGIN_VERSION_FILENAME
    description = "Exports the plugin version to a text file ($filename)"

    val pluginVersion = project.version.toString()
    inputs.property("version", pluginVersion)

    val pluginVersionFile = exportedDir.resolve(filename)
    outputs.file(pluginVersionFile)

    doLast {
      pluginVersionFile.writeText(pluginVersion)
    }
  }

  private fun Copy.configureExportSharedGradleProperties() {
    val filename = SuperpomFileSharing.SHARED_PROPERTIES_FILENAME

    group = TaskGroupNames.FILE_SHARING
    description = "Exports shared Gradle properties ($filename)"

    from("${project.rootDir}/gradle/$filename")
    into(exportedDir)
  }

  private fun Zip.configureExportSharedIdeaFiles() {
    group = TaskGroupNames.FILE_SHARING
    from(SuperpomFileSharing.sharedIdeaFiles(project))
    intoSharedFilesZip("idea")
  }

  private fun Task.configureExportSharedFiles() {
    group = TaskGroupNames.FILE_SHARING
    description = "Exports all files shared by the SuperPOM plugin"
  }

  private fun Delete.configureCleanExportSharedFiles() {
    group = TaskGroupNames.FILE_SHARING
    description = "Cleans all files to be exported by the plugin"
    delete(exportedDir)
  }

  /**
   * Configures the destination of the [Zip] task.
   */
  private fun Zip.intoSharedFilesZip(subname: String) {
    val filename = "shared-$subname-files.zip"

    description = "Packages $filename as an exported resource"

    destinationDirectory.set(exportedDir)
    archiveFileName.set(filename)
    entryCompression = ZipEntryCompression.STORED
  }
}
