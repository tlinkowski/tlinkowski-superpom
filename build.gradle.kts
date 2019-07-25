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
import pl.droidsonroids.gradle.jacoco.testkit.JaCoCoTestKitPlugin
import pl.tlinkowski.gradle.my.buildsrc.plugin.JacocoGradleTestkitWindowsIssueWorkaroundPlugin
import pl.tlinkowski.gradle.my.superpom.internal.shared.plugin.MyCompleteSharedConfigPlugin

plugins {
  // https://docs.gradle.org/current/userguide/kotlin_dsl.html#sec:kotlin-dsl_plugin
  `kotlin-dsl` apply false

  // https://aalmiray.github.io/kordamp-gradle-plugins/#_org_kordamp_gradle_base
  id("org.kordamp.gradle.base") // so that we can access `config`

  // https://aalmiray.github.io/kordamp-gradle-plugins/#_org_kordamp_gradle_kotlindoc
  id("org.kordamp.gradle.kotlindoc")

  // https://github.com/koral--/jacoco-gradle-testkit-plugin
  id("pl.droidsonroids.jacoco.testkit") version "1.0.4" apply false
}

apply {
  plugin(MyCompleteSharedConfigPlugin::class) // shared build script

  if (JacocoGradleTestkitWindowsIssueWorkaroundPlugin.isWindows()) {
    plugin(JacocoGradleTestkitWindowsIssueWorkaroundPlugin::class)
  }
}

subprojects {
  apply {
    // https://docs.gradle.org/current/userguide/kotlin_dsl.html#sec:kotlin-dsl_plugin
    // https://docs.gradle.org/current/userguide/java_gradle_plugin.html
    plugin(KotlinDslPlugin::class)

    // https://github.com/koral--/jacoco-gradle-testkit-plugin
    plugin(JaCoCoTestKitPlugin::class)
  }

  // https://docs.gradle.org/current/userguide/java_gradle_plugin.html#sec:gradle_plugin_dev_usage
  config {
    plugin {
      val pluginId: String by project
      id = pluginId

      val pluginImplementationClass: String by project
      implementationClass = pluginImplementationClass
    }
  }
}

allprojects {
  //region WORKAROUND FOR: https://github.com/aalmiray/kordamp-gradle-plugins/issues/139
  configurations {
    create("dokkaRuntime")
  }
  repositories {
    gradlePluginPortal {
      content {
        includeGroup("org.jetbrains.dokka")
      }
    }
  }
  //endregion
}

// https://aalmiray.github.io/kordamp-gradle-plugins/#_org_kordamp_gradle_base_dsl
config {
  info {
    name = "tlinkowski-superpom"
    description = "Gradle Settings & Gradle SuperPOM plugins for all projects of Tomasz Linkowski."
    inceptionYear = "2019"

    links {
      website = "https://github.com/tlinkowski/tlinkowski-superpom"
      issueTracker = "https://github.com/tlinkowski/tlinkowski-superpom/issues"
      scm = "https://github.com/tlinkowski/tlinkowski-superpom.git"
    }
  }

  kotlindoc {
    replaceJavadoc = true
    jdkVersion = 8
  }
}
