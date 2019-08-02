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

import org.ajoberstar.grgit.Grgit
import java.io.File

/**
 * Prints the file with separators normalized to Unix format.
 */
internal fun File.toNormalizedString() = path.replace('\\', '/')

/**
 * Returns a path to this file in a form that can be supplied to [grgit]'s `add` or `commit` operations.
 */
internal fun File.toGrgitPath(grgit: Grgit) = absoluteFile.relativeTo(grgit.repository.rootDir.parentFile).toNormalizedString()
