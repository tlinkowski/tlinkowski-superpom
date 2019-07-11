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

import org.apache.tools.ant.taskdefs.condition.Os

// WORKAROUND FOR: https://github.com/koral--/jacoco-gradle-testkit-plugin/issues/9
if (Os.isFamily(Os.FAMILY_WINDOWS)) {
  tasks {
    "test" {
      fun File.isLocked() = !renameTo(this) // https://stackoverflow.com/a/13706972/2032415

      val waitUntilJacocoTestExecIsUnlocked = Action<Task> {
        val jacocoTestExec = checkNotNull(extensions.getByType(JacocoTaskExtension::class).destinationFile)
        val waitMillis = 100L
        while (jacocoTestExec.isLocked()) {
          logger.info("Waiting $waitMillis ms (${jacocoTestExec.name} is locked)...")
          Thread.sleep(waitMillis)
        }
        logger.info("Done waiting (${jacocoTestExec.name} is unlocked).")
      }

      doLast(waitUntilJacocoTestExecIsUnlocked)
    }
  }
}
