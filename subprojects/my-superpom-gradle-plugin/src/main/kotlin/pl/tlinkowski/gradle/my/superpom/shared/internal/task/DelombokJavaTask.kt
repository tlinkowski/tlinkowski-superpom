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

package pl.tlinkowski.gradle.my.superpom.shared.internal.task

import org.gradle.api.tasks.*
import org.gradle.kotlin.dsl.*
import pl.tlinkowski.gradle.my.superpom.shared.internal.TaskGroupNames

/**
 * Generates delomboked code from the main source set (assumes the main source set is JPMS-compatible).
 *
 * Based on: https://github.com/franzbecker/gradle-lombok/blob/master/examples/delombok-gradle-kotlin/build.gradle.kts
 *
 * @author Tomasz Linkowski
 */
internal open class DelombokJavaTask : JavaExec() {

  @get:InputDirectory
  val inputDir = project.projectDir.resolve("src/main/java")

  @get:OutputDirectory
  val outputDir = project.buildDir.resolve("delombok")

  init {
    group = TaskGroupNames.LOMBOK
    description = "Delomboks main source code of this project"

    main = "lombok.launch.Main"
    args("delombok", inputDir, "-d", outputDir)

    classpath(configuration("compileOnly"))
  }

  @TaskAction
  override fun exec() {
    outputDir.delete()
    // calling Configuration.asPath during evaluation may cause problems, hence we call it during execution
    args("--module-path=" + configuration("compileClasspath").asPath)
    super.exec()
  }

  private fun configuration(name: String) = project.configurations[name]
}
