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
import org.gradle.api.tasks.bundling.Jar
import org.gradle.kotlin.dsl.*
import org.javamodularity.moduleplugin.ModuleSystemPlugin
import org.javamodularity.moduleplugin.extensions.TestModuleOptions
import pl.tlinkowski.gradle.my.superpom.shared.internal.ModuleNameValidator
import pl.tlinkowski.gradle.my.superpom.shared.internal.configureIfPresent

/**
 * Applies and configures [Gradle Modules Plugin](https://github.com/java9-modularity/gradle-modules-plugin).
 *
 * @author Tomasz Linkowski
 */
internal class ModularityConfigPlugin : AbstractRootPlugin() {

  override fun Project.configureRootProject() {
    subprojects {
      configureSubproject()
    }
  }

  //region SUBPROJECT CONFIG
  private fun Project.configureSubproject() {
    // https://github.com/java9-modularity/gradle-modules-plugin
    apply<ModuleSystemPlugin>()

    if (!hasProperty("moduleName")) { // added by ModuleSystemPlugin if `module-info.java` found
      validateAutomaticModuleName()
      configureAutomaticModuleName()
    }

    tasks {
      configureModularityForTasks()
    }
  }

  private fun Project.validateAutomaticModuleName() {
    if (!ModuleNameValidator.isValidModuleName(project.name)) {
      throw GradleException("Invalid automatic module name: ${project.name}")
    }
  }

  private fun Project.configureAutomaticModuleName() {
    tasks.named<Jar>("jar") {
      inputs.property("project.name", project.name)

      manifest {
        attributes["Automatic-Module-Name"] = project.name
      }
    }
  }

  private fun TaskContainerScope.configureModularityForTasks() {
    "test" {
      // https://github.com/java9-modularity/gradle-modules-plugin#fall-back-to-classpath-mode
      // extension will be absent if `module-info.java` is not found for given subproject
      configureIfPresent<TestModuleOptions> {
        runOnClasspath = true // Groovy doesn't work on module-path yet
      }
    }

    "javadoc" {
      // workaround for: https://github.com/tlinkowski/tlinkowski-superpom/issues/30
      if (project.isModularizedKotlinProject()) {
        logger.debug("Disabling 'javadoc' task")
        enabled = false
      }
    }
  }

  private fun Project.isModularizedKotlinProject() = !fileTree("src/main/kotlin").isEmpty
          && fileTree("src/main/java").files == setOf(file("src/main/java/module-info.java"))
}
