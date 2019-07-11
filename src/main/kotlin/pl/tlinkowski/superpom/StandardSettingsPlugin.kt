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
import org.gradle.api.initialization.Settings
import org.gradle.kotlin.dsl.*
import org.kordamp.gradle.plugin.settings.ProjectsExtension

/**
 * A wrapper over Kordamp Settings Plugin.
 *
 * @see [https://aalmiray.github.io/kordamp-gradle-plugins/#_org_kordamp_gradle_settings]
 *
 * @author Tomasz Linkowski
 */
class StandardSettingsPlugin : Plugin<Settings> {

  override fun apply(settings: Settings) {
    settings.configure()
  }

  private fun Settings.configure() {
    apply(plugin = "org.kordamp.gradle.settings")

    configure<ProjectsExtension> {
      directories = listOf("subprojects")
    }
  }
}
