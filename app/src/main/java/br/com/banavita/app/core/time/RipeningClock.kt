package br.com.banavita.app.core.time

import br.com.banavita.app.domain.model.Camara
import br.com.banavita.app.domain.model.MaturacaoEstado
import br.com.banavita.app.domain.model.RipeningCurve
import br.com.banavita.app.domain.model.RipeningSnapshot
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

/**
 * Relógio de maturação: só anda na câmara de etileno.
 * Um “dia” é meia-noite a meia-noite no [ZoneId] do aparelho.
 */
object RipeningClock {
    fun snapshot(
        camara: Camara,
        estadoRegistrado: MaturacaoEstado,
        etapaInicioEpochDay: Long?,
        curve: RipeningCurve,
        todayEpochDay: Long,
    ): RipeningSnapshot {
        if (camara == Camara.FRIA || etapaInicioEpochDay == null) {
            return RipeningSnapshot(
                estado = estadoRegistrado,
                etapaInicioEpochDay = etapaInicioEpochDay ?: todayEpochDay,
                daysUntilNext = null,
            )
        }
        var estado = estadoRegistrado
        var start = etapaInicioEpochDay
        while (true) {
            val duracao = curve.daysOf(estado) ?: return RipeningSnapshot(
                estado = estado,
                etapaInicioEpochDay = start,
                daysUntilNext = null,
            )
            val elapsed = (todayEpochDay - start).coerceAtLeast(0L)
            if (elapsed < duracao) {
                return RipeningSnapshot(
                    estado = estado,
                    etapaInicioEpochDay = start,
                    daysUntilNext = (duracao - elapsed).toInt(),
                )
            }
            val proximo = estado.next() ?: return RipeningSnapshot(
                estado = estado,
                etapaInicioEpochDay = start,
                daysUntilNext = null,
            )
            start += duracao.toLong()
            estado = proximo
        }
    }

    fun estadoEm(
        camara: Camara,
        estadoRegistrado: MaturacaoEstado,
        etapaInicioEpochDay: Long?,
        curve: RipeningCurve,
        targetEpochDay: Long,
    ): MaturacaoEstado = snapshot(
        camara = camara,
        estadoRegistrado = estadoRegistrado,
        etapaInicioEpochDay = etapaInicioEpochDay,
        curve = curve,
        todayEpochDay = targetEpochDay,
    ).estado

    fun todayEpochDay(nowMillis: Long = nowEpochMillis(), zone: ZoneId = ZoneId.systemDefault()): Long =
        Instant.ofEpochMilli(nowMillis).atZone(zone).toLocalDate().toEpochDay()

    fun localDateOfEpochDay(epochDay: Long): LocalDate = LocalDate.ofEpochDay(epochDay)
}
