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

package pl.tlinkowski.gradle.my.superpom.shared.extension

/**
 * Per-subproject extension of My Gradle SuperPOM.
 *
 * Usage:
 * ```kotlin
 * superpom {
 *
 * }
 * ```
 *
 * @author Tomasz Linkowski
 */
open class MySuperpomExtension {

  /**
   * If `true`, a project will get all the test dependencies with `api` instead of `testImplementation` scope.
   */
  var isTestProject = false

  /**
   * If `true`, a project will get Lombok dependencies, a Delombok task, and its Javadoc task will ba based on
   * delomboked source code.
   */
  var useLombok = false

  /**
   * An [automatic module name](https://docs.oracle.com/en/java/javase/12/docs/specs/jar/jar.html#modular-jar-files)
   * (must be set if there's no `module-info.java`).
   */
  var automaticModuleName: String? = null
}
