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

import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import pl.tlinkowski.gradle.my.superpom.shared.internal.commitExt
import pl.tlinkowski.gradle.my.superpom.shared.internal.toGrgitPath

/**
 * Commits the file at [filepath] and pushes the commit to the origin.
 *
 * @author Tomasz Linkowski
 */
internal open class CommitFileAndPushTask : GrgitTask() {

  @get:Input
  lateinit var filepath: String

  @get:Input
  lateinit var commitMessage: String

  @TaskAction
  fun commitFileAndPush() {
    val grgitPath = project.file(filepath).toGrgitPath(grgit) // toGrgitPath() is needed for testing
    grgit.commitExt {
      message = commitMessage
      paths = setOf(grgitPath)
      sign = false
    }
    logger.info("Pushing modified {}", grgitPath)
    grgit.pushIfNotDryRun {}
  }
}
