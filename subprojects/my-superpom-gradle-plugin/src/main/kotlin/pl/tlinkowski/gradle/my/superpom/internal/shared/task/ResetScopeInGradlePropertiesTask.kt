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

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import pl.tlinkowski.gradle.my.superpom.internal.shared.TaskGroupNames

/**
 * Task modifying `gradle.properties` to contain `reckon.scope=patch`.
 *
 * @author Tomasz Linkowski
 */
internal open class ResetScopeInGradlePropertiesTask : DefaultTask() {

  companion object {
    const val PROPERTY_NAME = "reckon.scope"
    const val FILENAME = "gradle.properties"
  }

  init {
    group = TaskGroupNames.INTERNAL
  }

  fun canResetScope() = project.property(PROPERTY_NAME) != "patch"

  /**
   * Sets the scope of next release to "patch" in `gradle.properties` (the scope is then to be modified manually
   * when "minor" or "major" changes are introduced into the codebase).
   */
  @TaskAction
  fun resetScopeInGradleProperties() {
    check(canResetScope())

    val gradleProperties = project.rootDir.resolve(FILENAME)
    val modifiedContent = gradleProperties.readText().replace(
            Regex("^${Regex.escape(PROPERTY_NAME)}=(?:major|minor)$", RegexOption.MULTILINE),
            "$PROPERTY_NAME=patch"
    )

    logger.info("Setting {}=patch in {}", PROPERTY_NAME, FILENAME)
    gradleProperties.writeText(modifiedContent)
  }
}
