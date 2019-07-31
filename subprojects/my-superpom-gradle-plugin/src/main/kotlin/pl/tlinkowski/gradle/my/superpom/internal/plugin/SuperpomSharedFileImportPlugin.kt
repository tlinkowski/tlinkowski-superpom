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
import org.gradle.kotlin.dsl.*
import pl.tlinkowski.gradle.my.superpom.internal.shared.SuperpomFileSharing
import pl.tlinkowski.gradle.my.superpom.internal.shared.TaskGroupNames
import pl.tlinkowski.gradle.my.superpom.internal.shared.plugin.AbstractRootPlugin
import pl.tlinkowski.gradle.my.superpom.internal.task.CleanZipSharedFileImportTask
import pl.tlinkowski.gradle.my.superpom.internal.task.ZipSharedFileImportTask

/**
 * Facilitates import of files shared (exported) by the SuperPOM plugin.
 *
 * Counterpart of `SuperpomSharedFileExportPlugin` from `buildSrc`.
 *
 * @author Tomasz Linkowski
 */
internal class SuperpomSharedFileImportPlugin : AbstractRootPlugin() {

  companion object {
    const val MAIN_TASK_NAME = "importSharedFiles"
    const val MAIN_CLEAN_TASK_NAME = "cleanImportSharedFiles"
  }

  override fun Project.configureRootProject() {
    tasks {
      configureRootImportTasks()
    }
  }

  private fun TaskContainerScope.configureRootImportTasks() {
    registerMainTasks()

    SuperpomFileSharing.zipKeys().forEach {
      configureImportTasksForKey(it)
    }
  }

  /**
   * Note: these tasks are not hooked up to lifecycle tasks (should be run manually whenever needed).
   */
  private fun TaskContainerScope.registerMainTasks() {
    register(MAIN_TASK_NAME) {
      group = TaskGroupNames.FILE_SHARING
      description = "Imports all files shared by the SuperPOM plugin"
    }
    register(MAIN_CLEAN_TASK_NAME) {
      group = TaskGroupNames.FILE_SHARING
      description = "Cleans all files imported from the SuperPOM plugin"
    }
  }

  private fun TaskContainerScope.configureImportTasksForKey(key: String) {
    val importTaskName = "importShared${key.capitalize()}Files"
    register<ZipSharedFileImportTask>(importTaskName) {
      forKey(key)
    }
    MAIN_TASK_NAME {
      dependsOn(importTaskName)
    }

    val cleanImportTaskName = "clean" + importTaskName.capitalize()
    register<CleanZipSharedFileImportTask>(cleanImportTaskName) {
      forKey(key)
    }
    MAIN_CLEAN_TASK_NAME {
      dependsOn(cleanImportTaskName)
    }
  }
}
