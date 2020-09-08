package com.example.bumap.Singleton

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


object RetrofitClient {
    private var instance : Retrofit? = null
    private var gson = GsonBuilder().setLenient().create()
    var okHttpClient = OkHttpClient.Builder()
        .connectTimeout(3, TimeUnit.MINUTES)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        .build()

    fun getInstance() : Retrofit{
        if(instance == null){
            instance = Retrofit.Builder()
                .baseUrl("http://ec2-3-34-130-211.ap-northeast-2.compute.amazonaws.com:8080/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build()
        }

        return instance!!
    }
}