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

import org.gradle.api.*
import org.gradle.api.plugins.ExtensionAware
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.Delete
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.kotlin.dsl.*
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.nio.file.Files
import java.nio.file.StandardCopyOption

/**
 * Hand-written base class for [MySuperpomGradlePlugin].
 *
 * @author Tomasz Linkowski
 */
abstract class BaseMySuperpomGradlePlugin : Plugin<Project> {

  /**
   * Applies the plugin (can be called on the root project only).
   */
  override fun apply(project: Project) {
    project.pluginOnlyBuildScript()
    project.sharedBuildScriptFromBuildGradleKts()
  }

  private fun Project.pluginOnlyBuildScript() {
    if (project != rootProject) {
      throw GradleException("This plugin can be applied to a root project only")
    }
    configureSharedFileImport()
    fixCompileJavaDestinationDirIfNecessary()
  }

  //region SHARED FILE IMPORT
  /*
   * Counterpart of `configureSharedFileExport.gradle.kts`.
   */
  private fun Project.configureSharedFileImport() {
    tasks {
      //region DUPLICATED IN `configureSharedFileExport.gradle.kts`
      val superpomGroup = "superpom"
      val sharedIdeaFileTree = fileTree("$rootDir/.idea") {
        include("/codeStyles/", "/copyright/", "/inspectionProfiles/")
      }
      //endregion

      // https://docs.gradle.org/current/userguide/kotlin_dsl.html#using_kotlin_delegated_properties
      val importSharedIdeaFiles by registering(Copy::class) {
        group = superpomGroup
        fromSharedFilesZip("idea")
        into(".idea")
      }
      val cleanImportSharedIdeaFiles by registering(Delete::class) {
        group = superpomGroup
        description = "Cleans shared .idea files imported from the SuperPOM plugin"
        delete(sharedIdeaFileTree)
      }

      //region MAIN TASKS (no dependency on them - should be run manually whenever needed)
      register("importSharedFiles") {
        group = superpomGroup
        description = "Imports all files shared by the SuperPOM plugin"
        dependsOn(importSharedIdeaFiles)
      }
      register("cleanImportSharedFiles") {
        group = superpomGroup
        description = "Cleans all files imported from the SuperPOM plugin"
        dependsOn(cleanImportSharedIdeaFiles)
      }
      //endregion
    }
  }

  private fun Copy.fromSharedFilesZip(subname: String) {
    val sharedZipTempDir = project.file(System.getProperty("java.io.tmpdir"))
            .resolve("tlinkowski-superpom")
            .resolve(MySuperpomGradlePluginExportedFiles.readPluginVersion())
    sharedZipTempDir.mkdirs()

    val filename = "shared-$subname-files.zip"
    val sharedZipTempFile = sharedZipTempDir.resolve(filename)

    MySuperpomGradlePluginExportedFiles.exportedResourceAsStream(filename).use { zipInputStream ->
      Files.copy(zipInputStream, sharedZipTempFile.toPath(), StandardCopyOption.REPLACE_EXISTING)
    }

    description = "Imports shared files from $filename exported by the SuperPOM plugin"
    from(project.zipTree(sharedZipTempFile))
  }
  //endregion

  /**
   * Fixes too eager modification of `compileJava.destinationDir`. This modification should be performed only
   * for Kotlin-only project (i.e. when `compileKotlin` has some sources), but - currently - it's performed always.
   *
   * This breaks in a scenario when Kotlin is used for tests only, as happens in this plugin.
   *
   * The original modification can be found in `org.javamodularity.moduleplugin` - `CompileJavaTaskMutator.java:57`.
   *
   * TODO: Remove this as soon as it's fixed in `org.javamodularity.moduleplugin`
   */
  private fun Project.fixCompileJavaDestinationDirIfNecessary() {
    subprojects {
      afterEvaluate {
        tasks {
          val mutatedJavaCompileName = "compileModuleInfoJava".takeIf { findByName(it) != null } ?: "compileJava"
          named<JavaCompile>(mutatedJavaCompileName) {
            if (fileTree("src/main/kotlin").isEmpty) {
              doFirst {
                val compileKotlin by getting(KotlinCompile::class)
                assert(destinationDir == compileKotlin.destinationDir) { "Redundant fix - remove!" }
                destinationDir = buildDir.resolve("classes/java/main")
                logger.info("Fixed {}.destinationDir = {}", name, destinationDir)
              }
            }
          }
        }
      }
    }
  }

  //region DUPLICATED IN `build.gradle.kts`
  /**
   * Executes the given configuration block against the [extension][ExtensionAware] of the specified type (if found).
   *
   * @see [ExtensionAware.configure]
   */
  inline fun <reified T : Any> ExtensionAware.configureIfPresent(noinline configuration: T.() -> Unit) {
    extensions.findByType(typeOf<T>())?.apply(configuration)
  }
  //endregion

  /**
   * The contents of this method are copied from root `build.gradle.kts` (`SHARED BUILD SCRIPT` region).
   */
  protected abstract fun Project.sharedBuildScriptFromBuildGradleKts()
}
