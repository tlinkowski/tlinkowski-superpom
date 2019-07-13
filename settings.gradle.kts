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
pluginManagement {
  //region SOLVES: https://github.com/gradle/gradle/issues/1697
  resolutionStrategy {
    eachPlugin {
      if (requested.id.namespace == "org.jetbrains.kotlin") {
        val kotlinVersion: String by settings
        useVersion(kotlinVersion)
      }
      if (requested.id.namespace == "org.kordamp.gradle") {
        val kordampVersion: String by settings
        useVersion(kordampVersion)
      }
    }
  }
  //endregion
}

//region SEE: https://aalmiray.github.io/kordamp-gradle-plugins/#_org_kordamp_gradle_settings
buildscript {
  repositories {
    gradlePluginPortal()
  }
  dependencies {
    val kordampVersion: String by settings
    classpath(group = "org.kordamp.gradle", name = "settings-gradle-plugin", version = kordampVersion)
  }
}
apply(plugin = "org.kordamp.gradle.settings")
//endregion

rootProject.name = "tlinkowski-superpom"

configure<org.kordamp.gradle.plugin.settings.ProjectsExtension> {
  directories = listOf("subprojects")
}
