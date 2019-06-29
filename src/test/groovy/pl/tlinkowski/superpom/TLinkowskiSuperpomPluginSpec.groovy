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

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.kordamp.gradle.plugin.base.ProjectConfigurationExtension
import spock.lang.Specification

/**
 * @author Tomasz Linkowski
 */
class TLinkowskiSuperpomPluginSpec extends Specification {

  private Project project

  def 'plugin sets proper license'() {
    given:
      project = newEmptyProject()
    when:
      applyTLinkowskiPlugin()
    then:
      kordampConfig().licensing.licenses.licenses[0].id == 'Apache-2.0'
  }

  def 'all tasks can be configured'() {
    given:
      project = newMultiProject()
    when:
      applyTLinkowskiPlugin()
      configureAllTasks()
    then:
      noExceptionThrown()
  }

  //region PROJECT HELPERS
  private static Project newMultiProject() {
    def rootProject = newEmptyProject()
    newSubprojectOf(rootProject)
    rootProject
  }

  private static Project newEmptyProject() {
    ProjectBuilder.builder().build()
  }

  private static Project newSubprojectOf(Project parent) {
    ProjectBuilder.builder().withParent(parent).build()
  }
  //endregion

  private void applyTLinkowskiPlugin() {
    project.pluginManager.apply TLinkowskiSuperpomPlugin
  }

  /**
   * Necessary for high code coverage - causes all tasks to be eagerly configured.
   *
   * See: https://docs.gradle.org/current/userguide/task_configuration_avoidance.html
   */
  private configureAllTasks() {
    project.tasks.forEach({})
  }

  private ProjectConfigurationExtension kordampConfig() {
    project.extensions.getByType ProjectConfigurationExtension
  }
}
