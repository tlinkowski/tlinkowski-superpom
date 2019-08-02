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
import pl.tlinkowski.gradle.my.buildsrc.plugin.DokkaRuntimeConfigurationWorkaroundPlugin
import pl.tlinkowski.gradle.my.buildsrc.plugin.JacocoGradleTestkitWindowsIssueWorkaroundPlugin
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

apply {
  from("gradle/shared-gradle-properties.gradle.kts")
  plugin(MyCompleteSharedConfigPlugin::class) // shared build script

  plugin(DokkaRuntimeConfigurationWorkaroundPlugin::class)
  if (JacocoGradleTestkitWindowsIssueWorkaroundPlugin.isWindows()) {
    plugin(JacocoGradleTestkitWindowsIssueWorkaroundPlugin::class)
  }
}

subprojects {
  apply {
    // https://docs.gradle.org/current/userguide/kotlin_dsl.html#sec:kotlin-dsl_plugin
    // https://docs.gradle.org/current/userguide/java_gradle_plugin.html
    plugin(KotlinDslPlugin::class)

    // https://aalmiray.github.io/kordamp-gradle-plugins/#_org_kordamp_gradle_kotlindoc
    plugin(KotlindocPlugin::class)

    // https://github.com/koral--/jacoco-gradle-testkit-plugin
    plugin(JaCoCoTestKitPlugin::class)
  }

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
      enabled = false // these plugins are NOT to be published to Gradle Plugin Portal

      val pluginId: String by project
      id = pluginId

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

// https://aalmiray.github.io/kordamp-gradle-plugins/#_org_kordamp_gradle_base_dsl
config {
  info {
    name = "tlinkowski-superpom"
    description = "Gradle Settings & Gradle SuperPOM plugins for all projects of Tomasz Linkowski."
    inceptionYear = "2019"
    tags = listOf("gradle", "gradle-plugin", "configuration", "sharing")

    links {
      website = "https://github.com/tlinkowski/tlinkowski-superpom"
      issueTracker = "https://github.com/tlinkowski/tlinkowski-superpom/issues"
      scm = "https://github.com/tlinkowski/tlinkowski-superpom.git"
    }
  }
}
