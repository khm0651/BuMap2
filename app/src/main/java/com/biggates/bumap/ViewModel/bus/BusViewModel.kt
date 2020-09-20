package com.biggates.bumap.ViewModel.bus

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.biggates.bumap.Retrofit.RetrofitService
import com.biggates.bumap.Model.Bus
import com.biggates.bumap.Retrofit.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object BusViewModel : ViewModel() {
    private val myAPI = RetrofitClient.getInstance()
        .create(RetrofitService::class.java)

    private val _busList = MutableLiveData<ArrayList<Bus>>().apply {
        value = arrayListOf()
    }
    val busList: LiveData<ArrayList<Bus>> =
        _busList

    private val _isViewLoading = MutableLiveData<Boolean>()
    val isViewLoading: LiveData<Boolean> =
        _isViewLoading

    private var context : Context? = null

    fun setContext(context: Context){
        BusViewModel.context = context
    }

    fun getContext(context: Context) =
        BusViewModel.context

    fun loadBus(){
        _isViewLoading.postValue(true)
        myAPI.getBus().enqueue(object  : Callback<ArrayList<Bus>> {
            override fun onFailure(call: Call<ArrayList<Bus>>, t: Throwable) {
                _isViewLoading.postValue(false)
                Toast.makeText(context,"서버에 문제가 생겼습니다." + t.message, Toast.LENGTH_LONG).show()
            }

            override fun onResponse(
                call: Call<ArrayList<Bus>>,
                response: Response<ArrayList<Bus>>
            ) {
                if(response.isSuccessful){
                    var response =response.body()

                    if(response != null) _busList.value = response
                    Toast.makeText(context,"셔틀버스 시간표 불러오기 완료", Toast.LENGTH_SHORT).show()
                    _isViewLoading.postValue(false)

                }
            }

        })
    }


}