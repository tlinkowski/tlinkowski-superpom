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

package pl.tlinkowski.gradle.my.superpom.internal.shared.task.generic

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import pl.tlinkowski.gradle.my.superpom.internal.shared.TaskGroupNames
import pl.tlinkowski.gradle.my.superpom.internal.shared.isDryRunRelease

/**
 * Asks a "Yes/No" question in the console, and throws a [GradleException] if the answer is not "Yes".
 *
 * If it's a dry run, the question is automatically answered as "Yes".
 *
 * @author Tomasz Linkowski
 */
internal open class ConsoleYesNoQuestionTask : DefaultTask() {

  @get:Input
  lateinit var prompt: String

  init {
    group = TaskGroupNames.INTERNAL
  }

  @TaskAction
  fun askYesNoQuestion() {
    println("=== $prompt [y/N] ===")
    if (!answerYesToQuestion()) {
      throw GradleException("Wrong answer to: $prompt")
    }
  }

  private fun answerYesToQuestion(): Boolean {
    if (project.isDryRunRelease) {
      println("y [dry run]")
      return true
    }

    val answer = readLine()
    return answer.equals("y", ignoreCase = true)
  }
}
