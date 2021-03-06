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
import org.gradle.api.tasks.javadoc.Javadoc
import org.gradle.kotlin.dsl.*
import pl.tlinkowski.gradle.my.superpom.shared.internal.task.DelombokJavaTask

/**
 * Configures [project Lombok](https://projectlombok.org/) for this subproject:
 * - adds `compileOnly` and `annotationProcessor` dependencies
 * - registers a `delombok` task
 * - configures `javadoc` to be based on delomboked source code
 *
 * @author Tomasz Linkowski
 */
internal class LombokConfigPlugin : AbstractSubprojectPlugin() {

  override fun Project.configureSubproject() {
    dependencies {
      val lombokVersion: String by project // https://projectlombok.org/changelog

      "compileOnly"(group = "org.projectlombok", name = "lombok", version = lombokVersion)
      "annotationProcessor"(group = "org.projectlombok", name = "lombok", version = lombokVersion)
    }

    tasks {
      configureTasks()
    }
  }

  private fun TaskContainerScope.configureTasks() {
    val delombokJava by registering(DelombokJavaTask::class) {
      dependsOn("compileJava")
    }

    named<Javadoc>("javadoc") {
      dependsOn(delombokJava)
      source = project.fileTree(delombokJava.get().outputDir)
    }
  }
}
