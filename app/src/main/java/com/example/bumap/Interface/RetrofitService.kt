package com.example.bumap.Interface

import com.example.bumap.Model.Calendar
import com.example.bumap.Model.Notice
import com.example.bumap.Singleton.CalendarList
import com.example.bumap.Singleton.NoticeList
import retrofit2.Call
import retrofit2.http.GET

interface RetrofitService {

    @GET("notice")
    fun getNotice() : Call<ArrayList<Notice>>

    @GET("calendar")
    fun getCalendar() : Call<ArrayList<Calendar>>

}