package br.com.banavita.app.core.time

import br.com.banavita.app.domain.model.PeriodoPreset
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.temporal.TemporalAdjusters

data class EpochRange(
    val startInclusive: Long,
    val endExclusive: Long,
) {
    operator fun contains(epochMillis: Long): Boolean =
        epochMillis in startInclusive until endExclusive
}

object PeriodRanges {
    fun of(
        preset: PeriodoPreset,
        nowMillis: Long = nowEpochMillis(),
        zone: ZoneId = ZoneId.systemDefault(),
        intervaloInicio: Long? = null,
        intervaloFimInclusive: Long? = null,
    ): EpochRange {
        val today = Instant.ofEpochMilli(nowMillis).atZone(zone).toLocalDate()
        return when (preset) {
            PeriodoPreset.HOJE -> day(today, zone)
            PeriodoPreset.SEMANA -> {
                val monday = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                val nextMonday = monday.plusWeeks(1)
                EpochRange(startOfDay(monday, zone), startOfDay(nextMonday, zone))
            }
            PeriodoPreset.MES -> {
                val first = today.withDayOfMonth(1)
                val next = first.plusMonths(1)
                EpochRange(startOfDay(first, zone), startOfDay(next, zone))
            }
            PeriodoPreset.INTERVALO -> {
                val start = intervaloInicio ?: startOfDay(today.withDayOfMonth(1), zone)
                val end = intervaloFimInclusive?.let { endExclusiveOfInclusiveDay(it, zone) }
                    ?: startOfDay(today.plusDays(1), zone)
                EpochRange(start, end)
            }
        }
    }

    fun day(date: LocalDate, zone: ZoneId = ZoneId.systemDefault()): EpochRange =
        EpochRange(startOfDay(date, zone), startOfDay(date.plusDays(1), zone))

    fun next7Days(nowMillis: Long = nowEpochMillis(), zone: ZoneId = ZoneId.systemDefault()): EpochRange {
        val today = Instant.ofEpochMilli(nowMillis).atZone(zone).toLocalDate()
        return EpochRange(startOfDay(today, zone), startOfDay(today.plusDays(7), zone))
    }

    fun startOfDay(date: LocalDate, zone: ZoneId = ZoneId.systemDefault()): Long =
        date.atStartOfDay(zone).toInstant().toEpochMilli()

    fun atTime(date: LocalDate, time: LocalTime, zone: ZoneId = ZoneId.systemDefault()): Long =
        LocalDateTime.of(date, time).atZone(zone).toInstant().toEpochMilli()

    private fun endExclusiveOfInclusiveDay(inclusiveMillis: Long, zone: ZoneId): Long {
        val date = Instant.ofEpochMilli(inclusiveMillis).atZone(zone).toLocalDate()
        return startOfDay(date.plusDays(1), zone)
    }
}
