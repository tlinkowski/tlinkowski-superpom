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

package pl.tlinkowski.gradle.my.superpom.shared.internal.configurator

import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.TaskContainer
import org.gradle.kotlin.dsl.*
import pl.tlinkowski.gradle.my.superpom.shared.internal.TaskGroupNames
import pl.tlinkowski.gradle.my.superpom.shared.internal.isDryRunRelease
import pl.tlinkowski.gradle.my.superpom.shared.internal.task.ReleaseReadyValidationTask
import pl.tlinkowski.gradle.my.superpom.shared.internal.task.ResetScopeInGradlePropertiesTask
import pl.tlinkowski.gradle.my.superpom.shared.internal.task.generic.*

/**
 * @author Tomasz Linkowski
 * @see pl.tlinkowski.gradle.my.superpom.shared.internal.plugin.MyComprehensiveReleaseConfigPlugin
 */
internal class MyComprehensiveReleaseConfigurator(tasks: TaskContainer) {

  private val Task.versionString
    get() = if (project.isDryRunRelease) "TEST-${project.version}" else "${project.version}"

  private val validateReleasePossible by tasks.registering(ReleaseReadyValidationTask::class)
  private val confirmReleaseProcessLaunch by tasks.registering(ConsoleYesNoQuestionTask::class)

  private val addTemporaryVersionTag by tasks.registering(AddTagAndPushTask::class)
  private val generateChangelog by tasks.registering(NpmRunTask::class)
  private val removeTemporaryVersionTag by tasks.registering(RemoveTagAndPushTask::class)

  private val confirmChangelogPush by tasks.registering(ConsoleYesNoQuestionTask::class)
  private val pushUpdatedChangelog by tasks.registering(CommitFileAndPushTask::class)
  private val addFinalVersionTag by tasks.registering(AddTagAndPushTask::class)

  private val confirmFinalPublication by tasks.registering(ConsoleYesNoQuestionTask::class)
  private val releaseToGitHub by tasks.registering(NpmRunTask::class)
  private val releaseToCentralRepos by tasks.registering
  private val release by tasks.registering

  private val resetScopeInGradleProperties by tasks.registering(ResetScopeInGradlePropertiesTask::class)
  private val pushUpdatedGradleProperties by tasks.registering(CommitFileAndPushTask::class)

  fun configureTasks() {
    configureValidationTasks()
    configureChangelogGenerationTasks()
    configureFinalGitRepoStateTasks()
    configureFinalPublicationTasks()
    configurePostReleaseTasks()
  }

  fun configureTaskDependenciesFor(project: Project) {
    project.tasks {
      confirmReleaseProcessLaunch {
        dependsOn(named("build")) // make 100% sure the build is OK before starting
      }
      "bintrayUpload" {
        dependsOn(confirmFinalPublication)
      }
    }
  }

  private fun configureValidationTasks() {
    confirmReleaseProcessLaunch {
      prompt = "Do you want to begin the release process for version $versionString of '${project.rootProject.name}'?"
      dependsOn(validateReleasePossible)
    }
  }

  private fun configureChangelogGenerationTasks() {
    addTemporaryVersionTag {
      tagName = versionString
      dependsOn(confirmReleaseProcessLaunch)
    }

    generateChangelog {
      scriptName = "gren-changelog"
      dependsOn(addTemporaryVersionTag) // Git tag is needed for gren to be able to generate the changelog
      finalizedBy(removeTemporaryVersionTag) // we remove the Git tag immediately after gren runs
      onlyIfNotDryRun()
    }

    removeTemporaryVersionTag {
      tagName = versionString
    }
  }

  private fun configureFinalGitRepoStateTasks() {
    confirmChangelogPush {
      prompt = "Do you want to push the updated CHANGELOG.md and continue with the release process?"
      dependsOn(generateChangelog)
    }

    pushUpdatedChangelog {
      filepath = "CHANGELOG.md"
      commitMessage = "Release $versionString"
      dependsOn(confirmChangelogPush)
      onlyIfNotDryRun()
    }

    addFinalVersionTag {
      tagName = versionString
      dependsOn(pushUpdatedChangelog)
      onlyIfNotDryRun()
    }
  }

  private fun configureFinalPublicationTasks() {
    confirmFinalPublication {
      prompt = "Are you SURE you want to publish the code at $versionString tag to GitHub, JCenter & MavenCentral?"
      dependsOn(addFinalVersionTag)
    }

    releaseToGitHub {
      scriptName = "gren-release"
      dependsOn(confirmFinalPublication)
      onlyIfNotDryRun()
    }

    releaseToCentralRepos {
      group = TaskGroupNames.INTERNAL
      dependsOn(confirmFinalPublication, "bintrayUpload", "bintrayPublish")
      shouldRunAfter(releaseToGitHub)
    }

    release {
      group = TaskGroupNames.RELEASING
      description = "Performs the release to GitHub, JCenter & Maven Central"
      dependsOn(releaseToGitHub, releaseToCentralRepos)
      finalizedBy(pushUpdatedGradleProperties)
    }
  }

  private fun configurePostReleaseTasks() {
    resetScopeInGradleProperties {
      onlyIf { canResetScope() }
    }

    pushUpdatedGradleProperties {
      filepath = "gradle.properties"
      commitMessage = "Init next release"
      dependsOn(resetScopeInGradleProperties)
      onlyIf { resetScopeInGradleProperties.get().didWork }
    }
  }

  private fun Task.onlyIfNotDryRun() = onlyIf { !project.isDryRunRelease }
}
