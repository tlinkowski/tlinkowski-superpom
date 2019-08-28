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

import org.gradle.api.Project
import pl.tlinkowski.gradle.my.superpom.shared.internal.configurator.MyComprehensiveReleaseConfigurator

/**
 * Configures tasks that let us perform a complete release in a single step. Includes:
 * - generating the changelog (requires Node.js)
 * - tagging the release
 * - publishing to GitHub (requires Node.js)
 * - publishing to central repos (JCenter & Maven Central)
 * - resetting the release scope to "patch" (if needed)
 *
 * @author Tomasz Linkowski
 * @see MyComprehensiveReleaseConfigurator
 */
internal class MyComprehensiveReleaseConfigPlugin : AbstractRootPlugin() {

  override fun Project.configureRootProject() {
    val configurator = MyComprehensiveReleaseConfigurator(tasks)

    configurator.configureTasks()

    subprojects {
      configurator.configureTaskDependenciesFor(this)
    }
  }
}
