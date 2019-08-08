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
import org.gradle.api.plugins.JavaBasePlugin
import org.gradle.kotlin.dsl.*
import org.gradle.plugins.ide.idea.IdeaPlugin
import org.gradle.plugins.ide.idea.model.IdeaModel
import org.kordamp.gradle.plugin.base.ProjectConfigurationExtension
import org.kordamp.gradle.plugin.licensing.LicensingPlugin
import org.kordamp.gradle.plugin.publishing.PublishingPlugin

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
      // https://aalmiray.github.io/kordamp-gradle-plugins/#_org_kordamp_gradle_licensing
      // (the rest of Kordamp's plugins is applied through `BintrayPlugin`)
      plugin(LicensingPlugin::class)

      // https://aalmiray.github.io/kordamp-gradle-plugins/#_org_kordamp_gradle_publishing
      plugin(PublishingPlugin::class) // without this applied early, we ran into problems (e.g. #39)
    }

    configure<ProjectConfigurationExtension> {
      configureCoreKordamp()
    }

    subprojects {
      configureSubproject()
    }

    allprojects {
      configureEveryProject()
    }

    configureRootIdea()
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

      specification.enabled = false
      implementation.enabled = false
    }

    licensing {
      licenses {
        license {
          id = "Apache-2.0"
        }
      }
    }
  }

  private fun Project.configureSubproject() {
    apply {
      // https://guides.gradle.org/designing-gradle-plugins/#capabilities-vs-conventions
      plugin(JavaBasePlugin::class)
    }
  }

  private fun Project.configureEveryProject() {
    apply {
      // https://docs.gradle.org/current/userguide/idea_plugin.html
      plugin(IdeaPlugin::class)
    }

    repositories {
      mavenCentral()
    }
  }

  private fun Project.configureRootIdea() {
    configure<IdeaModel> {
      module {
        excludeDirs.add(file("node_modules"))
      }
    }
  }
}
