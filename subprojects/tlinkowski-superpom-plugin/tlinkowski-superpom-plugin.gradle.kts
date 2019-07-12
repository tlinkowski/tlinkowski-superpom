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

plugins {
  // https://github.com/koral--/jacoco-gradle-testkit-plugin
  id("pl.droidsonroids.jacoco.testkit") version "1.0.4"
}

/**
 * ATTENTION: The same plugins must be included in the `plugins` block in `build.gradle.kts`.
 */
//region SHARED PLUGINS
dependencies {
  val kordampVersion: String by project

  compile(group = "org.kordamp.gradle", name = "project-gradle-plugin", version = kordampVersion)
}

repositories {
  gradlePluginPortal() // for shared Gradle plugins
}
//endregion

apply(from = "gradle/generateTLinkowskiSuperpomPluginKt.gradle.kts")
apply(from = "gradle/configureSharedFileExport.gradle.kts")

// WORKAROUND FOR: https://github.com/koral--/jacoco-gradle-testkit-plugin/issues/9
apply(from = "gradle/workAroundJacocoGradleTestKitIssueOnWindows.gradle.kts")

tasks {
  licenseMain {
    exclude("/pl/tlinkowski/superpom/exported/*")
  }

  //region FOR SMOKE TEST
  pluginUnderTestMetadata {
    // https://discuss.gradle.org/t/how-to-make-gradle-testkit-depend-on-output-jar-rather-than-just-classes/18940/2
    pluginClasspath.from(project(":tlinkowski-settings-plugin").tasks.jar)
  }
  compileTestGroovy {
    // https://stackoverflow.com/a/37851957/2032415
    classpath += files(compileTestKotlin.get().destinationDir)
    dependsOn(compileTestKotlin)
  }
  //endregion
}