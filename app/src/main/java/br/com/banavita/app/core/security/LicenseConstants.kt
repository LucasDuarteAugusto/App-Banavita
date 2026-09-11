package br.com.banavita.app.core.security

object LicenseConstants {
    const val APP_SALT = "Banavita_Offline_v1_Salt_2026"

    const val REQUEST_PREFIX = "BAN1-REQ-"
    const val LICENSE_PREFIX = "BAN1-LIC-"

    const val LICENSE_VERSION = 1
    const val MONTHLY_DURATION_MILLIS = 30L * 24L * 60L * 60L * 1000L

    val PUBLIC_KEY_PEM = """
-----BEGIN PUBLIC KEY-----
MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAoU0zUirC01Y/9JPq7zzB
qsbOQTrrarwAJZOxqlqdNl0Vo6o+SDEpMRlQd/5VpSn5lTt4KQ2mlwucZeIsaG59
6Jv9Xb7n5RP7Xl1zXLy+uu1W+smBOIhPQjkOBiilOiHeZ6i48iza/opZ5r5n0W9y
uDfQ+lvpBXqYkUBLVrNFMqLWWmBKOaWVYlYWZT747+n8PlQah1dptuj+vmwB5Zxd
BlCIrmXvoInX+klvS909X+JdjMOK4+zuWl3HZY7/8iv9NanDk1cIiUUKk4vTlOll
nsxhMr6PVCbutdN9PO/16QBp9af5VIwTQ9FsFqxu8EP3peIlrR6LPpENfMoX85dU
OQIDAQAB
-----END PUBLIC KEY-----
    """.trimIndent()
}
