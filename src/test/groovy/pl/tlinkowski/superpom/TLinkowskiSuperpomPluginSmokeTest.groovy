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

import org.gradle.testkit.runner.*
import spock.lang.*

import java.nio.file.*

/**
 * @author Tomasz Linkowski
 */
@Stepwise
class TLinkowskiSuperpomPluginSmokeTest extends Specification {

  private static final Path TEST_DATA_DIR = Path.of('test-data')
  private static final Path SAMPLE_PROJECT_DIR = TEST_DATA_DIR.resolve('sample-project')

  private static final String IDEA_CODE_STYLES_XML = '.idea/codeStyles/Project.xml'
  private static final String IDEA_INSPECTION_PROFILES_XML = '.idea/inspectionProfiles/Project_Default.xml'

  @AutoCleanup
  private SmokeTestRunner runner

  def 'gradle cleanImportSharedFiles clean'() {
    given:
      runner = sampleProjectRunner('cleanImportSharedFiles', 'clean')
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
      runner = sampleProjectRunner('importSharedFiles', 'build')
    when:
      def result = runner.build()
    then:
      sampleProjectFileExists(IDEA_CODE_STYLES_XML)
      sampleProjectFileExists(IDEA_INSPECTION_PROFILES_XML)
    and:
      result.task(':build').getOutcome() == TaskOutcome.SUCCESS
  }

  //region HELPERS
  private static SmokeTestRunner sampleProjectRunner(String... tasks) {
    new SmokeTestRunner(SAMPLE_PROJECT_DIR, tasks)
  }

  private static boolean sampleProjectFileExists(String subpath) {
    Files.exists(SAMPLE_PROJECT_DIR.resolve(subpath))
  }
  //endregion

  /**
   * Special class that handles setting up and cleaning up an appropriately modified `gradle.properties` file.
   */
  private static class SmokeTestRunner implements AutoCloseable {

    private final GradleRunner gradleRunner
    private final Path gradlePropertiesPath
    private final Path gradlePropertiesBakPath

    SmokeTestRunner(Path projectDir, String... tasks) {
      gradleRunner = createGradleRunner(projectDir, tasks)
      gradlePropertiesPath = projectDir.resolve('gradle.properties')
      gradlePropertiesBakPath = projectDir.resolve('gradle.properties.bak')

      setUpModifiedGradleProperties()
    }

    @Override
    void close() {
      cleanUpModifiedGradleProperties()
    }

    BuildResult build() {
      gradleRunner.build()
    }

    private static GradleRunner createGradleRunner(Path projectDir, String... tasks) {
      GradleRunner.create()
              .withPluginClasspath()
              .withProjectDir(projectDir.toFile())
              .withArguments(tasks)
              .forwardOutput()
    }

    //region MODIFIED GRADLE PROPERTIES
    private void setUpModifiedGradleProperties() {
      Files.copy(gradlePropertiesPath, gradlePropertiesBakPath)

      def testkitContent = GradleRunner.class.classLoader.getResourceAsStream('testkit-gradle.properties').text
      Files.writeString(gradlePropertiesPath, testkitContent, StandardOpenOption.APPEND)
    }

    private void cleanUpModifiedGradleProperties() {
      Files.move(gradlePropertiesBakPath, gradlePropertiesPath, StandardCopyOption.REPLACE_EXISTING)
    }
    //endregion
  }
}
