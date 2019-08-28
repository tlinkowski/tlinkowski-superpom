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

package pl.tlinkowski.gradle.my.superpom.internal.task

import org.gradle.api.tasks.Delete
import pl.tlinkowski.gradle.my.superpom.shared.internal.SuperpomFileSharing
import pl.tlinkowski.gradle.my.superpom.shared.internal.TaskGroupNames

/**
 * A task that removes files extracted using [ZipSharedFileImportTask].
 *
 * @author Tomasz Linkowski
 */
internal open class CleanZipSharedFileImportTask : Delete() {

  init {
    group = TaskGroupNames.FILE_SHARING
  }

  /**
   * Configures this task to delete the files for given [key].
   */
  fun forKey(key: String) {
    description = "Cleans shared $key files imported from the SuperPOM plugin"

    val fileTreeProvider = SuperpomFileSharing.zipFileTreeProvider(key)
    delete(fileTreeProvider(project))
  }
}
