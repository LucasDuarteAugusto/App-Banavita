package br.com.banavita.app.ui.common

import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

object DateTimeInputParser {
    private val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
    private val zone = ZoneId.systemDefault()

    fun todayDateInput(): String = formatDate(System.currentTimeMillis())

    fun nowTimeInput(): String = formatTime(System.currentTimeMillis())

    fun formatDate(date: LocalDate): String = dateFormatter.format(date)

    fun formatDate(epochMillis: Long): String =
        dateFormatter.format(Instant.ofEpochMilli(epochMillis).atZone(zone))

    fun formatTime(epochMillis: Long): String =
        timeFormatter.format(Instant.ofEpochMilli(epochMillis).atZone(zone))

    fun parseToEpochMillis(dateInput: String, timeInput: String): Long? = runCatching {
        val date = LocalDate.parse(dateInput.trim(), dateFormatter)
        val time = LocalTime.parse(timeInput.trim(), timeFormatter)
        LocalDateTime.of(date, time).atZone(zone).toInstant().toEpochMilli()
    }.getOrNull()
}
