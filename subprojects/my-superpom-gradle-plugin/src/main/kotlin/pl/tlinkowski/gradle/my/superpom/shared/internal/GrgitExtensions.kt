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

package pl.tlinkowski.gradle.my.superpom.shared.internal

import org.ajoberstar.grgit.*
import org.ajoberstar.grgit.util.JGitUtil

/**
 * @return `true` if given [branch] is pushed to `origin`
 */
internal fun Grgit.isPushedBranch(branch: Branch) =
        resolve.toObjectId(branch) == resolve.toObjectId(branch.trackingBranch)


/**
 * Like [Grgit.commit] but signing is always disabled.
 *
 * Workaround for: https://github.com/tlinkowski/tlinkowski-superpom/issues/60
 */
internal fun Grgit.commitExt(closure: Configurable<ExtCommitOp>): Commit {
  val commitOp = ExtCommitOp(repository)
  closure.configure(commitOp)
  return commitOp.call()
}

/**
 * Like [org.ajoberstar.grgit.operation.CommitOp] but has `sign` field.
 */
internal data class ExtCommitOp(private val repo: Repository) {

  lateinit var message: String
  var paths: Set<String> = setOf()
  var sign: Boolean? = null

  fun call(): Commit {
    val cmd = repo.jgit.commit()
    cmd.message = message
    paths.forEach { cmd.setOnly(it) }
    cmd.setSign(sign) // this cannot be set in CommitOp yet
    val commit = cmd.call()
    return JGitUtil.convertCommit(repo, commit)
  }
}
