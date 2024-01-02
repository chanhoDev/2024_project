package com.chanho.common

import android.content.Context
import android.content.SharedPreferences

object PrefHelper {
    private const val APP_PREFERENCES = "waplat_preferences"
    lateinit var preferences: SharedPreferences

    const val URI = "content://com.nhn.waplat.provider/"
    const val AUTHORITY = "com.nhn.waplat.provider"
    const val GET_ACCESS_TOKEN_METHOD = "getAccessToken"
    const val SET_ACCESS_TOKEN_METHOD = "setAccessToken"
    const val GET_USER_INFO_METHOD = "getUserInfo"

    const val GET_SSAID_METHOD = "getSSAID"
    const val KEY_SSAID = "waplat_ssaid"

    const val KEY_ACCESS_TOKEN = "waplat_access_token"
    const val KEY_USER_INFO = "waplat_user_info"
    const val KEY_USER_INFO_FOR_FORTUNE = "waplat_user_info_for_fortune"
    const val KEY_TEXT_SIZE = "waplat_text_size"

    const val KEY_CONFIRM_WALK_THROUGH = "waplat_confirm_walk_through"

    const val KEY_CURRENT_RADIO_CHANNEL_ITEM = "current_radio_channel_item"

//    const val KEY_MEMBER_NO = "waplat_member_no"
//    const val KEY_PHONE_NUMBER = "waplat_phone_number"

    fun init(context: Context) {
        preferences = context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE)
    }

    private inline fun SharedPreferences.edit(operation: (SharedPreferences.Editor) -> Unit) {
        val editor = this.edit()
        operation(editor)
        editor.apply()
    }

    fun removeAll() {
        preferences.edit().clear().commit()
    }

    fun removeKey(itemKey: String) {
        preferences.edit().remove(itemKey).commit()
    }

    operator fun set(key: String, value: Any?) =
        when (value) {
            is String? -> preferences.edit { it.putString(key, value) }
            is Int -> preferences.edit { it.putInt(key, value) }
            is Boolean -> preferences.edit { it.putBoolean(key, value) }
            is Float -> preferences.edit { it.putFloat(key, value) }
            is Long -> preferences.edit { it.putLong(key, value) }
            is Set<*> -> preferences.edit { it.putStringSet(key, value as Set<String>?) }
            else -> throw UnsupportedOperationException("Not yet implemented")
        }

    inline operator fun <reified T : Any> get(
        key: String,
        defaultValue: T? = null
    ): T =
        when (T::class) {
            String::class -> preferences.getString(key, defaultValue as String? ?: "") as T
            Int::class -> preferences.getInt(key, defaultValue as? Int ?: -1) as T
            Boolean::class -> preferences.getBoolean(key, defaultValue as? Boolean ?: false) as T
            Float::class -> preferences.getFloat(key, defaultValue as? Float ?: -1f) as T
            Long::class -> preferences.getLong(key, defaultValue as? Long ?: -1) as T
            Set::class -> preferences.getStringSet(
                key,
                defaultValue as? Set<String> ?: emptySet()
            ) as T
            else -> throw UnsupportedOperationException("Not yet implemented")
        }
}