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

buildscript {
  repositories {
    gradlePluginPortal()
    mavenCentral()
  }
  dependencies {
    classpath(group = "org.kordamp.gradle", name = "base-gradle-plugin", version = "0.21.0")
  }
}

//region SHARED CONFIG (carbon copy in: TLinkowskiSuperpomPlugin.kt)
apply<org.kordamp.gradle.plugin.base.BasePlugin>()

configure<org.kordamp.gradle.plugin.base.ProjectConfigurationExtension> {
  info {
    vendor = "Tomasz Linkowski"

    people {
      person {
        id = "tlinkowski"
        name = "Tomasz Linkowski"
        url = "https://tlinkowski.pl/"
        roles = listOf("developer")
      }
    }
  }

  licensing {
    licenses {
      license {
        id = "Apache-2.0"
      }
    }
  }
}
//endregion
