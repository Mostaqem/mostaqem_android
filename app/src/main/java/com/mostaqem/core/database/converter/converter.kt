package com.mostaqem.core.database.converter

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mostaqem.features.reciters.data.RecitationData
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
class RecitationDataConverter {
    @TypeConverter
    fun fromRecitationData(recitationData: RecitationData?): String? {
        return Gson().toJson(recitationData)
    }

    @TypeConverter
    fun toRecitationData(recitationDataString: String?): RecitationData? {
        if (recitationDataString == null) {
            return null
        }
        val type = object : TypeToken<RecitationData>() {}.type
        return Gson().fromJson(recitationDataString, type)
    }
}