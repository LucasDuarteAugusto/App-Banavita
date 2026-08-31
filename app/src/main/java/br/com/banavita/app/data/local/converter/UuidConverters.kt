package br.com.banavita.app.data.local.converter

import androidx.room.TypeConverter
import java.util.UUID

class UuidConverters {
    @TypeConverter
    fun fromUuid(value: UUID?): String? = value?.toString()

    @TypeConverter
    fun toUuid(value: String?): UUID? = value?.let(UUID::fromString)
}
