/*
 * SPDX-License-Identifier: LGPL-2.1-or-later
 * SPDX-FileCopyrightText: Copyright 2021-2023 Fcitx5 for Android Contributors
 */
package com.yuyan.imemodule.prefs.behavior

import com.yuyan.imemodule.view.preference.ManagedPreference


enum class ExpandedCandidateStyle {
    Grid,
    Flexbox;

    companion object : ManagedPreference.StringLikeCodec<ExpandedCandidateStyle> {
        override fun decode(raw: String): ExpandedCandidateStyle = valueOf(raw)
    }
}