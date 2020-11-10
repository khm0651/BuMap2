package com.biggates.bumap.Model

class Message {
    private val message = HashMap<String, String>()

    fun getMessage(k : String): String? {
        return message[k]
    }

    fun setMessage(k: String, v: String) {
        message[k] = v
    }
}