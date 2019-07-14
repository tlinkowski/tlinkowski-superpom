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

/*
 * Counterpart of `configureSharedFileImport()` in `BaseMySuperpomGradlePlugin.kt`.
 */
tasks {
  //region DUPLICATED IN `BaseMySuperpomGradlePlugin.configureSharedFileImport()`
  val superpomGroup = "superpom"
  val sharedIdeaFileTree = fileTree("$rootDir/.idea") {
    include("/codeStyles/", "/copyright/", "/inspectionProfiles/")
  }
  //endregion

  val exportedDir = file("src/main/resources/pl/tlinkowski/gradle/my/superpom/exported")

  // https://docs.gradle.org/current/userguide/kotlin_dsl.html#using_kotlin_delegated_properties
  val exportPluginVersion by registering {
    group = superpomGroup
    description = "Exports the plugin version to a text file"

    val pluginVersionFile = exportedDir.resolve("plugin-version.txt")

    inputs.property("version", project.version)
    outputs.file(pluginVersionFile)

    doLast {
      val version: String by project
      pluginVersionFile.writeText(version)
    }
  }

  /**
   * Configures the destination of the [Zip] task.
   */
  fun Zip.intoSharedFilesZip(subname: String) {
    val filename = "shared-$subname-files.zip"

    description = "Packages $filename as an exported resource"

    destinationDirectory.set(exportedDir)
    archiveFileName.set(filename)
    entryCompression = ZipEntryCompression.STORED
  }

  val exportSharedIdeaFiles by registering(Zip::class) {
    group = superpomGroup
    from(sharedIdeaFileTree)
    intoSharedFilesZip("idea")
  }

  //region MAIN TASKS
  val exportSharedFiles by registering {
    group = superpomGroup
    description = "Exports all files shared by the SuperPOM plugin"
    dependsOn(exportPluginVersion, exportSharedIdeaFiles)
  }
  val cleanExportSharedFiles by existing(Delete::class) {
    group = superpomGroup
    description = "Cleans all files to be exported by the plugin"
    delete(exportedDir)
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
