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

package pl.tlinkowski.gradle.my.superpom.internal.plugin

import org.gradle.api.Project
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.Delete
import org.gradle.kotlin.dsl.*
import pl.tlinkowski.gradle.my.superpom.MySuperpomGradlePluginExportedFiles
import pl.tlinkowski.gradle.my.superpom.internal.shared.SuperpomFileSharing
import pl.tlinkowski.gradle.my.superpom.internal.shared.SuperpomTasks
import pl.tlinkowski.gradle.my.superpom.internal.shared.plugin.AbstractRootPlugin
import java.nio.file.Files
import java.nio.file.StandardCopyOption

/**
 * Facilitates import of files shared (exported) by the SuperPOM plugin.
 *
 * Counterpart of `SuperpomSharedFileExportPlugin` from `buildSrc`.
 *
 * @author Tomasz Linkowski
 */
class SuperpomSharedFileImportPlugin : AbstractRootPlugin() {

  override fun Project.configureRootProject() {
    tasks {
      configureRootTasks()
    }
  }

  private fun TaskContainerScope.configureRootTasks() {
    // https://docs.gradle.org/current/userguide/kotlin_dsl.html#using_kotlin_delegated_properties
    val importSharedIdeaFiles by registering(Copy::class) {
      group = SuperpomTasks.GROUP
      fromSharedFilesZip("idea")
      into(".idea")
    }
    val cleanImportSharedIdeaFiles by registering(Delete::class) {
      group = SuperpomTasks.GROUP
      description = "Cleans shared .idea files imported from the SuperPOM plugin"
      delete(SuperpomFileSharing.sharedIdeaFiles(project))
    }

    //region MAIN TASKS (no dependency on them - should be run manually whenever needed)
    register("importSharedFiles") {
      group = SuperpomTasks.GROUP
      description = "Imports all files shared by the SuperPOM plugin"
      dependsOn(importSharedIdeaFiles)
    }
    register("cleanImportSharedFiles") {
      group = SuperpomTasks.GROUP
      description = "Cleans all files imported from the SuperPOM plugin"
      dependsOn(cleanImportSharedIdeaFiles)
    }
    //endregion
  }

  /**
   * Configures a [Copy] task to import files from `shared-$subname-files.zip` resource.
   */
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
}
