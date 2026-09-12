package br.com.banavita.app.core.money

import java.math.BigDecimal
import java.math.RoundingMode

object Money {
    private const val CENT_SCALE = 2

    fun toCents(value: BigDecimal): Long =
        value.setScale(CENT_SCALE, RoundingMode.HALF_UP)
            .movePointRight(CENT_SCALE)
            .longValueExact()

    fun fromCents(cents: Long): BigDecimal =
        BigDecimal.valueOf(cents, CENT_SCALE)

    fun lineTotal(unitCents: Long?, quantity: Double): Long =
        unitCents?.let { BigDecimal.valueOf(it) }
            ?.multiply(BigDecimal.valueOf(quantity))
            ?.setScale(0, RoundingMode.HALF_UP)
            ?.longValueExact() ?: 0L

    fun sumCents(values: Iterable<Long>): Long = values.sum()
}
