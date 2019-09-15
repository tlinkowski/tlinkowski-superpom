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
package pl.tlinkowski.gradle.my.superpom.shared.internal.plugin

import com.jfrog.bintray.gradle.BintrayExtension
import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.publish.maven.tasks.PublishToMavenLocal
import org.gradle.api.tasks.TaskProvider
import org.gradle.api.tasks.bundling.Jar
import org.gradle.kotlin.dsl.*
import org.kordamp.gradle.plugin.base.ProjectConfigurationExtension
import org.kordamp.gradle.plugin.bintray.BintrayPlugin
import pl.tlinkowski.gradle.my.superpom.shared.internal.*
import pl.tlinkowski.gradle.my.superpom.shared.internal.task.InjectReleasePasswordsTask

/**
 * Configures things related to publishing the project to JCenter and Maven Central.
 *
 * @author Tomasz Linkowski
 */
internal class MyCentralPublishConfigPlugin : AbstractRootPlugin() {

  /**
   * If `false`, the release is published to JCenter and Maven Central without the possibility of reverting.
   * If `true` (for debugging), the release is only uploaded to Bintray and needs to be published manually from there.
   */
  private val uploadOnly = false

  private val Project.isFirstFinalRelease
    get() = isFinalRelease && (version == "0.1.0" || isDryRunRelease)

  override fun Project.configureRootProject() {
    // https://aalmiray.github.io/kordamp-gradle-plugins/#_org_kordamp_gradle_bintray
    apply<BintrayPlugin>()

    // https://aalmiray.github.io/kordamp-gradle-plugins/#_org_kordamp_gradle_base_dsl
    configure<ProjectConfigurationExtension> {
      configureKordamp()
    }

    val injectReleasePasswords by tasks.registering(InjectReleasePasswordsTask::class)

    subprojects {
      configure<BintrayExtension> {
        configureBintray()
      }
      tasks {
        hookUpInjectReleasePasswordsTask(injectReleasePasswords)
        if (isFinalRelease) {
          configureJarTasks()
        }
      }
      afterEvaluate {
        tryAddJavadocJarToMainPublication()
      }
    }

    if (isFirstFinalRelease) {
      configureFirstFinalReleaseMessage()
    }
  }

  private fun ProjectConfigurationExtension.configureKordamp() {
    release = project.isFinalRelease

    info {
      buildInfo {
        // for reproducible builds: https://aalmiray.github.io/kordamp-gradle-plugins/#_reproducible_builds
        // we're adding a commit ID ourselves in a separate method below
        enabled = false
      }

      credentials.sonatype {
        username = "tlinkowski"
        password = "?" // configured later but can't be empty
      }
    }

    bintray {
      userOrg = "tlinkowski"
      name = project.name
      skipMavenSync = uploadOnly || project.isFirstFinalRelease

      credentials {
        username = "tlinkowski"
        password = "?" // configured later but can't be empty
      }
    }
  }

  // TODO: file a ticket for Kordamp that will let to set all this using Kordamp's DSL
  private fun BintrayExtension.configureBintray() {
    dryRun = project.isDryRunRelease
    publish = !uploadOnly

    pkg.apply {
      version.apply {
        vcsTag = project.version.toString()
        gpg.sign = true
      }
    }
  }

  private fun TaskContainerScope.hookUpInjectReleasePasswordsTask(injectReleasePasswords: TaskProvider<*>) {
    "bintrayUpload" {
      dependsOn(injectReleasePasswords) // run just before upload
    }
    injectReleasePasswords {
      withType<PublishToMavenLocal>().forEach {
        shouldRunAfter(it) // run as late as possible
      }
    }
  }

  /**
   * Replaces a part of what [org.kordamp.gradle.plugin.jar.JarPlugin] does.
   */
  private fun TaskContainerScope.configureJarTasks() {
    withType<Jar>().configureEach {
      manifest {
        attributes(mapOf(
                "Build-Revision" to project.grgit.head().id
        ))
      }
    }
  }

  private fun Project.tryAddJavadocJarToMainPublication() {
    val effectiveConfig: ProjectConfigurationExtension by project
    if (!effectiveConfig.publishing.enabled) {
      return
    }

    configure<PublishingExtension> {
      publications {
        named<MavenPublication>("main") {
          artifact(tasks.getByName("javadocJar"))
        }
      }
    }
  }

  private fun Project.configureFirstFinalReleaseMessage() {
    tasks {
      "bintrayPublish" {
        doLast {
          logger.lifecycle("SKIPPED sync to Maven Central (the project needs to be manually included in JCenter first)")
        }
      }
    }
  }
}
