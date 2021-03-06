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

package pl.tlinkowski.gradle.my.superpom.shared.internal.plugin

import org.ajoberstar.grgit.Status
import org.ajoberstar.reckon.gradle.ReckonExtension
import org.ajoberstar.reckon.gradle.ReckonPlugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.*
import pl.tlinkowski.gradle.my.superpom.shared.internal.grgit
import pl.tlinkowski.gradle.my.superpom.shared.internal.isFinalRelease

/**
 * Applies [`org.ajoberstar.reckon`](https://github.com/ajoberstar/reckon/) plugin and configures it to use
 * SNAPSHOT version scheme.
 *
 * @author Tomasz Linkowski
 */
internal class VersionConfigPlugin : AbstractRootPlugin() {

  override fun Project.configureRootProject() {
    apply<ReckonPlugin>()

    configure<ReckonExtension> {
      scopeFromProp()
      snapshotFromProp()
    }

    if (isFinalRelease) {
      warnAboutDirtyFiles()
    }

    tasks {
      ReckonPlugin.PUSH_TASK {
        enabled = false // we use our own push task for version tags (MyComprehensiveReleaseConfigPlugin)
      }
    }
  }

  private fun Project.warnAboutDirtyFiles() {
    grgit.status().dirtyFiles().forEach {
      logger.warn("Dirty file: {}", it)
    }
  }

  /**
   * @see Status.isClean
   */
  private fun Status.dirtyFiles() = staged.allChanges + unstaged.allChanges + conflicts
}
