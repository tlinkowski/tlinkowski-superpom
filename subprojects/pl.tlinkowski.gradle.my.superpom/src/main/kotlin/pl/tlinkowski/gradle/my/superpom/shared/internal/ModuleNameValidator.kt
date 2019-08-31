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

/**
 * Validates given string as a JPMS module name.
 *
 * @author Tomasz Linkowski
 */
internal object ModuleNameValidator {

  // https://docs.oracle.com/javase/specs/jls/se12/html/jls-3.html#jls-Keyword
  // https://docs.oracle.com/javase/specs/jls/se12/html/jls-3.html#jls-BooleanLiteral
  // https://docs.oracle.com/javase/specs/jls/se12/html/jls-3.html#jls-NullLiteral
  private val keywordOrLiteral = Regex(
          "abstract|continue|for|new|switch|assert|default|if|package|synchronized|boolean|do|goto|private|this|break|double|implements|protected|throw|byte|else|import|public|throws|case|enum|instanceof|return|transient|catch|extends|int|short|try|char|final|interface|static|void|class|finally|long|strictfp|volatile|const|float|native|super|while|_|true|false|null"
  ).pattern

  // https://docs.oracle.com/javase/specs/jls/se12/html/jls-3.html#jls-Identifier
  private val identifier = Regex("(?!(?:$keywordOrLiteral)(?:\\.|$))[A-Za-z_$][\\w$]*+").pattern

  // https://docs.oracle.com/javase/specs/jls/se12/html/jls-7.html#jls-7.7
  private val validModuleNameRegex = Regex("$identifier(\\.$identifier)*+")

  /**
   * Returns `true` if given `moduleName` is a valid JPMS module name as per
   * [Java Language Specification](https://docs.oracle.com/javase/specs/jls/se12/html/jls-7.html#jls-7.7).
   */
  fun isValidModuleName(moduleName: String) = validModuleNameRegex.matches(moduleName)
}
