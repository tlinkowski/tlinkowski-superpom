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

package pl.tlinkowski.gradle.my.superpom.internal.shared

import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification

/**
 * @author Tomasz Linkowski
 */
class SuperpomFileSharingSpec extends Specification {

  def zipFileTreeProvider() {
    when:
      SuperpomFileSharing.INSTANCE.zipFileTreeProvider$my_superpom_gradle_plugin("FAKE")
    then:
      thrown(IllegalStateException)
  }

  def exportedResourceDir() {
    given:
      def project = ProjectBuilder.builder().build()
    when:
      def resourceDir = SuperpomFileSharing.INSTANCE.exportedResourceDir$my_superpom_gradle_plugin(project)
    then:
      FileExtensionsKt.toNormalizedString(resourceDir).endsWith(SuperpomFileSharing.RESOURCE_PATH)
  }
}
