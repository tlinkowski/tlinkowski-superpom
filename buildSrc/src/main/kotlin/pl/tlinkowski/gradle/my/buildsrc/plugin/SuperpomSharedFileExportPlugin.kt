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

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.Delete
import org.gradle.kotlin.dsl.*
import pl.tlinkowski.gradle.my.buildsrc.task.*
import pl.tlinkowski.gradle.my.superpom.shared.internal.SuperpomFileSharing
import pl.tlinkowski.gradle.my.superpom.shared.internal.TaskGroupNames

/**
 * Facilitates export of some files from this SuperPOM project to all target projects.
 *
 * Counterpart of `SuperpomSharedFileImportPlugin` from `my-superpom-gradle-plugin`.
 *
 * @author Tomasz Linkowski
 */
class SuperpomSharedFileExportPlugin : Plugin<Project> {

  companion object {
    const val MAIN_TASK_NAME = "exportSharedFiles"
    const val MAIN_CLEAN_TASK_NAME = "cleanExportSharedFiles"
  }

  override fun apply(superpomProject: Project) {
    superpomProject.tasks {
      configureSuperpomExportTasks()
    }
  }

  private fun TaskContainerScope.configureSuperpomExportTasks() {
    registerMainTasks()
    hookUpMainTasksToLifecycleTasks()

    configureSpecialExportTasks()

    SuperpomFileSharing.zipKeys().forEach {
      configureExportTaskForKey(it)
    }
  }

  private fun TaskContainerScope.registerMainTasks() {
    register(MAIN_TASK_NAME) {
      group = TaskGroupNames.FILE_SHARING
      description = "Exports all files shared by the SuperPOM plugin"
    }

    register<Delete>(MAIN_CLEAN_TASK_NAME) {
      group = TaskGroupNames.FILE_SHARING
      description = "Cleans all files to be exported by the plugin"
      delete(SuperpomFileSharing.exportedResourceDir(project))
    }
  }

  private fun TaskContainerScope.hookUpMainTasksToLifecycleTasks() {
    "processResources" {
      dependsOn(MAIN_TASK_NAME)
    }
    "clean" {
      dependsOn(MAIN_CLEAN_TASK_NAME)
    }
  }

  private fun TaskContainerScope.configureSpecialExportTasks() {
    // https://docs.gradle.org/current/userguide/kotlin_dsl.html#using_kotlin_delegated_properties
    val exportPluginVersion by registering(PluginVersionExportTask::class)

    val exportSharedGradleProperties by registering(DirectSharedFileExportTask::class) {
      fromFile(project.rootDir.resolve("gradle").resolve(SuperpomFileSharing.SHARED_PROPERTIES_FILENAME))
    }

    MAIN_TASK_NAME {
      dependsOn(exportPluginVersion, exportSharedGradleProperties)
    }
  }

  private fun TaskContainerScope.configureExportTaskForKey(key: String) {
    val exportTaskName = "exportShared${key.capitalize()}Files"
    register<ZipSharedFileExportTask>(exportTaskName) {
      forKey(key)
    }
    MAIN_TASK_NAME {
      dependsOn(exportTaskName)
    }
  }
}
