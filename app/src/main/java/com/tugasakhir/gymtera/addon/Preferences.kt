package com.tugasakhir.gymtera.addon

import android.content.Context
import android.content.SharedPreferences

class Preferences(context: Context) {
    private val TAG_STATUS = "status"
    private val TAG_ROLE = "role"
    private val TAG_APP = "app"

    private val pref: SharedPreferences =
        context.getSharedPreferences(TAG_APP, Context.MODE_PRIVATE)

    var prefStatus: Boolean
        get() = pref.getBoolean(TAG_STATUS, false)
        set(value) = pref.edit().putBoolean(TAG_STATUS, value).apply()

    var prefRole: String?
        get() = pref.getString(TAG_ROLE, "")
        set(value) = pref.edit().putString(TAG_ROLE, value).apply()

    fun prefClear() {
        pref.edit().remove(TAG_STATUS).apply()
        pref.edit().remove(TAG_ROLE).apply()
    }
}