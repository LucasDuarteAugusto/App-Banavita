package br.com.banavita.app.ui.common

import br.com.banavita.app.core.money.Money
import java.text.NumberFormat
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

private val currencyFormat = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
private val dateFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    .withZone(ZoneId.systemDefault())
private val dateTimeFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
    .withZone(ZoneId.systemDefault())
private val timeFormat = DateTimeFormatter.ofPattern("HH:mm")
    .withZone(ZoneId.systemDefault())

fun formatCurrency(centavos: Long): String =
    (currencyFormat.clone() as NumberFormat).format(Money.fromCents(centavos))

fun formatDate(epochMillis: Long): String =
    dateFormat.format(Instant.ofEpochMilli(epochMillis))

fun formatDateTime(epochMillis: Long): String =
    dateTimeFormat.format(Instant.ofEpochMilli(epochMillis))

fun formatTime(epochMillis: Long): String =
    timeFormat.format(Instant.ofEpochMilli(epochMillis))

fun formatQuantity(value: Double, unidade: String): String {
    val quantity = if (value % 1.0 == 0.0) {
        value.toLong().toString()
    } else {
        String.format(Locale("pt", "BR"), "%.2f", value)
    }
    return "$quantity $unidade"
}
