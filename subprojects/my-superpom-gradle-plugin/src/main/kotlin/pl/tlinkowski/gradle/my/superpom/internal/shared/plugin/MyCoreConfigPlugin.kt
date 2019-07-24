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
import org.kordamp.gradle.plugin.project.ProjectPlugin

/**
 * Applies [Kordamp Project Plugin](https://aalmiray.github.io/kordamp-gradle-plugins/#_org_kordamp_gradle_project).
 *
 * Configures core stuff (including Kordamp).
 *
 * @author Tomasz Linkowski
 */
internal class MyCoreConfigPlugin : AbstractRootPlugin() {

  override fun Project.configureRootProject() {
    apply {
      // https://aalmiray.github.io/kordamp-gradle-plugins/#_org_kordamp_gradle_project
      plugin(ProjectPlugin::class)
    }

    configure<ProjectConfigurationExtension> {
      configureCoreKordamp()
    }

    allprojects {
      // https://docs.gradle.org/current/userguide/idea_plugin.html
      apply(plugin = "idea")

      repositories {
        mavenCentral()
      }
    }
  }

  private fun ProjectConfigurationExtension.configureCoreKordamp() {
    // https://aalmiray.github.io/kordamp-gradle-plugins/#_org_kordamp_gradle_base_dsl
    info {
      vendor = "Tomasz Linkowski"

      people {
        person {
          id = "tlinkowski"
          name = "Tomasz Linkowski"
          url = "https://tlinkowski.pl/"
          roles = listOf("developer")
        }
      }
    }

    licensing {
      licenses {
        license {
          id = "Apache-2.0"
        }
      }
    }
  }
}