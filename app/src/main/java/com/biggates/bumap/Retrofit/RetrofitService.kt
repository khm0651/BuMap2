package com.biggates.bumap.Retrofit

import com.biggates.bumap.Model.*
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface RetrofitService {

    @GET("notice")
    fun getNotice() : Call<ArrayList<Notice>>

    @GET("calendar")
    fun getCalendar() : Call<ArrayList<Calendar>>

    @GET("bus")
    fun getBus() : Call<ArrayList<Bus>>

    @POST("schedule")
    fun getSchedule(@Body params : LoginParam) : Call<LectureSchedule>

    @POST("buLogin")
    fun buLogin(@Body params : LoginParam) : Call<Message>

}