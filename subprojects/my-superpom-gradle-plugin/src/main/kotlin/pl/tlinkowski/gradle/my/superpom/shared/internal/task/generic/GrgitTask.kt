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

import org.ajoberstar.grgit.Configurable
import org.ajoberstar.grgit.Grgit
import org.ajoberstar.grgit.operation.PushOp
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Internal
import org.gradle.kotlin.dsl.*
import pl.tlinkowski.gradle.my.superpom.shared.internal.TaskGroupNames
import pl.tlinkowski.gradle.my.superpom.shared.internal.isDryRunRelease

/**
 * A task that performs some Grgit action(s).
 *
 * @author Tomasz Linkowski
 */
internal open class GrgitTask : DefaultTask() {

  @get:Internal
  protected val grgit: Grgit by project

  init {
    group = TaskGroupNames.INTERNAL
  }

  /**
   * Calls [Grgit.push] if not in dry-run mode.
   */
  protected fun Grgit.pushIfNotDryRun(closure: Configurable<PushOp>) {
    if (project.isDryRunRelease) {
      // we can't use PushOp.dryRun property due to CI (https://github.com/tlinkowski/tlinkowski-superpom/issues/42)
      closure.configure(PushOp(null)) // just test the closure
    } else {
      push(closure) // actually push
    }
  }
}
