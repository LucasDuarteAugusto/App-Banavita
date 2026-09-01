package br.com.banavita.app.ui.common

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

object DateInputParser {
    private val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    private val zone = ZoneId.systemDefault()

    fun todayInput(): String = formatForInput(System.currentTimeMillis())

    fun formatForInput(epochMillis: Long): String =
        formatter.format(Instant.ofEpochMilli(epochMillis).atZone(zone))

    fun parseToEpochMillis(input: String): Long? = runCatching {
        val date = LocalDate.parse(input.trim(), formatter)
        date.atStartOfDay(zone).toInstant().toEpochMilli()
    }.getOrNull()
}
