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

import org.ajoberstar.grgit.Grgit
import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.TaskOutcome
import spock.lang.*

import java.nio.file.Files
import java.nio.file.Path

/**
 * @author Tomasz Linkowski
 */
@Stepwise
class MySuperpomGradlePluginSmokeTest extends Specification {

  private static final Path TEST_DATA_DIR = Path.of('../../test-data')
  private static final Path SAMPLE_PROJECT_DIR = TEST_DATA_DIR.resolve('sample-project')

  private static final List<String> SAMPLE_SHARED_FILES = [
          '.idea/codeStyles/Project.xml',
          '.idea/inspectionProfiles/Project_Default.xml',
          'package.json'
  ]

  private static final List<String> TESTED_SUBPROJECTS = [
          'java8-unmodularized',
          'java8-modularized',
          'java11-modularized',
          'kotlin-modularized'
  ]
  private static final List<String> PUBLISHED_SUBPROJECTS = TESTED_SUBPROJECTS + 'test-project'
  private static final List<String> ALL_SUBPROJECTS = PUBLISHED_SUBPROJECTS + 'unpublished'

  @AutoCleanup
  private MySuperpomSmokeTestRunner runner

  def 'gradle cleanImportSharedFiles clean'() {
    given:
      runner = sampleProjectRunner('cleanImportSharedFiles', 'clean')
    when:
      def result = runner.build()
    then:
      SAMPLE_SHARED_FILES.forEach { !sampleProjectFileExists(it) }
    and:
      taskDidNotFail(result, ':clean')
  }

  def 'gradle importSharedFiles build'() {
    given:
      runner = sampleProjectRunner('importSharedFiles', 'build')
    when:
      def result = runner.build()
    then:
      SAMPLE_SHARED_FILES.forEach { sampleProjectFileExists(it) }
    and:
      taskDidNotFail(result, ':build')
      TESTED_SUBPROJECTS.forEach { taskWasSuccessful(result, ":$it:test") }
      ALL_SUBPROJECTS.forEach { taskWasSuccessful(result, ":$it:build") }
  }

  def 'gradle dependencyUpdates'() {
    given:
      runner = sampleProjectRunner('dependencyUpdates')
    when:
      def result = runner.build()
    then:
      taskWasSuccessful(result, ':dependencyUpdates')
      !result.output.matches("(?i)\\b(?:alpha|beta|rc)\\d*\\b")
  }

  def 'gradle release FAILS without reckon.stage=final'() {
    given:
      runner = sampleProjectRunner('release')
    when:
      def result = runner.buildAndFail()
    then:
      result.output.contains("reckon.stage=final")
  }

  def 'gradle release'() {
    given:
      def grgit = Grgit.open()
      def originalHead = grgit.head()
    and:
      runner = sampleProjectRunner('release', '-Preckon.stage=final')
      runner.commitDirtyFilepaths(grgit)
    when:
      def result = runner.build()
    then:
      PUBLISHED_SUBPROJECTS.forEach { taskWasSuccessful(result, ":$it:bintrayUpload") }
      (ALL_SUBPROJECTS - PUBLISHED_SUBPROJECTS).forEach { taskWasSkipped(result, ":$it:bintrayUpload") }

      taskWasSuccessful(result, ':bintrayPublish')

      taskWasSkipped(result, ':releaseToGitHub')
      taskWasSuccessful(result, ':releaseToCentralRepos')

      taskWasSuccessful(result, ':release')
      taskWasSuccessful(result, ':pushUpdatedGradleProperties')
    and:
      grgit.head() != originalHead
    cleanup:
      grgit.reset { commit = originalHead }
  }

  //region HELPERS
  private static MySuperpomSmokeTestRunner sampleProjectRunner(String... args) {
    new MySuperpomSmokeTestRunner(SAMPLE_PROJECT_DIR, Arrays.asList(args))
  }

  private static boolean sampleProjectFileExists(String subpath) {
    Files.exists(SAMPLE_PROJECT_DIR.resolve(subpath))
  }

  private static boolean taskWasSuccessful(BuildResult result, String taskName) {
    result.task(taskName).outcome == TaskOutcome.SUCCESS
  }

  private static boolean taskWasSkipped(BuildResult result, String taskName) {
    result.task(taskName).outcome == TaskOutcome.SKIPPED
  }

  private static boolean taskDidNotFail(BuildResult result, String taskName) {
    result.task(taskName).outcome != TaskOutcome.FAILED
  }
  //endregion
}
