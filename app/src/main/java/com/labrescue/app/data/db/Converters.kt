package com.labrescue.app.data.db

import androidx.room.TypeConverter
import com.labrescue.app.data.model.MistakeCategory

class Converters {

    @TypeConverter
    fun fromMistakeCategory(category: MistakeCategory): String {
        return category.name
    }

    @TypeConverter
    fun toMistakeCategory(value: String): MistakeCategory {
        return try {
            MistakeCategory.valueOf(value)
        } catch (_: IllegalArgumentException) {
            MistakeCategory.LOGIC
        }
    }
}

