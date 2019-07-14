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

/**
 * ATTENTION: The same plugins must be included in the `plugins` block in `build.gradle.kts`.
 */
//region SHARED PLUGINS
dependencies {
  // https://kotlinlang.org/docs/reference/using-gradle.html
  val kotlinVersion: String by project
  compile(group = "org.jetbrains.kotlin", name = "kotlin-gradle-plugin", version = kotlinVersion)

  // https://aalmiray.github.io/kordamp-gradle-plugins/#_org_kordamp_gradle_project
  val kordampVersion: String by project
  compile(group = "org.kordamp.gradle", name = "project-gradle-plugin", version = kordampVersion)

  // https://github.com/java9-modularity/gradle-modules-plugin
  val modularityVersion: String by project
  compile(group = "org.javamodularity", name = "moduleplugin", version = modularityVersion)
}

repositories {
  gradlePluginPortal() // for shared Gradle plugins
}
//endregion

apply(from = "gradle/generateMySuperpomGradlePluginKt.gradle.kts")
apply(from = "gradle/configureSharedFileExport.gradle.kts")

tasks {
  licenseMain {
    // files exported by `configureSharedFileExport.gradle.kts.
    exclude("/pl/tlinkowski/gradle/my/superpom/exported/*")
  }

  //region FOR SMOKE TEST
  // https://docs.gradle.org/current/userguide/test_kit.html#sub:test-kit-automatic-classpath-injection
  pluginUnderTestMetadata {
    // https://discuss.gradle.org/t/how-to-make-gradle-testkit-depend-on-output-jar-rather-than-just-classes/18940/2
    pluginClasspath.from(project(":my-settings-gradle-plugin").tasks.jar)
  }
  //endregion
}

//region JAVA PLATFORM MODULE SYSTEM
val moduleName by extra("pl.tlinkowski.gradle.my.superpom")

tasks {
  jar {
    inputs.property("moduleName", moduleName)

    manifest {
      // for this project, there are too many split packages to bother with a full JPMS module
      attributes["Automatic-Module-Name"] = moduleName
    }
  }
}
//endregion
