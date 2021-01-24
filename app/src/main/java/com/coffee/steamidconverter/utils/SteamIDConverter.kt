package com.coffee.steamidconverter.utils

import android.content.Context
import com.coffee.steamidconverter.R
import java.math.BigInteger
import java.util.regex.Pattern

class SteamIDConverter(private val context: Context) {

    fun convert(steamID: String): Map<String, String>? {
        if (isInRange(steamID)) {
            return mapOf(
                STEAM_ID to toSteamID(steamID),
                STEAM_ID_3 to toSteamID3(steamID),
                STEAM_ID_64 to toSteamID64(steamID)
            )
        }
        return null
    }

    private fun isInRange(steamID: String): Boolean {
        val id64 = toSteamID64(steamID)
        if (id64 != "Invalid SteamID") {
            return BigInteger(id64) in MIN..MAX
        }
        return false
    }

    private fun validateID(ID: String): String {

        patternMap.forEach {
            if (it.value.matcher(ID).matches()) return it.key
        }

        return INVALID_ID
    }

    /**
     * Generate a SteamID from a SteamID64 or SteamID3
     */
    private fun toSteamID(steamID: String): String {
        when (validateID(steamID)) {
            STEAM_ID -> return steamID

            STEAM_ID_3 -> return fromSteamID3toSteamID(steamID)

            STEAM_ID_64 -> {
                val v = BigInteger(BASE_NUM.toString())

                var w = BigInteger(steamID)

                val y = w.mod(BigInteger.valueOf(2))

                w = w.subtract(y).subtract(v)

                return if (w.signum() < 1) {
                    "Cannot Generate SteamID"
                } else String.format(
                    context.getString(R.string.steam_id),
                    y,
                    w.divide(BigInteger.valueOf(2))
                )
            }

            INVALID_ID -> return "Invalid SteamID"
        }
        return "An Error Occurred"
    }

    /**
     * Generate a SteamID3 from a SteamID or SteamID64
     */
    private fun toSteamID3(steamID: String): String {
        var sID = steamID

        when (validateID(sID)) {
            STEAM_ID_3 -> return steamID

            STEAM_ID_64 -> {
                sID = toSteamID(sID)
            }

            INVALID_ID -> return "Invalid SteamID"
        }

        val s = sID.split(":")

        return String.format(
            context.getString(R.string.steam_id_3),
            s[1].toInt() + s[2].toInt() * 2
        )
    }

    /**
     * Generate a SteamID64 from a SteamID or SteamID3
     */
    private fun toSteamID64(steamID: String): String {
        var sID = steamID

        when (validateID(sID)) {
            STEAM_ID_64 -> return steamID

            STEAM_ID_3 -> sID = fromSteamID3toSteamID(sID)

            INVALID_ID -> return "Invalid SteamID"
        }

        val s = sID.split(":")
        val v = BigInteger(BASE_NUM.toString())
        val z = BigInteger(s[2])
        val y = BigInteger(s[1])

        return v.add(z.multiply(BigInteger.valueOf(2))).add(y).toString()
    }

    /**
     * Generate a SteamID from a SteamID3
     */
    private fun fromSteamID3toSteamID(steamId: String): String {
        val steamID3 = steamId.split(":")
        val id = BigInteger(steamID3[2].substring(0, steamID3[2].length - 1))

        return String.format(
            context.getString(R.string.steam_id),
            id.mod(BigInteger.valueOf(2)),
            id.divide(BigInteger.valueOf(2))
        )
    }

    companion object {
        /* SteamID64 Identifier */
        const val BASE_NUM = 0x0110000100000000

        /* SteamID64 Minimum Value */
        const val ID64_MIN = 0x0110000100000001
        val MIN = BigInteger(ID64_MIN.toString())

        /* SteamID64 Maximum Value */
        const val ID64_MAX = 0x01100001FFFFFFFF
        val MAX = BigInteger(ID64_MAX.toString())

        private const val STEAM_ID = "STEAMID"
        private const val STEAM_ID_3 = "STEAMID3"
        private const val STEAM_ID_64 = "STEAMID64"
        private const val INVALID_ID = ""

        val patternMap = mapOf(
            STEAM_ID to Pattern.compile("^STEAM_[0-5]:[01]:\\d+$"),
            STEAM_ID_3 to Pattern.compile("^\\[U:1:[0-9]+]$"),
            STEAM_ID_64 to Pattern.compile("^[0-9]{17}$")
        )
    }
}