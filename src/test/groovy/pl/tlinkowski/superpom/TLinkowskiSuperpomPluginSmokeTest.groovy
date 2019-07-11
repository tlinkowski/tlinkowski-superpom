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

  def 'gradle dependencyUpdates'() {
    given:
      runner = sampleProjectRunner('dependencyUpdates')
    when:
      def result = runner.build()
    then:
      result.task(':dependencyUpdates').getOutcome() == TaskOutcome.SUCCESS
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
    private final Path settingsGradleKtsPath

    SmokeTestRunner(Path projectDir, String... tasks) {
      gradleRunner = createGradleRunner(projectDir, tasks)
      gradlePropertiesPath = projectDir.resolve('gradle.properties')
      settingsGradleKtsPath = projectDir.resolve('settings.gradle.kts')

      setUpModifiedGradleProperties()
      setUpModifiedSettingsGradleKts()
    }

    @Override
    void close() {
      cleanUpModifiedGradleProperties()
      cleanUpModifiedSettingsGradleKts()
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

    //region MODIFIED GRADLE.PROPERTIES
    private void setUpModifiedGradleProperties() {
      backupFile(gradlePropertiesPath)
      Files.writeString(gradlePropertiesPath, readTestkitPropertiesContent(), StandardOpenOption.APPEND)
    }

    private void cleanUpModifiedGradleProperties() {
      restoreFile(gradlePropertiesPath)
    }

    private static String readTestkitPropertiesContent() {
      GradleRunner.class.classLoader.getResourceAsStream('testkit-gradle.properties').text
    }
    //endregion

    //region MODIFIED SETTINGS.GRADLE.KTS
    private void setUpModifiedSettingsGradleKts() {
      backupFile(settingsGradleKtsPath)
      Files.writeString(settingsGradleKtsPath, replaceWithPluginClasspath(Files.readString(settingsGradleKtsPath)))
    }

    private void cleanUpModifiedSettingsGradleKts() {
      restoreFile(settingsGradleKtsPath)
    }

    // https://github.com/gradle/gradle/blob/6b1ced257444b04ca4f93a4e0d624d0f1903e42c/subprojects/test-kit/src/integTest/groovy/org/gradle/testkit/TestKitEndUserIntegrationTest.groovy#L354-L365
    private String replaceWithPluginClasspath(String content) {
      def dependencyNotation = 'group = "pl.tlinkowski.superpom", name = "tlinkowski-superpom", version = null'
      def pluginClasspath = gradleRunner.pluginClasspath
              .collect { file -> '"' + toNormalizedString(file) + '"' }
              .join(', ')
      content.replace(dependencyNotation, "files($pluginClasspath)")
    }

    private static String toNormalizedString(File file) {
      file.path.replace('\\', '/')
    }
    //endregion

    private static void backupFile(Path path) {
      Files.copy(path, toBackupPath(path))
    }

    private static void restoreFile(Path path) {
      Files.move(toBackupPath(path), path, StandardCopyOption.REPLACE_EXISTING)
    }

    private static Path toBackupPath(Path path) {
      Path.of(path.toString() + '.bak')
    }
  }
}
