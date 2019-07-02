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

import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import spock.lang.Specification
import spock.lang.Stepwise

import java.nio.file.Files
import java.nio.file.Path

/**
 * @author Tomasz Linkowski
 */
@Stepwise
class TLinkowskiSuperpomPluginSmokeTest extends Specification {

  private static final String GRADLE_VERSION = '5.4.1'
  private static final Path TEST_DATA_DIR = Path.of('test-data')
  private static final Path SAMPLE_PROJECT_DIR = TEST_DATA_DIR.resolve('sample-project')

  private static final String IDEA_CODE_STYLES_XML = '.idea/codeStyles/Project.xml'
  private static final String IDEA_INSPECTION_PROFILES_XML = '.idea/inspectionProfiles/Project_Default.xml'

  def 'gradle cleanImportSharedFiles clean'() {
    given:
      def runner = sampleProjectRunner('cleanImportSharedFiles', 'clean')
    when:
      def result = runner.build()
    then:
      !sampleProjectFileExists(IDEA_CODE_STYLES_XML)
      !sampleProjectFileExists(IDEA_INSPECTION_PROFILES_XML)
    and:
      result.task(':clean').getOutcome() != TaskOutcome.FAILED
  }

  def 'gradle importSharedFiles build'() {
    given:
      def runner = sampleProjectRunner('importSharedFiles', 'build')
    when:
      def result = runner.build()
    then:
      sampleProjectFileExists(IDEA_CODE_STYLES_XML)
      sampleProjectFileExists(IDEA_INSPECTION_PROFILES_XML)
    and:
      result.task(':build').getOutcome() == TaskOutcome.SUCCESS
  }

  //region HELPERS
  private static GradleRunner gradleRunner(Path projectDir, String... tasks) {
    GradleRunner.create()
            .withGradleVersion(GRADLE_VERSION)
            .withPluginClasspath()
            .withProjectDir(projectDir.toFile())
            .withArguments(tasks)
            .forwardOutput()
  }

  private static GradleRunner sampleProjectRunner(String... tasks) {
    gradleRunner(SAMPLE_PROJECT_DIR, tasks)
  }

  private static boolean sampleProjectFileExists(String subpath) {
    Files.exists(SAMPLE_PROJECT_DIR.resolve(subpath))
  }
  //endregion
}
