package com.biggates.bumap.Model

class LectureSchedule {
    private val schedule: HashMap<String, Any> = HashMap<String, Any>()

    fun getSchedule(): HashMap<String, Any>? {
        return schedule
    }

    fun setSchedule(k: String, v: Any) {
        schedule[k] = v
    }
}