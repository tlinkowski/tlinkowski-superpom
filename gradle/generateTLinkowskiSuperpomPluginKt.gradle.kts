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

import java.time.LocalDate
import java.time.LocalTime

/**
 * See: https://github.com/tlinkowski/tlinkowski-superpom/issues/6
 */
tasks {
  val generateTLinkowskiSuperpomPluginKt by registering {
    val buildGradleKts = file("build.gradle.kts")
    val superpomPluginKt = file("src/main/kotlin/pl/tlinkowski/superpom/TLinkowskiSuperpomPlugin.kt")

    description = "Generates ${superpomPluginKt.name}"
    inputs.file(buildGradleKts)
    outputs.file(superpomPluginKt)

    doLast {
      val sharedBuildScript = prepareSharedBuildScript(buildGradleKts.readText())
      val superpomPluginContent = buildTLinkowskiSuperpomPluginKtContent(sharedBuildScript)
      superpomPluginKt.writeText(superpomPluginContent)
    }
  }

  "compileKotlin" {
    dependsOn(generateTLinkowskiSuperpomPluginKt)
  }
}

/**
 * Prepares the shared content to be pasted into `TLinkowskiSuperpomPlugin.kt`.
 */
fun prepareSharedBuildScript(buildGradleKtsContent: String) = buildGradleKtsContent
        .substringAfter("//region SHARED BUILD SCRIPT")
        .substringBefore("//endregion")
        .trim()
        .replace(Regex("val (\\w+): String by project")) {
          val propertyName = it.groups[1]!!.value
          val propertyValue = property(propertyName)
          "val $propertyName = \"$propertyValue\" // copied from gradle.properties"
        }

/**
 * Builds the content of `TLinkowskiSuperpomPlugin.kt`.
 */
fun buildTLinkowskiSuperpomPluginKtContent(sharedBuildScript: String) = """
// Auto-generated using `generateTLinkowskiSuperpomPluginKt.gradle.kts` on ${LocalDate.now()} at ${LocalTime.now().withNano(0)}
/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright ${LocalDate.now().year} Tomasz Linkowski.
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

import org.gradle.api.Project
import org.gradle.kotlin.dsl.*

/**
 * Applies the concept of a Gradle SuperPOM for all projects of Tomasz Linkowski.
 *
 * DO NOT EDIT THIS FILE!
 *
 * @author Tomasz Linkowski
 */
class TLinkowskiSuperpomPlugin : BaseTLinkowskiSuperpomPlugin() {

  /**
   * The contents of this method are copied from root `build.gradle.kts` (`SHARED BUILD SCRIPT` region).
   */
  override fun Project.sharedBuildScriptFromBuildGradleKts() {
    ${sharedBuildScript.prependIndent("    ").trimStart()}
  }
}
""".trimStart()