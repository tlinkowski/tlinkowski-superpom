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

import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.kotlin.dsl.*
import pl.tlinkowski.gradle.my.superpom.shared.internal.toNormalizedString

/**
 * Enforces a [naming convention for Maven & JPMS by Christian Stein](https://sormuras.github.io/blog/2019-08-04-maven-coordinates-and-java-module-names).
 *
 * @author Tomasz Linkowski
 */
internal class NamingConventionEnforcementPlugin : AbstractRootPlugin() {

  override fun Project.configureRootProject() {
    subprojects {
      ensureProjectAndGroupNameMatch()
      ensureProjectAndModuleNameMatch()
      ensureProjectAndPackageNameMatch()
    }
  }

  private fun Project.ensureProjectAndGroupNameMatch() {
    val group: String by project
    if (!project.name.startsWith("$group.")) {
      throw GradleException("Project name (${project.name}) must start with group ID ($group)")
    }
  }

  private fun Project.ensureProjectAndModuleNameMatch() {
    val moduleName: String? by project // added by ModuleSystemPlugin if `module-info.java` found
    if (moduleName != null && moduleName != project.name) {
      throw GradleException("Project name (${project.name}) must be equal to JPMS module name ($moduleName)")
    }
  }

  private fun Project.ensureProjectAndPackageNameMatch() {
    val requiredPrefix = project.name.replace('.', '/') + '/'
    val rootDir = project.file("src/main")
    val fileTree = project.fileTree(rootDir) {
      exclude("/java/module-info.java")
      exclude("/*/META-INF/")
    }
    fileTree.map { it.relativeTo(rootDir).toPath() }
            .map { it.subpath(1, it.nameCount) } // skip /java, /kotlin, etc.
            .map { it.toFile().toNormalizedString() }
            .filter { !it.startsWith(requiredPrefix) }
            .forEach {
              throw GradleException("$it has package inconsistent with its project name (${project.name})")
            }
  }
}
