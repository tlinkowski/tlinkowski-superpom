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

import org.gradle.api.Project

/**
 * Specification for SuperPOM file sharing.
 *
 * Needed both during:
 * - export (`SuperpomSharedFileExportPlugin`)
 * - import (`SuperpomSharedFileImportPlugin`)
 *
 * @author Tomasz Linkowski
 */
object SuperpomFileSharing {

  /**
   * Path under which the files will be exported as a resource in the SuperPOM plugin JAR.
   */
  const val RESOURCE_PATH = "/pl/tlinkowski/gradle/my/superpom/exported"

  internal const val PLUGIN_VERSION_FILENAME = "plugin-version.txt"
  internal const val SHARED_PROPERTIES_FILENAME = "shared-gradle.properties"

  /**
   * Mappings defining Zip export/import tasks to be added (see [zipFileName]).
   */
  private val ZIP_FILE_TREE_PROVIDER_MAP = mapOf(
          "idea" to this::sharedIdeaFiles,
          "release" to this::sharedReleaseFiles,
          "ci" to this::sharedContinuousIntegrationFiles
  )

  private fun sharedIdeaFiles(project: Project) = with(project) {
    fileTree("$rootDir/.idea") {
      include("/codeStyles/", "/copyright/", "/inspectionProfiles/")
    }
  }

  /**
   * Files needed for performing a release. See:
   * - [gren](https://github.com/github-tools/github-release-notes) (a Node.js app)
   */
  private fun sharedReleaseFiles(project: Project) = with(project) {
    fileTree(rootDir) {
      include("/.grenrc.yml", "/package.json", "/release.bat")
    }
  }

  private fun sharedContinuousIntegrationFiles(project: Project) = with(project) {
    fileTree(rootDir) {
      include("/.appveyor.yml", "/.travis.yml")
    }
  }

  internal fun zipFileName(key: String) = "shared-$key-files.zip"

  /**
   * A provider of a file tree to be zipped and exported under given [key].
   */
  internal fun zipFileTreeProvider(key: String) = checkNotNull(ZIP_FILE_TREE_PROVIDER_MAP[key]) {
    "No Zip file tree provider for key '$key' found"
  }

  /**
   * Keys for exported Zip files.
   */
  internal fun zipKeys() = ZIP_FILE_TREE_PROVIDER_MAP.keys

  /**
   * Path to the resources directory with exported files (relative to project).
   */
  internal fun exportedResourceDir(project: Project) = project.file("src/main/resources/$RESOURCE_PATH")
}
