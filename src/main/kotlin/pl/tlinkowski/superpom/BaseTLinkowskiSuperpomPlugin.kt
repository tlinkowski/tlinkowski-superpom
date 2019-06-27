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
package pl.tlinkowski.superpom

import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Applies the concept of a Gradle SuperPOM for all projects of Tomasz Linkowski.
 *
 * See: [http://andresalmiray.com/the-gradle-superpom/]
 *
 * @author Tomasz Linkowski
 */
abstract class BaseTLinkowskiSuperpomPlugin : Plugin<Project> {

  override fun apply(project: Project) {
    project.sharedBuildScriptFromBuildGradleKts()
  }

  protected abstract fun Project.sharedBuildScriptFromBuildGradleKts()
}
