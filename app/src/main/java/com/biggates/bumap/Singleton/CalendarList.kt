package com.biggates.bumap.Singleton

import com.biggates.bumap.Model.Calendar

object CalendarList {

    private var list : ArrayList<Calendar> = arrayListOf()

    fun getList() : ArrayList<Calendar> {
        return list
    }

    fun setList(list : ArrayList<Calendar>) {
        this.list = list
    }




}