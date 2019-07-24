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

package pl.tlinkowski.gradle.my.superpom

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import java.io.File
import java.nio.file.Path

/**
 * Special class that handles setting up and cleaning up an appropriately modified `gradle.properties` file.
 *
 * @author Tomasz Linkowski
 */
internal class MySuperpomSmokeTestRunner(projectDir: Path, args: List<String>) : AutoCloseable {

  private val gradleRunner: GradleRunner
  private val shadedGradleProperties: ShadedFile
  private val shadedSettingsGradleKts: ShadedFile

  init {
    gradleRunner = createGradleRunner(projectDir, args)
    shadedGradleProperties = ShadedFile(projectDir.resolve("gradle.properties")) {
      it + readTestkitPropertiesContent()
    }
    shadedSettingsGradleKts = ShadedFile(projectDir.resolve("settings.gradle.kts")) {
      replaceClasspathWithPluginClasspath(it)
    }
  }

  override fun close() {
    try {
      shadedGradleProperties.close()
    } finally {
      shadedSettingsGradleKts.close()
    }
  }

  fun build(): BuildResult = gradleRunner.build()

  private fun createGradleRunner(projectDir: Path, args: List<String>) = GradleRunner.create()
          .withPluginClasspath()
          .withProjectDir(projectDir.toFile())
          .withArguments(args)
          .forwardOutput()

  //region GRADLE.PROPERTIES (duplicated in `MySettingsSmokeTestRunner.kt`)
  private fun readTestkitPropertiesContent() = checkNotNull(gradleClassLoader().getResource("testkit-gradle.properties")).readText()

  private fun gradleClassLoader() = GradleRunner::class.java.classLoader
  //endregion

  //region SETTINGS.GRADLE.KTS
  private fun replaceClasspathWithPluginClasspath(content: String): String {
    val dependencyNotation = """group = "pl.tlinkowski.gradle.my", name = "my-settings-gradle-plugin", version = mySuperpomVersion"""
    val pluginClasspathString = buildPluginClasspathString()
    val modifiedContent = content.replace(dependencyNotation, "files($pluginClasspathString)")
    assert(content != modifiedContent) { content }
    return modifiedContent
  }

  // https://discuss.gradle.org/t/testing-a-settings-plugin/12716/4 (duplicated in `MySettingsSmokeTestRunner.kt`)
  private fun buildPluginClasspathString() = gradleRunner.pluginClasspath.joinToString(", ") { file ->
    '"' + file.toNormalizedString() + '"'
  }

  private fun File.toNormalizedString() = path.replace('\\', '/')
  //endregion
}
