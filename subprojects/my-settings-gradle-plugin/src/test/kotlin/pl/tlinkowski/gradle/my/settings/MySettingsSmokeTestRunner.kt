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

package pl.tlinkowski.gradle.my.settings

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import java.io.File

/**
 * Special class that handles setting up and cleaning up an appropriately modified `gradle.properties` file.
 *
 * @author Tomasz Linkowski
 */
internal class MySettingsSmokeTestRunner {

  private val gradleRunner: GradleRunner = GradleRunner.create()
          .withPluginClasspath()
          .withProjectDir(createTempDir(javaClass.simpleName))
          .withArguments("clean", "build")
          .forwardOutput()

  fun createSubprojectsDir() = file("subprojects").apply {
    mkdir()
  }

  fun createSubproject(name: String) = file("subprojects/$name").apply {
    mkdir()
    resolve("$name.gradle.kts").writeText(`build-gradle-kts`)
  }

  fun build(): BuildResult = gradleRunner.withArguments(gradleRunner.arguments + "--stacktrace").build()

  fun buildAndFail(): BuildResult = gradleRunner.buildAndFail()

  fun file(relative: String) = gradleRunner.projectDir.resolve(relative)

  init {
    file("gradle.properties").writeText(`gradle-properties`)
    file("settings.gradle.kts").writeText(`settings-gradle-kts`)
    file("build.gradle.kts").writeText(`build-gradle-kts`)
  }

  //region BUILD CONTENT
  private val `gradle-properties`
    get() = readTestkitPropertiesContent()

  private val `settings-gradle-kts`
    get() = """
      buildscript {
        dependencies {
          classpath(files(${buildPluginClasspathString()}))
        }
      }
      apply(plugin = "pl.tlinkowski.gradle.my.settings")
    """.trimIndent()

  private val `build-gradle-kts`
    get() = """
      plugins {
        java
      }
    """.trimIndent()
  //endregion

  //region GRADLE.PROPERTIES (duplicated in `MySuperpomSmokeTestRunner.kt`)
  private fun readTestkitPropertiesContent(): String = with(GradleRunner::class.java.classLoader) {
    return checkNotNull(getResource("testkit-gradle.properties")).readText()
  }
  //endregion

  //region SETTINGS.GRADLE.KTS
  // https://discuss.gradle.org/t/testing-a-settings-plugin/12716/4 (duplicated in `MySuperpomSmokeTestRunner.kt`)
  private fun buildPluginClasspathString() = gradleRunner.pluginClasspath.joinToString(", ") { file ->
    '"' + file.toNormalizedString() + '"'
  }

  private fun File.toNormalizedString() = path.replace('\\', '/')
  //endregion
}
