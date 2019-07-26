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

/**
 * Configuration shared between this project and the projects to which the SuperPOM plugin is applied.
 *
 * @author Tomasz Linkowski
 */
class MyCompleteSharedConfigPlugin : AbstractRootPlugin() {

  override fun Project.configureRootProject() {
    apply {
      plugin(VersionConfigPlugin::class) // adds project.grgit + modified project.version

      plugin(MyCoreConfigPlugin::class)
      plugin(ModularityConfigPlugin::class)
      plugin(TestConfigPlugin::class)
      plugin(JacocoConfigPlugin::class)
      plugin(MyCentralPublishConfigPlugin::class)

      plugin(DependencyUpdatesConfigPlugin::class)
    }
  }
}
