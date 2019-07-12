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

package pl.tlinkowski.superpom

import java.nio.file.*

/**
 * Special class that replaces the content of a file for the duration of a test.
 *
 * @author Tomasz Linkowski
 */
class ShadedFile(private val path: Path, contentMapper: (String) -> String) : AutoCloseable {

  init {
    val modifiedContent = contentMapper(Files.readString(path))
    Files.move(path, bakPath()) // backup original file
    Files.writeString(path, modifiedContent)
  }

  override fun close() {
    Files.move(bakPath(), path, StandardCopyOption.REPLACE_EXISTING) // restore original file
  }

  private fun bakPath() = Path.of("$path.bak")
}
