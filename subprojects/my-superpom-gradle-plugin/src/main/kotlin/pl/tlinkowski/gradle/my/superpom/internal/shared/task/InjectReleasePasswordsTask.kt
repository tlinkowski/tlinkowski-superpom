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

package pl.tlinkowski.gradle.my.superpom.internal.shared.task

import com.jfrog.bintray.gradle.tasks.BintrayUploadTask
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.*
import pl.tlinkowski.gradle.my.superpom.internal.shared.*
import javax.swing.JPasswordField

/**
 * A custom task that configures upload tasks with passwords necessary to perform a complete release.
 *
 * @author Tomasz Linkowski
 */
internal open class InjectReleasePasswordsTask : DefaultTask() {

  init {
    group = TaskGroupNames.INTERNAL
  }

  @TaskAction
  fun injectReleasePasswords() {
    val bintrayApiKey: String by project // from ~/.gradle/gradle.properties
    val gnupgPassphrase = requestPassword("GnuPG passphrase")
    val sonatypePassword = requestPassword("Sonatype password")

    // needs to be done directly on the tasks (at this point, the values have already been copied from the extension)
    project.subprojects {
      tasks.named<BintrayUploadTask>("bintrayUpload") {
        apiKey = bintrayApiKey
        gpgPassphrase = gnupgPassphrase
        ossPassword = sonatypePassword
      }
    }
  }

  private fun requestPassword(prompt: String): String {
    logger.info("Requesting {}", prompt)
    return if (project.isDryRunRelease) "*" else requestPasswordUsingSwing(prompt)
  }

  private fun requestPasswordUsingSwing(prompt: String): String {
    // source: https://stackoverflow.com/a/8881370/2032415
    val passwordField = JPasswordField()
    if (!answerQuestionInExplicitSwingDialog(passwordField, "$prompt (${project.name})")) {
      throw GradleException("Refused to provide $prompt")
    }
    return String(passwordField.password)
  }
}
