

package com.yuyan.imemodule.ui.utils

import android.content.res.Configuration

fun Configuration.isDarkMode(): Boolean =
    uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
