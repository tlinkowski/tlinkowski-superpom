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

package pl.tlinkowski.gradle.my.superpom.shared.internal.task.generic

import org.gradle.api.tasks.*
import pl.tlinkowski.gradle.my.superpom.shared.internal.TaskGroupNames
import pl.tlinkowski.gradle.my.superpom.shared.internal.isWindows

/**
 * A simple wrapper over [Exec] task to run Node Package Manager.
 *
 * @author Tomasz Linkowski
 */
internal open class NpmRunTask : Exec() {

  init {
    group = TaskGroupNames.INTERNAL
    workingDir = project.rootDir
    executable = if (isWindows()) "npm.cmd" else "npm"
  }

  /**
   * Script name defined in `package.json`.
   */
  @get:Input
  lateinit var scriptName: String

  @TaskAction
  override fun exec() {
    args("run", scriptName)
    super.exec()
  }
}
