package br.com.banavita.app.core.security

object LicenseConstants {
    const val APP_SALT = "Banavita_Offline_v1_Salt_2026"

    const val REQUEST_PREFIX = "BAN1-REQ-"
    const val LICENSE_PREFIX = "BAN1-LIC-"

    const val LICENSE_VERSION = 1
    const val MONTHLY_DURATION_MILLIS = 30L * 24L * 60L * 60L * 1000L

    const val PUBLIC_KEY_PEM = """
        -----BEGIN PUBLIC KEY-----
        MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAreIq8BZRzD6nMXjmRXUm
        Ek9dQqlRSbPnwC9thTdAUSE18aUbzd+EczCfLEiR/YNgANQcAeHXE1PfeKn2AiVU
        Um6PLKd/NecugA2KmnQ0dpmh4xdb9JNMpVQ1B7QRihUsMfc5XPrU6rKHzOeSfwzx
        qWToon9CA46/VIC9R2NlKbTgJCEk6cYcXDynt2WSQhGt1PbmA3QMbWWVbqhjm2Gg
        rC9ZSOJiauz7QeLNFxxMXBwoWeSy2H4q7/aLvWAH+vsJ06MWGYqlnWuE+Q3TTTEb
        OqOYlOpBPNC9fo3v6VuSpETX6X3pThkKicBmaufWwYdGmAQIk4sUsg9hlgS+bQbS
        rQIDAQAB
        -----END PUBLIC KEY-----
    """.trimIndent()
}
