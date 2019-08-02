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

import org.apache.tools.ant.taskdefs.condition.Os
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.kotlin.dsl.*
import org.gradle.testing.jacoco.plugins.JacocoTaskExtension
import pl.tlinkowski.gradle.my.superpom.shared.internal.plugin.AbstractRootPlugin
import java.io.File

/**
 * Workaround for https://github.com/koral--/jacoco-gradle-testkit-plugin/issues/9
 *
 * @author Tomasz Linkowski
 */
class JacocoGradleTestkitWindowsIssueWorkaroundPlugin : AbstractRootPlugin() {

  companion object {
    /**
     * @return `true` if current OS is Windows
     */
    fun isWindows() = Os.isFamily(Os.FAMILY_WINDOWS)
  }

  private val waitMillis = 100L

  override fun Project.configureRootProject() {
    assert(isWindows())

    subprojects {
      tasks {
        "test" {
          doLast {
            waitUntilJacocoTestExecIsUnlocked()
          }
        }
      }
    }
  }

  private fun Task.waitUntilJacocoTestExecIsUnlocked() {
    val jacocoTestExecFile = jacocoTestExecFile()

    // we wait preemptively because sometimes `jacocoTestExecFile.isLocked()` returns `false`,
    // but `test` task fails anyway a moment later when it tries to read this file
    logger.info("Waiting preemptively $waitMillis ms (in case ${jacocoTestExecFile.name} is locked)...")
    Thread.sleep(waitMillis)

    while (jacocoTestExecFile.isLocked()) {
      logger.warn("Waiting another $waitMillis ms (${jacocoTestExecFile.name} is still locked)...")
      Thread.sleep(waitMillis)
    }

    logger.lifecycle("File ${jacocoTestExecFile.name} is no longer locked.")
  }

  // https://docs.gradle.org/current/userguide/jacoco_plugin.html#sec:jacoco_specific_task_configuration
  private fun Task.jacocoTestExecFile() =
          checkNotNull(extensions.getByType(JacocoTaskExtension::class).destinationFile)

  /**
   * Checks if a file is locked. Source: [https://stackoverflow.com/a/13706972/2032415]
   */
  private fun File.isLocked() = !renameTo(this)
}
