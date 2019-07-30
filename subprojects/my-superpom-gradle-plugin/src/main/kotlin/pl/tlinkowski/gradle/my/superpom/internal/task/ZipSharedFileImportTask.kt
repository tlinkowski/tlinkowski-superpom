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

import org.gradle.api.tasks.Copy
import pl.tlinkowski.gradle.my.superpom.MySuperpomSharedFileAccess
import pl.tlinkowski.gradle.my.superpom.internal.shared.SuperpomFileSharing
import pl.tlinkowski.gradle.my.superpom.internal.shared.TaskGroupNames
import java.io.File
import java.nio.file.Files
import java.nio.file.StandardCopyOption

/**
 * A task that extracts files from `shared-$subname-files.zip` into a specified directory.
 *
 * Counterpart of `ZipSharedFileExportTask` from `buildSrc`.
 *
 * @author Tomasz Linkowski
 */
open class ZipSharedFileImportTask : Copy() {

  init {
    group = TaskGroupNames.FILE_SHARING
  }

  /**
   * Configures this task to import files from a Zip resource for given [key].
   */
  fun forKey(key: String) {
    fromExportedZipFile(key)
    intoFileTreeBaseDir(key)
  }

  /**
   * Counterpart of `ZipSharedFileExportTask.intoExportedZipFile` from `buildSrc`.
   */
  private fun fromExportedZipFile(key: String) {
    val filename = SuperpomFileSharing.zipFileName(key)
    description = "Imports files from $filename (as exported by the SuperPOM plugin)"

    val zipFileTree = project.zipTree(prepareSharedZipTempFile(filename))
    from(zipFileTree)
  }

  private fun intoFileTreeBaseDir(key: String) {
    val fileTreeProvider = SuperpomFileSharing.zipFileTreeProvider(key)
    val fileTreeBaseDir = fileTreeProvider(project).dir
    into(fileTreeBaseDir)
  }

  private fun prepareSharedZipTempFile(filename: String): File {
    val sharedZipTempDir = sharedZipTempDir()
    val sharedZipTempFile = sharedZipTempDir.resolve(filename)

    logger.info("Copying {} to {}", filename, sharedZipTempDir)

    sharedZipTempDir.mkdirs()
    MySuperpomSharedFileAccess.exportedResourceAsStream(filename).use { zipInputStream ->
      Files.copy(zipInputStream, sharedZipTempFile.toPath(), StandardCopyOption.REPLACE_EXISTING)
    }

    return sharedZipTempFile
  }

  private fun sharedZipTempDir() = File(System.getProperty("java.io.tmpdir"))
          .resolve("tlinkowski-superpom")
          .resolve(MySuperpomSharedFileAccess.readPluginVersion())
}
