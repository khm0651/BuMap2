package com.example.bumap.Singleton

import com.example.bumap.Model.Notice

object NoticeList {

    private var noticeList : ArrayList<Notice> = arrayListOf()


    fun getList() = noticeList
    fun setList(value :ArrayList<Notice>) {
        noticeList = value
    }




}