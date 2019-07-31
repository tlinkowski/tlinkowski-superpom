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

repositories {
  gradlePluginPortal()
}

dependencies {
  val compile by configurations

  // https://kotlinlang.org/docs/reference/using-gradle.html
  val kotlinVersion: String by project
  compile(group = "org.jetbrains.kotlin", name = "kotlin-gradle-plugin", version = kotlinVersion)

  // https://aalmiray.github.io/kordamp-gradle-plugins/#_org_kordamp_gradle_project
  val kordampVersion: String by project
  compile(group = "org.kordamp.gradle", name = "project-gradle-plugin", version = kordampVersion)

  // https://github.com/java9-modularity/gradle-modules-plugin
  val modularityVersion: String by project
  compile(group = "org.javamodularity", name = "moduleplugin", version = modularityVersion)

  // https://github.com/ajoberstar/grgit/
  val grgitVersion: String by project
  compile(group = "org.ajoberstar.grgit", name = "grgit-core", version = grgitVersion)

  // https://github.com/ajoberstar/reckon/
  val reckonVersion: String by project
  compile(group = "org.ajoberstar.reckon", name = "reckon-gradle", version = reckonVersion)
}
