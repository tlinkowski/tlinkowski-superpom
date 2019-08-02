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

package pl.tlinkowski.gradle.my.superpom.internal.shared.plugin

import org.gradle.api.*

/**
 * Base plugin for root [Project]s.
 *
 * @author Tomasz Linkowski
 */
abstract class AbstractRootPlugin : Plugin<Project> {

  /**
   * Applies this plugin, verifying that the provided [project] is a root project.
   */
  final override fun apply(project: Project) {
    if (project != project.rootProject) {
      throw GradleException("This plugin can be applied to the root project only")
    }
    project.configureRootProject()
  }

  /**
   * Logic of this plugin (for convenience, as an extension method of the root project).
   */
  protected abstract fun Project.configureRootProject()
}
