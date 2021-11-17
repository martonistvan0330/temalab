package hu.bme.aut.temalab.authserver

import net.minidev.json.JSONObject
import java.util.HashMap

class BodyHandler {

    fun parseParameters(urlParameters: String): MutableMap<String, String> {
        val values: MutableMap<String, String> = HashMap()
        val parameters = urlParameters.split("&".toRegex()).toTypedArray()
        var key: String
        var value: String
        for (parameter in parameters) {
            key = parameter.split("=".toRegex()).toTypedArray()[0]
            value = parameter.split("=".toRegex()).toTypedArray()[1]
            values[key] = value
        }
        return values
    }
}