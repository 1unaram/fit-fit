package com.fitfit.app.data.util

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class IdGenerator(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(
        "id_counter_prefs",
        Context.MODE_PRIVATE
    )

    companion object {
        private const val USER_COUNTER_KEY = "user_counter"
        private const val CLOTHES_COUNTER_KEY = "clothes_counter"
        private const val OUTFIT_COUNTER_KEY = "outfit_counter"
        private const val WEATHER_COUNTER_KEY = "weather_counter"
    }

    // 다음 User ID 생성 (u1, u2, u3...)
    fun generateNextUserId(): String {
        val currentCount = prefs.getInt(USER_COUNTER_KEY, 0)
        val nextCount = currentCount + 1
        prefs.edit { putInt(USER_COUNTER_KEY, nextCount) }
        return "u$nextCount"
    }

    // 다음 Clothes ID 생성 (c1, c2, c3...)
    fun generateNextClothesId(): String {
        val currentCount = prefs.getInt(CLOTHES_COUNTER_KEY, 0)
        val nextCount = currentCount + 1
        prefs.edit { putInt(CLOTHES_COUNTER_KEY, nextCount) }
        return "c$nextCount"
    }

    // 다음 Outfit ID 생성 (o1, o2, o3...)
    fun generateNextOutfitId(): String {
        val currentCount = prefs.getInt(OUTFIT_COUNTER_KEY, 0)
        val nextCount = currentCount + 1
        prefs.edit { putInt(OUTFIT_COUNTER_KEY, nextCount) }
        return "o$nextCount"
    }

    // 다음 Weather ID 생성 (w1, w2, w3...)
    fun generateNextWeatherId(): String {
        val currentCount = prefs.getInt(WEATHER_COUNTER_KEY, 0)
        val nextCount = currentCount + 1
        prefs.edit { putInt(WEATHER_COUNTER_KEY, nextCount) }
        return "w$nextCount"
    }
}