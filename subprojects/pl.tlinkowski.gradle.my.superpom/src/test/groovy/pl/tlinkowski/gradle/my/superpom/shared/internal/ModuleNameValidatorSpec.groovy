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

package pl.tlinkowski.gradle.my.superpom.shared.internal

import spock.lang.Specification

/**
 * @author Tomasz Linkowski
 */
class ModuleNameValidatorSpec extends Specification {

  def isValidModuleName(String moduleName, boolean valid) {
    expect:
      valid == ModuleNameValidator.INSTANCE.isValidModuleName(moduleName)
    where:
      // https://github.com/openjdk/jdk/blob/0e137c973f19bf998f67b6b479b965313a23c2b9/test/jdk/java/lang/module/ModuleNamesTest.java
      moduleName    | valid
      '.'           | false
      '.foo'        | false
      'foo.'        | false
      'foo.bar'     | true
      '..'          | false
      '..foo'       | false
      'foo..'       | false
      'foo..bar'    | false
      ''            | false
      '_foo'        | true
      'foo_'        | true
      '_foo.bar'    | true
      'foo_.bar'    | true
      'foo_bar'     | true
      '$foo'        | true
      'foo$'        | true
      '$foo.bar'    | true
      'foo$.bar'    | true
      'foo$bar'     | true
      // symbols other than _ and $
      ':'           | false
      'foo:bar'     | false
      '-'           | false
      'foo-bar'     | false
      // _ as identifier
      '_'           | false
      'foo._'       | false
      // public as identifier
      'public'      | false
      'foo.public'  | false
      'foo.Public'  | true
      // initial letter
      '1'           | false
      '1a'          | false
      'a1'          | true
      'a1.2'        | false
      'a1.2b'       | false
      'a1.b2'       | true
      // normal module names
      'foo'         | true
      'foo.bar.baz' | true
      'a'           | true
      'a.b'         | true
      'a.b.c'       | true
  }
}
