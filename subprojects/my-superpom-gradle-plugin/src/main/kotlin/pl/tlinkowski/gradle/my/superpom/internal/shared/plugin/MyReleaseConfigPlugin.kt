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
package pl.tlinkowski.gradle.my.superpom.internal.shared.plugin

import org.gradle.api.Project
import org.gradle.kotlin.dsl.*
import org.kordamp.gradle.plugin.base.ProjectConfigurationExtension

/**
 * Configures things related to releasing the project.
 *
 * @author Tomasz Linkowski
 */
internal class MyReleaseConfigPlugin : AbstractRootPlugin() {

  override fun Project.configureRootProject() {
    // https://aalmiray.github.io/kordamp-gradle-plugins/#_org_kordamp_gradle_base_dsl
    configure<ProjectConfigurationExtension> {
      release = rootProject.hasProperty("release")

      info {
        buildInfo {
          // for reproducible builds: https://aalmiray.github.io/kordamp-gradle-plugins/#_reproducible_builds
          skipBuildBy = true
          skipBuildDate = true
          skipBuildTime = true
        }
      }
    }
  }
}
