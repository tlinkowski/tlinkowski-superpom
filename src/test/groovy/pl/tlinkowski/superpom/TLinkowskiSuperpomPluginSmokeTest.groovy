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

import java.nio.file.Path

/**
 * @author Tomasz Linkowski
 */
class TLinkowskiSuperpomPluginSmokeTest extends Specification {

  private static final String GRADLE_VERSION = '5.4.1'
  private static final Path TEST_DATA_DIR = Path.of('test-data')

  def smokeTest() {
    given:
      def runner = GradleRunner.create()
              .withGradleVersion(GRADLE_VERSION)
              .withPluginClasspath()
              .withProjectDir(TEST_DATA_DIR.resolve('sample-project').toFile())
              .withArguments('clean', 'build')
              .forwardOutput()
    when:
      def result = runner.build()
    then:
      TaskOutcome.SUCCESS == result.task(':build').getOutcome()
  }
}
