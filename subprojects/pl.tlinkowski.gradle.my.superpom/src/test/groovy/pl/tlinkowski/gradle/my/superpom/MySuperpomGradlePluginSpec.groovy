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

package pl.tlinkowski.gradle.my.superpom

import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.kordamp.gradle.plugin.base.ProjectConfigurationExtension
import spock.lang.Specification

/**
 * @author Tomasz Linkowski
 */
class MySuperpomGradlePluginSpec extends Specification {

  private Project project

  def 'plugin can be applied to a multi-project root'() {
    given:
      project = newMultiProject()
    when:
      applyMySuperpomPlugin()
    then:
      kordampConfig().info.people.people[0].id == 'tlinkowski'
      kordampConfig().licensing.licenses.licenses[0].id == 'Apache-2.0'
  }

  def 'plugin cannot be applied to a subproject'() {
    given:
      project = newMultiProject().subprojects.first()
    when:
      applyMySuperpomPlugin()
    then:
      thrown(GradleException)
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

  private void applyMySuperpomPlugin() {
    project.pluginManager.apply MySuperpomGradlePlugin
  }

  private ProjectConfigurationExtension kordampConfig() {
    project.extensions.getByType ProjectConfigurationExtension
  }
}
