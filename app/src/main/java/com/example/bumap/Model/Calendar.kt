package com.example.bumap.Model

class Calendar {

    private var year : String = ""
    private var month : String = ""
    private var day : String = ""
    private var content : String = ""

    fun getYear() : String {
        return year
    }

    fun setYear(year : String) {
        this.year = year
    }

    fun getMonth() : String {
        return month
    }

    fun setMonth(month : String) {
        this.month = month
    }

    fun getDay() : String {
        return day
    }

    fun setDay(day : String) {
        this.day = day
    }

    fun getContent() : String {
        return content
    }

    fun setContent(content : String) {
        this.content = content
    }


}