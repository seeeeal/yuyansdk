/*
 * SPDX-License-Identifier: LGPL-2.1-or-later
 * SPDX-FileCopyrightText: Copyright 2021-2023 Fcitx5 for Android Contributors
 */
package com.yuyan.imemodule.view.preference

import android.content.Context
import androidx.appcompat.app.AlertDialog
import androidx.preference.PreferenceViewHolder
import androidx.preference.SwitchPreference
import com.yuyan.imemodule.R
import com.yuyan.imemodule.prefs.restore

class CsSwitchPreference(context: Context) : SwitchPreference(context) {
    override fun onBindViewHolder(holder: PreferenceViewHolder) {
        super.onBindViewHolder(holder)
        holder.itemView.setOnLongClickListener {
            AlertDialog.Builder(context)
                .setTitle(title ?: "Preference")
                .setMessage(R.string.whether_reset_switch_preference)
                .setNegativeButton(android.R.string.cancel) { _, _ -> }
                .setPositiveButton(R.string.reset) { _, _ -> restore() }
                .show()
            true
        }
    }
}