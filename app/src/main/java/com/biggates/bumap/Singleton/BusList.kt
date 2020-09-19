package com.biggates.bumap.Singleton

import com.biggates.bumap.Model.Bus

object BusList {
    private var busList : ArrayList<Bus> = arrayListOf()


    fun getList() = busList
    fun setList(value :ArrayList<Bus>) {
        busList = value
    }
}