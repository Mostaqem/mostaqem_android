package com.mostaqem.core.database.converter

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.mostaqem.features.reciters.data.reciter.Reciter
import com.mostaqem.features.surahs.data.Surah

class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromSurah(surah: Surah?): String? {
        return surah?.let { gson.toJson(it) }
    }

    @TypeConverter
    fun toSurah(json: String?): Surah? {
        return json?.let { gson.fromJson(it, Surah::class.java) }
    }

    @TypeConverter
    fun fromReciter(reciter: Reciter): String {
        return gson.toJson(reciter)
    }

    @TypeConverter
    fun toReciter(json: String): Reciter {
        return gson.fromJson(json, Reciter::class.java)
    }
}