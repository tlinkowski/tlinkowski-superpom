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

package pl.tlinkowski.gradle.my.superpom.internal.plugin

import org.gradle.api.Project
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.kotlin.dsl.*
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import pl.tlinkowski.gradle.my.superpom.internal.shared.plugin.AbstractRootPlugin

/**
 * Fixes too eager modification of `compileJava.destinationDir`. This modification should be performed only
 * for Kotlin-only project (i.e. when `compileKotlin` has some sources), but - currently - it's performed always.
 *
 * This breaks in a scenario when Kotlin is used for tests only, as happens in this plugin.
 *
 * The original modification can be found in `org.javamodularity.moduleplugin` - `CompileJavaTaskMutator.java:57`.
 *
 * TODO: Remove this as soon as it's fixed in `org.javamodularity.moduleplugin`
 *
 * @author Tomasz Linkowski
 */
class EagerDestinationDirModificationFixPlugin : AbstractRootPlugin() {

  override fun Project.configureRootProject() {
    subprojects {
      afterEvaluate {
        tasks {
          configureTasks()
        }
      }
    }
  }

  private fun TaskContainerScope.configureTasks() {
    val mutatedJavaCompileName = "compileModuleInfoJava".takeIf { findByName(it) != null } ?: "compileJava"

    named<JavaCompile>(mutatedJavaCompileName) {
      if (hasNoMainKotlinSources()) {
        doFirst {
          val compileKotlin by getting(KotlinCompile::class)
          assert(destinationDir == compileKotlin.destinationDir) { "Redundant fix - remove!" }

          fixDestinationDir()
          logger.info("Fixed {}.destinationDir = {}", name, destinationDir)
        }
      }
    }
  }

  private fun JavaCompile.hasNoMainKotlinSources() = project.fileTree("src/main/kotlin").isEmpty

  private fun JavaCompile.fixDestinationDir() {
    destinationDir = project.buildDir.resolve("classes/java/main")
  }
}
