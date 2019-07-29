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

package pl.tlinkowski.gradle.my.superpom.internal.shared

import javax.swing.JFrame
import javax.swing.JOptionPane

/**
 * Brings up an always-on-top OK/Cancel dialog and returns `true` if the user clicked OK.
 */
internal fun answerQuestionInExplicitSwingDialog(message: Any, title: String) = JOptionPane.showConfirmDialog(
        JFrame().apply { isAlwaysOnTop = true }, // source: https://stackoverflow.com/a/15604780/2032415
        message, title, JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE
) == JOptionPane.OK_OPTION
