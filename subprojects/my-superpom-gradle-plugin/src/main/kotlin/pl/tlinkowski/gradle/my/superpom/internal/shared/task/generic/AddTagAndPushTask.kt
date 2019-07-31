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

package pl.tlinkowski.gradle.my.superpom.internal.shared.task.generic

import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import pl.tlinkowski.gradle.my.superpom.internal.shared.isDryRunRelease

/**
 * Adds (both locally and remotely) a tag with given name.
 *
 * @author Tomasz Linkowski
 * @see RemoveTagAndPushTask
 */
internal open class AddTagAndPushTask : GrgitTask() {

  @get:Input
  lateinit var tagName: String

  @TaskAction
  fun addTagAndPush() {
    logger.info("Adding local and remote tag '{}' to {}", tagName, grgit.head().abbreviatedId)
    // local
    grgit.tag.add {
      name = tagName
    }
    // remote
    grgit.push {
      refsOrSpecs = listOf("refs/tags/$tagName")
      dryRun = project.isDryRunRelease
    }
  }
}
