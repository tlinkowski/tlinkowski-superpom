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
package pl.tlinkowski.gradle.my.superpom

import org.gradle.api.*
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.Delete
import org.gradle.kotlin.dsl.*
import java.nio.file.Files
import java.nio.file.StandardCopyOption

/**
 * Hand-written base class for [MySuperpomGradlePlugin].
 *
 * @author Tomasz Linkowski
 */
abstract class BaseMySuperpomGradlePlugin : Plugin<Project> {

  /**
   * Applies the plugin (can be called on the root project only).
   */
  override fun apply(project: Project) {
    project.pluginOnlyBuildScript()
    project.sharedBuildScriptFromBuildGradleKts()
  }

  private fun Project.pluginOnlyBuildScript() {
    if (project != rootProject) {
      throw GradleException("This plugin can be applied to a root project only")
    }
    configureSharedFileImport()
  }

  //region SHARED FILE IMPORT
  /*
   * Counterpart of `configureSharedFileExport.gradle.kts`.
   */
  private fun Project.configureSharedFileImport() {
    tasks {
      //region DUPLICATED IN `configureSharedFileExport.gradle.kts`
      val superpomGroup = "superpom"
      val sharedIdeaFileTree = fileTree("$rootDir/.idea") {
        include("/codeStyles/", "/copyright/", "/inspectionProfiles/")
      }
      //endregion

      val importSharedIdeaFiles by registering(Copy::class) {
        group = superpomGroup
        fromSharedFilesZip("idea")
        into(".idea")
      }
      val cleanImportSharedIdeaFiles by registering(Delete::class) {
        group = superpomGroup
        description = "Cleans shared .idea files imported from the SuperPOM plugin"
        delete(sharedIdeaFileTree)
      }

      //region MAIN TASKS (no dependency on them - should be run manually whenever needed)
      register("importSharedFiles") {
        group = superpomGroup
        description = "Imports all files shared by the SuperPOM plugin"
        dependsOn(importSharedIdeaFiles)
      }
      register("cleanImportSharedFiles") {
        group = superpomGroup
        description = "Cleans all files imported from the SuperPOM plugin"
        dependsOn(cleanImportSharedIdeaFiles)
      }
      //endregion
    }
  }

  private fun Copy.fromSharedFilesZip(subname: String) {
    val sharedZipTempDir = project.file(System.getProperty("java.io.tmpdir"))
            .resolve("tlinkowski-superpom")
            .resolve(MySuperpomGradlePluginExportedFiles.readPluginVersion())
    sharedZipTempDir.mkdirs()

    val filename = "shared-$subname-files.zip"
    val sharedZipTempFile = sharedZipTempDir.resolve(filename)

    MySuperpomGradlePluginExportedFiles.exportedResourceAsStream(filename).use { zipInputStream ->
      Files.copy(zipInputStream, sharedZipTempFile.toPath(), StandardCopyOption.REPLACE_EXISTING)
    }

    description = "Imports shared files from $filename exported by the SuperPOM plugin"
    from(project.zipTree(sharedZipTempFile))
  }
  //endregion

  /**
   * The contents of this method are copied from root `build.gradle.kts` (`SHARED BUILD SCRIPT` region).
   */
  protected abstract fun Project.sharedBuildScriptFromBuildGradleKts()
}
