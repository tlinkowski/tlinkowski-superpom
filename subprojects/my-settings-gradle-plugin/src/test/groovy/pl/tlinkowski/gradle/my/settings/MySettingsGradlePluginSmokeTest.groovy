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

package pl.tlinkowski.gradle.my.settings

import org.gradle.testkit.runner.TaskOutcome
import spock.lang.Specification

/**
 * @author Tomasz Linkowski
 */
class MySettingsGradlePluginSmokeTest extends Specification {

  private def runner = new MySettingsSmokeTestRunner()

  def 'plugin loads `sample` subproject'() {
    given:
      runner.createSubprojectsDir()
      runner.createSubproject("sample")
    when:
      def result = runner.build()
    then:
      result.task(":build").outcome == TaskOutcome.SUCCESS
      result.task(":sample:build").outcome == TaskOutcome.SUCCESS
  }

  def 'plugin resolves version for pl.tlinkowski.gradle.superpom using custom resolution strategy'() {
    given:
      def superpomPluginId = 'pl.tlinkowski.gradle.my.superpom'
      def mySuperpomVersion = 'dummy'
    and:
      runner.createSubprojectsDir()
      runner.file('gradle.properties').text += """
        mySuperpomVersion=$mySuperpomVersion
        """
      runner.file('build.gradle.kts').text = """
        plugins {
          id("$superpomPluginId")
        }
        """
    when:
      def result = runner.buildAndFail()
    then:
      result.output.contains("[id: '$superpomPluginId', version: '$mySuperpomVersion']")
  }

  def 'plugin fails when `subprojects` dir is missing'() {
    given:
      def settingsPluginId = 'pl.tlinkowski.gradle.my.settings'
    when:
      def result = runner.buildAndFail()
    then:
      result.output.contains("Failed to apply plugin [id '$settingsPluginId']")
  }
}
