package br.com.banavita.app.core.time

import br.com.banavita.app.domain.model.Camara
import br.com.banavita.app.domain.model.MaturacaoEstado
import br.com.banavita.app.domain.model.RipeningCurve
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class RipeningClockTest {

    private val curve = RipeningCurve(
        diasEmMaturacao = 1,
        diasVerde = 3,
        diasVerdolenga = 2,
        diasAmarela = 2,
        diasPintada = 2
    )

    @Test
    fun `fria nao avanca mesmo com dias decorridos`() {
        val snap = RipeningClock.snapshot(
            camara = Camara.FRIA,
            estadoRegistrado = MaturacaoEstado.VERDE,
            etapaInicioEpochDay = 10_000,
            curve = curve,
            todayEpochDay = 10_010,
        )
        assertEquals(MaturacaoEstado.VERDE, snap.estado)
        assertNull(snap.daysUntilNext)
    }

    @Test
    fun `em maturacao um dia vira verde`() {
        val inicio = 20_000L
        val snap = RipeningClock.snapshot(
            camara = Camara.ETILENO,
            estadoRegistrado = MaturacaoEstado.EM_MATURACAO,
            etapaInicioEpochDay = inicio,
            curve = curve,
            todayEpochDay = inicio + 1,
        )
        assertEquals(MaturacaoEstado.VERDE, snap.estado)
        assertEquals(3, snap.daysUntilNext)
    }

    @Test
    fun `quatro dias na etileno com maturacao 1 e verde 3 vira verdolenga`() {
        val inicio = 20_000L
        val snap = RipeningClock.snapshot(
            camara = Camara.ETILENO,
            estadoRegistrado = MaturacaoEstado.EM_MATURACAO,
            etapaInicioEpochDay = inicio,
            curve = curve,
            todayEpochDay = inicio + 4,
        )
        assertEquals(MaturacaoEstado.VERDOLENGA, snap.estado)
        assertEquals(2, snap.daysUntilNext)
    }

    @Test
    fun `exatamente ao fim do verde vira verdolenga no mesmo dia`() {
        val inicio = 20_000L
        val snap = RipeningClock.snapshot(
            camara = Camara.ETILENO,
            estadoRegistrado = MaturacaoEstado.VERDE,
            etapaInicioEpochDay = inicio,
            curve = curve,
            todayEpochDay = inicio + 3,
        )
        assertEquals(MaturacaoEstado.VERDOLENGA, snap.estado)
    }

    @Test
    fun `curva completa chega a podre e para`() {
        val inicio = 1_000L
        val total = 3 + 2 + 2 + 2
        val snap = RipeningClock.snapshot(
            camara = Camara.ETILENO,
            estadoRegistrado = MaturacaoEstado.VERDE,
            etapaInicioEpochDay = inicio,
            curve = curve,
            todayEpochDay = inicio + total,
        )
        assertEquals(MaturacaoEstado.PODRE, snap.estado)
        assertNull(snap.daysUntilNext)
        val aindaPodre = RipeningClock.snapshot(
            camara = Camara.ETILENO,
            estadoRegistrado = MaturacaoEstado.PODRE,
            etapaInicioEpochDay = inicio + total,
            curve = curve,
            todayEpochDay = inicio + total + 10,
        )
        assertEquals(MaturacaoEstado.PODRE, aindaPodre.estado)
    }

    @Test
    fun `previsao em 7 dias ignora fria`() {
        val estado = RipeningClock.estadoEm(
            camara = Camara.FRIA,
            estadoRegistrado = MaturacaoEstado.VERDE,
            etapaInicioEpochDay = null,
            curve = curve,
            targetEpochDay = 99_999,
        )
        assertEquals(MaturacaoEstado.VERDE, estado)
    }
}
