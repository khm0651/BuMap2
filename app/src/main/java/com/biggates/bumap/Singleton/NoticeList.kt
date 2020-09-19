package com.biggates.bumap.Singleton

import com.biggates.bumap.Model.Notice

object NoticeList {

    private var noticeList : ArrayList<Notice> = arrayListOf()


    fun getList() = noticeList
    fun setList(value :ArrayList<Notice>) {
        noticeList = value
    }




}