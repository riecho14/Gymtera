package com.tugasakhir.gymtera.addon

import android.content.Context

class IntroPref(context: Context) {
    private val prefName = "IntroPref"
    private val isFirstTimeKey = "isFirstTime"
    private val sharedPreferences = context.getSharedPreferences(prefName, Context.MODE_PRIVATE)

    var isFirstTime: Boolean
        get() = sharedPreferences.getBoolean(isFirstTimeKey, true)
        set(value) = sharedPreferences.edit().putBoolean(isFirstTimeKey, value).apply()
}