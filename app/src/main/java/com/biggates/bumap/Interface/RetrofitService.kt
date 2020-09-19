package com.biggates.bumap.Interface

import com.biggates.bumap.Model.Bus
import com.biggates.bumap.Model.Calendar
import com.biggates.bumap.Model.Notice
import retrofit2.Call
import retrofit2.http.GET

interface RetrofitService {

    @GET("notice")
    fun getNotice() : Call<ArrayList<Notice>>

    @GET("calendar")
    fun getCalendar() : Call<ArrayList<Calendar>>

    @GET("bus")
    fun getBus() : Call<ArrayList<Bus>>

}