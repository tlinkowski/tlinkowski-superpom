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

package pl.tlinkowski.gradle.my.superpom

import pl.tlinkowski.gradle.my.superpom.shared.internal.SuperpomFileSharing
import java.io.InputStream

/**
 * Utility class for loading the resources exported using `configureSharedFileExport.gradle.kts`.
 *
 * @author Tomasz Linkowski
 */
internal object MySuperpomSharedFileAccess {

  /**
   * Returns an [InputStream] for a resource with given [name].
   */
  fun exportedResourceAsStream(name: String): InputStream = javaClass.getResourceAsStream("exported/$name")
          ?: throw IllegalArgumentException("Resource ${SuperpomFileSharing.RESOURCE_PATH}/$name not found")

  /**
   * Returns the SuperPOM plugin version stored as a resource.
   */
  fun readPluginVersion(): String =
          exportedResourceAsStream(SuperpomFileSharing.PLUGIN_VERSION_FILENAME).reader().use { it.readText() }
}
