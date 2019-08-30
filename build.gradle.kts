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
import org.jetbrains.dokka.gradle.DokkaTask
import org.kordamp.gradle.plugin.kotlindoc.KotlindocPlugin
import pl.droidsonroids.gradle.jacoco.testkit.JaCoCoTestKitPlugin
import pl.tlinkowski.gradle.my.buildsrc.plugin.*
import pl.tlinkowski.gradle.my.superpom.shared.internal.plugin.MyCompleteSharedConfigPlugin
import java.net.URL

plugins {
  // https://aalmiray.github.io/kordamp-gradle-plugins/#_org_kordamp_gradle_base
  id("org.kordamp.gradle.base") // so that we can access `config`

  // https://docs.gradle.org/current/userguide/kotlin_dsl.html#sec:kotlin-dsl_plugin
  `kotlin-dsl` apply false

  // https://aalmiray.github.io/kordamp-gradle-plugins/#_org_kordamp_gradle_kotlindoc
  id("org.kordamp.gradle.kotlindoc") apply false

  // https://github.com/koral--/jacoco-gradle-testkit-plugin
  id("pl.droidsonroids.jacoco.testkit") version "1.0.4" apply false
}

// https://aalmiray.github.io/kordamp-gradle-plugins/#_org_kordamp_gradle_base_dsl
config {
  info {
    name = "tlinkowski-superpom"
    description = "Gradle Settings & Gradle SuperPOM plugins for all projects of Tomasz Linkowski."
    inceptionYear = "2019"
    tags = listOf("gradle", "gradle-plugin", "configuration", "sharing")

    links {
      website = "https://github.com/tlinkowski/tlinkowski-superpom"
      issueTracker = "$website/issues"
      scm = "$website.git"
    }
  }
}

subprojects {
  // workaround for: https://github.com/aalmiray/kordamp-gradle-plugins/pull/165
  apply<org.kordamp.gradle.plugin.base.BasePlugin>()

  // https://docs.gradle.org/current/userguide/kotlin_dsl.html#sec:kotlin-dsl_plugin
  // https://docs.gradle.org/current/userguide/java_gradle_plugin.html
  apply<KotlinDslPlugin>()

  // https://aalmiray.github.io/kordamp-gradle-plugins/#_org_kordamp_gradle_kotlindoc
  apply<KotlindocPlugin>()

  // https://github.com/koral--/jacoco-gradle-testkit-plugin
  apply<JaCoCoTestKitPlugin>()

  config {
    bintray.enabled = true

    kotlindoc {
      enabled = true
      replaceJavadoc = true
      jdkVersion = 8

      // workaround for: https://github.com/aalmiray/kordamp-gradle-plugins/issues/161
      outputDirectory = file("$buildDir/docs/kotlindoc")
    }

    // https://aalmiray.github.io/kordamp-gradle-plugins/#_org_kordamp_gradle_plugin
    // NOTE: simply replaces more verbose `gradlePlugin { plugins { create(project.name) { ... }}}`
    plugin {
      enabled = true // NOT published to Gradle Plugin Portal, but this causes the marker artifact to be published

      id = project.name

      val pluginImplementationClass: String by project
      implementationClass = pluginImplementationClass
    }
  }

  tasks {
    // workaround for: https://github.com/aalmiray/kordamp-gradle-plugins/issues/155
    withType(DokkaTask::class).configureEach {
      externalDocumentationLink {
        url = URL("https://docs.gradle.org/current/javadoc/")
      }
    }
  }
}

apply(from = "gradle/shared-gradle-properties.gradle.kts")
apply<MyCompleteSharedConfigPlugin>() // shared build script

//region WORKAROUNDS
apply<DokkaRuntimeConfigurationWorkaroundPlugin>()
apply<IncompletePluginMavenPublicationWorkaroundPlugin>()
apply<JacocoGradleTestkitIssueWorkaroundPlugin>()
//endregion
