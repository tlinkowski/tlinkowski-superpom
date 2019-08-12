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

package pl.tlinkowski.gradle.my.buildsrc.plugin

import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.kotlin.dsl.*
import org.kordamp.gradle.plugin.base.ProjectConfigurationExtension
import org.kordamp.gradle.plugin.base.plugins.util.PublishingUtils
import pl.tlinkowski.gradle.my.superpom.shared.internal.plugin.AbstractRootPlugin

/**
 * Workaround for https://github.com/tlinkowski/tlinkowski-superpom/issues/50
 *
 * @author Tomasz Linkowski
 */
class IncompletePluginMavenPublicationWorkaroundPlugin : AbstractRootPlugin() {

  override fun Project.configureRootProject() {
    subprojects {
      afterEvaluate {
        configurePluginMavenPublications()
      }
    }
  }

  private fun Project.configurePluginMavenPublications() {
    configure<PublishingExtension> {
      publications {
        val effectiveConfig: ProjectConfigurationExtension by project
        listOf("pluginMaven", "${effectiveConfig.plugin.pluginName}PluginMarkerMaven").forEach {
          named<MavenPublication>(it) {
            PublishingUtils.configurePom(pom, effectiveConfig, effectiveConfig.publishing.pom)
          }
        }
      }
    }
  }
}
