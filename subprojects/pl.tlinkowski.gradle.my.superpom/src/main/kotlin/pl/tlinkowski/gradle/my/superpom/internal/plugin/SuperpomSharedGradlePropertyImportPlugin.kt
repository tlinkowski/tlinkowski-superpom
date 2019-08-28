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

package pl.tlinkowski.gradle.my.superpom.internal.plugin

import org.gradle.api.Project
import org.gradle.kotlin.dsl.*
import pl.tlinkowski.gradle.my.superpom.MySuperpomSharedFileAccess
import pl.tlinkowski.gradle.my.superpom.shared.internal.SuperpomFileSharing
import pl.tlinkowski.gradle.my.superpom.shared.internal.plugin.AbstractRootPlugin
import java.util.*

/**
 * Imports all properties from `shared-gradle.properties` exported from the SuperPOM project.
 *
 * Analogue of `gradle/shared-gradle-properties.gradle.kts`
 *
 * @author Tomasz Linkowski
 */
internal class SuperpomSharedGradlePropertyImportPlugin : AbstractRootPlugin() {

  override fun Project.configureRootProject() {
    val properties = Properties()
    MySuperpomSharedFileAccess.exportedResourceAsStream(SuperpomFileSharing.SHARED_PROPERTIES_FILENAME).use {
      properties.load(it)
    }

    properties.stringPropertyNames().forEach { name ->
      if (hasProperty(name)) {
        logger.warn("Overwriting property: {}", name)
      }
      extra[name] = properties[name]
    }
  }
}
