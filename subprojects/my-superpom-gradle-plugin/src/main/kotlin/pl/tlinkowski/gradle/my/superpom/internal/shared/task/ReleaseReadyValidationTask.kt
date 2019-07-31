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

package pl.tlinkowski.gradle.my.superpom.internal.shared.task

import org.gradle.api.GradleException
import org.gradle.api.tasks.TaskAction
import pl.tlinkowski.gradle.my.superpom.internal.shared.*
import pl.tlinkowski.gradle.my.superpom.internal.shared.task.generic.GrgitTask

/**
 * A simple task that verifies if a release is possible.
 *
 * @author Tomasz Linkowski
 */
internal open class ReleaseReadyValidationTask : GrgitTask() {

  init {
    group = TaskGroupNames.RELEASING
    description = "Ensures that a release is possible right now"
  }

  @TaskAction
  fun validateReleasePossible() {
    if (!isPushedMasterBranch() && !project.isDryRunRelease) {
      throw GradleException("Release can be performed only from a pushed 'master' branch")
    }
    if (!project.isFinalRelease) {
      throw GradleException("Release can be performed only with -Preckon.stage=final")
    }
  }

  private fun isPushedMasterBranch(): Boolean = with(grgit.branch.current()) {
    return name == "master" && grgit.isPushedBranch(this)
  }
}
