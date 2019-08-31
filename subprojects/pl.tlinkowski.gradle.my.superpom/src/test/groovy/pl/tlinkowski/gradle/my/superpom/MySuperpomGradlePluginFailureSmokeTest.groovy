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

import spock.lang.*

import java.nio.file.Path

/**
 * @author Tomasz Linkowski
 */
class MySuperpomGradlePluginFailureSmokeTest extends Specification {

  private static final Path TEST_DATA_DIR = Path.of('../../test-data')
  private static final Path FAILURE_PROJECT_DIR = TEST_DATA_DIR.resolve('failure-project')

  @AutoCleanup
  private MySuperpomSmokeTestRunner runner

  def 'invalid automatic module name detected'() {
    given:
      def projectName = 'invalid-module-name'
      runner = failureProjectRunner(projectName)
    when:
      def result = runner.buildAndFail()
    then:
      result.output.contains('Invalid automatic module name')
  }

  def 'project and group name mismatch detected'() {
    given:
      def group = 'pl.tlinkowski.fail'
      def projectName = 'pl.tlinkowski.fail2.mismatch.group'
      runner = failureProjectRunner(projectName)
    when:
      def result = runner.buildAndFail()
    then:
      result.output.contains("Project name ($projectName) must start with group ID ($group)")
  }

  def 'project and module name mismatch detected'() {
    given:
      def projectName = 'pl.tlinkowski.fail.mismatch.module'
      def moduleName = 'pl.tlinkowski.fail.mismatch.module2'
      runner = failureProjectRunner(projectName)
    when:
      def result = runner.buildAndFail()
    then:
      result.output.contains("Project name ($projectName) must be equal to JPMS module name ($moduleName)")
  }

  def 'project and package name mismatch detected'() {
    given:
      def sourceFile = 'pl/tlinkowski/fail/mismatch/package2/Sample.java'
      def projectName = 'pl.tlinkowski.fail.mismatch.package'
      runner = failureProjectRunner(projectName)
    when:
      def result = runner.buildAndFail()
    then:
      result.output.contains("$sourceFile has package inconsistent with its project name ($projectName)")
  }

  private static MySuperpomSmokeTestRunner failureProjectRunner(String projectName) {
    new MySuperpomSmokeTestRunner(FAILURE_PROJECT_DIR, [
            '-c', 'failure.settings.gradle.kts',
            "-PtestedProjectName=$projectName".toString(),
            ":$projectName:build".toString()
    ])
  }
}
