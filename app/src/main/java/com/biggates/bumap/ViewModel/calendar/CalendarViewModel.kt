package com.biggates.bumap.ViewModel.calendar

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.biggates.bumap.Retrofit.RetrofitService
import com.biggates.bumap.Model.Calendar
import com.biggates.bumap.Retrofit.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object CalendarViewModel : ViewModel(){

    private val myAPI = RetrofitClient.getInstance()
        .create(RetrofitService::class.java)

    private val _calendarList = MutableLiveData<ArrayList<Calendar>>().apply {
        value = arrayListOf()
    }
    val calendarList: LiveData<ArrayList<Calendar>> =
        _calendarList

    private val _isViewLoading = MutableLiveData<Boolean>()
    val isViewLoading: LiveData<Boolean> =
        _isViewLoading

    private var context : Context? = null

    fun setContext(context: Context){
        CalendarViewModel.context = context
    }

    fun getContext(context: Context) =
        CalendarViewModel.context

    fun loadCalendar(){
        _isViewLoading.postValue(true)
        myAPI.getCalendar().enqueue(object  : Callback<ArrayList<Calendar>> {
            override fun onFailure(call: Call<ArrayList<Calendar>>, t: Throwable) {
                _isViewLoading.postValue(false)
                Toast.makeText(context,"캘린더 불러오기 실패\n서버 또는 네트워크에 문제가 생겼습니다.", Toast.LENGTH_LONG).show()
            }

            override fun onResponse(
                call: Call<ArrayList<Calendar>>,
                response: Response<ArrayList<Calendar>>
            ) {
                if(response.isSuccessful){
                    var response =response.body()

                    if(response != null) _calendarList.value = response
                    Toast.makeText(context,"캘린더 불러오기 완료", Toast.LENGTH_SHORT).show()
                    _isViewLoading.postValue(false)

                }
            }

        })
    }



}