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

import spock.lang.Specification
import spock.lang.Unroll

/**
 * @author Tomasz Linkowski
 */
class MySuperpomSharedFileAccessSpec extends Specification {

  def "plugin version can be read"() {
    when:
      def pluginVersion = MySuperpomSharedFileAccess.INSTANCE.readPluginVersion()
    then:
      !pluginVersion.isBlank()
  }

  @Unroll
  def "#fileName can be loaded"(String fileName) {
    when:
      MySuperpomSharedFileAccess.INSTANCE.exportedResourceAsStream(fileName).close()
    then:
      noExceptionThrown()
    where:
      fileName                   | _
      "shared-gradle.properties" | _
      "shared-idea-files.zip"    | _
  }

  def "missing resource throws an exception"() {
    when:
      MySuperpomSharedFileAccess.INSTANCE.exportedResourceAsStream("FAKE.zip")
    then:
      thrown(IllegalArgumentException)
  }
}
