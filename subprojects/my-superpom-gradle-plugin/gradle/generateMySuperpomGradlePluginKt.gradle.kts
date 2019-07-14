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

import org.gradle.kotlin.dsl.support.*
import java.time.LocalDate
import java.time.LocalTime

/*
 * See: https://github.com/tlinkowski/tlinkowski-superpom/issues/6
 */
tasks {
  // https://docs.gradle.org/current/userguide/kotlin_dsl.html#using_kotlin_delegated_properties
  val generateMySuperpomGradlePluginKt by registering {
    val buildGradleKts = rootDir.resolve("build.gradle.kts")
    val superpomPluginKt = file("src/main/kotlin/pl/tlinkowski/gradle/my/superpom/MySuperpomGradlePlugin.kt")

    group = "superpom"
    description = "Generates ${superpomPluginKt.name}"
    inputs.file(buildGradleKts)
    outputs.file(superpomPluginKt)

    doLast {
      val sharedBuildScript = prepareSharedBuildScript(buildGradleKts.readText())
      val superpomPluginContent = buildMySuperpomGradlePluginKtContent(sharedBuildScript)
      superpomPluginKt.writeText(superpomPluginContent)
    }
  }

  "compileKotlin" {
    dependsOn(generateMySuperpomGradlePluginKt)
  }

  "clean" {
    dependsOn("cleanGenerateMySuperpomGradlePluginKt")
  }
}

/**
 * Prepares the shared content to be pasted into `MySuperpomGradlePlugin.kt`.
 */
fun prepareSharedBuildScript(buildGradleKtsContent: String) = buildGradleKtsContent
        .normaliseLineSeparators()
        .substringAfter("\n//region SHARED BUILD SCRIPT\n")
        .substringBefore("\n//endregion\n")
        .replace(Regex("val (\\w+): String by project")) {
          val propertyName = checkNotNull(it.groups[1], { "Group 1 not found" }).value
          val propertyValue = property(propertyName)
          "val $propertyName = \"$propertyValue\" // copied from gradle.properties"
        }

/**
 * Builds the content of `MySuperpomGradlePlugin.kt`.
 */
fun buildMySuperpomGradlePluginKtContent(sharedBuildScript: String) = """
// Auto-generated using `generateMySuperpomGradlePluginKt.gradle.kts` on ${LocalDate.now()} at ${LocalTime.now().withNano(0)}
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
package pl.tlinkowski.gradle.my.superpom

import org.gradle.api.Project
import org.gradle.kotlin.dsl.*

/**
 * Automatically generated class that applies [The Gradle SuperPOM](http://andresalmiray.com/the-gradle-superpom/)
 * concept of Andres Almiray to all projects of Tomasz Linkowski.
 *
 * @author Tomasz Linkowski
 */
class MySuperpomGradlePlugin : BaseMySuperpomGradlePlugin() {

  override fun Project.sharedBuildScriptFromBuildGradleKts() {
    // DO NOT EDIT THIS METHOD! Its contents are copied from `build.gradle.kts` (`SHARED BUILD SCRIPT` region).
    ${sharedBuildScript.prependIndent("    ").trimStart()}
  }
}
""".trimStart()
