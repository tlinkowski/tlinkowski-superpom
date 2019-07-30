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

import org.gradle.api.DefaultTask
import org.gradle.api.file.ConfigurableFileTree
import org.gradle.api.file.FileTree
import org.gradle.api.tasks.*
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
open class ZipSharedFileImportTask : DefaultTask() {

  init {
    group = TaskGroupNames.FILE_SHARING
  }

  /**
   * Represents the Zip file from which we copy (like [Copy.from] but we can't use [Copy] directly).
   */
  @InputFiles
  lateinit var sourceFileTree: FileTree
    private set

  /**
   * Represents the original location of the exported files, which is now being used for importing these files
   * in the corresponding location (like [Copy.into] but we can't use [Copy] directly).
   */
  @OutputFiles
  lateinit var targetFileTree: ConfigurableFileTree
    private set

  /**
   * Copies the files from [sourceFileTree] into [targetFileTree] (representing
   * the original location of the exported files).
   */
  @TaskAction
  fun importFiles() {
    // impl note: we can't use Gradle's Copy task (see https://github.com/tlinkowski/tlinkowski-superpom/issues/35)
    project.copy {
      from(sourceFileTree)
      into(targetFileTree.dir)
    }
  }

  /**
   * Configures this task to import files from a Zip resource for given [key].
   */
  fun forKey(key: String) {
    fromExportedZipFile(key)
    intoTargetFileTree(key)
  }

  /**
   * Counterpart of `ZipSharedFileExportTask.intoExportedZipFile` from `buildSrc`.
   */
  private fun fromExportedZipFile(key: String) {
    val filename = SuperpomFileSharing.zipFileName(key)
    description = "Imports files from $filename (as exported by the SuperPOM plugin)"

    sourceFileTree = project.zipTree(prepareSharedZipTempFile(filename))
  }

  private fun intoTargetFileTree(key: String) {
    val fileTreeProvider = SuperpomFileSharing.zipFileTreeProvider(key)
    targetFileTree = fileTreeProvider(project)
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
