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

import org.gradle.api.Project
import org.gradle.kotlin.dsl.*
import pl.tlinkowski.gradle.my.superpom.internal.plugin.SuperpomSharedFileImportPlugin
import pl.tlinkowski.gradle.my.superpom.internal.plugin.SuperpomSharedGradlePropertyImportPlugin
import pl.tlinkowski.gradle.my.superpom.shared.internal.plugin.AbstractRootPlugin
import pl.tlinkowski.gradle.my.superpom.shared.internal.plugin.MyCompleteSharedConfigPlugin

/**
 * Applies [The Gradle SuperPOM](http://andresalmiray.com/the-gradle-superpom/) concept of Andres Almiray
 * to all projects of Tomasz Linkowski.
 *
 * @author Tomasz Linkowski
 */
class MySuperpomGradlePlugin : AbstractRootPlugin() {

  override fun Project.configureRootProject() {
    apply<SuperpomSharedFileImportPlugin>()
    apply<SuperpomSharedGradlePropertyImportPlugin>()
    apply<MyCompleteSharedConfigPlugin>() // shared build script
  }
}
