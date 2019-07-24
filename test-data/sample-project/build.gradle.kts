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
  id("pl.tlinkowski.gradle.my.superpom")
}

config {
  info {
    name = "sample-project"
    description = "A sample project"
    inceptionYear = "2019"

    links {
      website = "https://github.com/tlinkowski/FAKE-NAME"
      issueTracker = "https://github.com/tlinkowski/FAKE-NAME/issues"
      scm = "https://github.com/tlinkowski/FAKE-NAME.git"
    }
  }
}

subprojects {
  dependencies {
    val guavaVersion: String by project

    "compile"(group = "com.google.guava", name = "guava", version = guavaVersion)
  }

  config {
    bintray.enabled = true
  }
}
