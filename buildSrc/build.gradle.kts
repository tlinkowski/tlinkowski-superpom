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
  `java-library`
  `kotlin-dsl`
  idea
}

apply(from = "../gradle/shared-gradle-properties.gradle.kts")
apply(from = "../gradle/shared-buildscript-dependencies.gradle.kts")

tasks {
  val syncSharedKotlinSources by registering(Sync::class) {
    group = "superpom"
    description = "Synchronizes 'shared' package from 'pl.tlinkowski.gradle.my.superpom' plugin into 'buildSrc'"

    val sharedSourceDir = "src/main/kotlin/pl/tlinkowski/gradle/my/superpom/shared"
    from("../subprojects/pl.tlinkowski.gradle.my.superpom/$sharedSourceDir")
    into(sharedSourceDir)
  }

  compileKotlin {
    dependsOn(syncSharedKotlinSources)
  }
}
