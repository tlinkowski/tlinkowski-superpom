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

import org.ajoberstar.grgit.Grgit
import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import pl.tlinkowski.gradle.my.superpom.shared.internal.toGrgitPath
import pl.tlinkowski.gradle.my.superpom.shared.internal.toNormalizedString
import java.nio.file.Path

/**
 * Special class that handles setting up and cleaning up an appropriately modified `gradle.properties` file.
 *
 * @author Tomasz Linkowski
 */
internal class MySuperpomSmokeTestRunner(projectDir: Path, args: List<String>) : AutoCloseable {

  private val gradleRunner: GradleRunner
  private val shadedFiles: List<ShadedFile>

  init {
    gradleRunner = createGradleRunner(projectDir, args)
    shadedFiles = listOf(
            projectDir.shadedGradleProperties,
            projectDir.shadedSettingsGradleKts
    )
  }

  private val Path.shadedGradleProperties
    get() = ShadedFile(resolve("gradle.properties")) { content ->
      content + readTestkitPropertiesContent()
    }

  private val Path.shadedSettingsGradleKts
    get() = ShadedFile(resolve("settings.gradle.kts")) { content ->
      replaceClasspathWithPluginClasspath(content)
    }

  fun build(): BuildResult = gradleRunner.withArguments(gradleRunner.arguments + "--stacktrace").build()

  fun buildAndFail(): BuildResult = gradleRunner.buildAndFail()

  /**
   * Returns relative paths that need to be (temporarily) committed in order to have a clean repo.
   */
  fun dirtyFilepaths(grgit: Grgit) = shadedFiles
          .flatMap { listOf(it.path, it.bakPath) }
          .map { it.toFile().toGrgitPath(grgit) }
          .toSet()

  override fun close() {
    shadedFiles.closeAll()
  }

  private fun createGradleRunner(projectDir: Path, args: List<String>) = GradleRunner.create()
          .withPluginClasspath()
          .withProjectDir(projectDir.toFile())
          .withArguments(args)
          .forwardOutput()

  //region GRADLE.PROPERTIES (duplicated in `MySettingsSmokeTestRunner.kt`)
  private fun readTestkitPropertiesContent(): String = with(GradleRunner::class.java.classLoader) {
    return checkNotNull(getResource("testkit-gradle.properties")).readText()
  }
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
  //endregion
}
