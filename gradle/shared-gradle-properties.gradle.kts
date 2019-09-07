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

import org.slf4j.LoggerFactory
import java.util.*

/**
 * Loads `shared-gradle.properties` as extra properties of this [Project]/[Settings].
 */
run {
  val buildscriptFile = checkNotNull(buildscript.sourceFile)
  val logger = LoggerFactory.getLogger(buildscriptFile.name)

  val properties = Properties()
  buildscriptFile.resolveSibling("shared-gradle.properties").inputStream().use { properties.load(it) }

  //region DUPLICATED IN SuperpomSharedGradlePropertyImportPlugin.kt
  properties.stringPropertyNames().forEach { name ->
    if (extra.has(name)) {
      logger.warn("Shared property {}={} ignored (property {}={} found)", name, properties[name], name, extra[name])
    } else {
      extra[name] = properties[name]
    }
  }
  //endregion
}
