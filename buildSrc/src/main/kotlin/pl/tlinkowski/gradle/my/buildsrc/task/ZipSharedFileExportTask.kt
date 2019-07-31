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

import org.gradle.api.tasks.bundling.Zip
import org.gradle.api.tasks.bundling.ZipEntryCompression
import pl.tlinkowski.gradle.my.superpom.internal.shared.SuperpomFileSharing
import pl.tlinkowski.gradle.my.superpom.internal.shared.TaskGroupNames

/**
 * A task that packages files specified by given `key` to a `shared-$key-files.zip` archive in exported resources dir.
 *
 * Counterpart of `ZipSharedFileImportTask` from `my-superpom-gradle-plugin`.
 *
 * @author Tomasz Linkowski
 */
internal open class ZipSharedFileExportTask : Zip() {

  init {
    group = TaskGroupNames.FILE_SHARING
    destinationDirectory.set(SuperpomFileSharing.exportedResourceDir(project))
    entryCompression = ZipEntryCompression.STORED
  }

  /**
   * Configures this task to archive files specified by [key].
   */
  fun forKey(key: String) {
    fromFileTree(key)
    intoExportedZipFile(key)
  }

  private fun fromFileTree(key: String) {
    val fileTreeProvider = SuperpomFileSharing.zipFileTreeProvider(key)
    from(fileTreeProvider(project))
  }

  /**
   * Counterpart of `ZipSharedFileImportTask.fromExportedZipFile` from `my-superpom-gradle-plugin`.
   */
  private fun intoExportedZipFile(key: String) {
    val filename = SuperpomFileSharing.zipFileName(key)
    description = "Packages $filename as an exported resource"
    archiveFileName.set(filename)
  }
}
