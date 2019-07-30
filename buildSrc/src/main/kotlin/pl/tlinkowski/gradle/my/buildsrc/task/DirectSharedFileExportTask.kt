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

package pl.tlinkowski.gradle.my.buildsrc.task

import org.gradle.api.tasks.Copy
import pl.tlinkowski.gradle.my.superpom.internal.shared.SuperpomFileSharing
import pl.tlinkowski.gradle.my.superpom.internal.shared.TaskGroupNames
import java.io.File

/**
 * A task that packages files specified by [from] to a `shared-$subname-files.zip` archive in exported resources dir.
 *
 * @author Tomasz Linkowski
 */
open class DirectSharedFileExportTask : Copy() {

  init {
    group = TaskGroupNames.FILE_SHARING
  }

  /**
   * Configures this task to copy [file] into the exported resource dir.
   */
  fun fromFile(file: File) {
    description = "Exports a file directly (${file.name})"
    from(file)
    into(SuperpomFileSharing.exportedResourceDir(project))
  }
}
