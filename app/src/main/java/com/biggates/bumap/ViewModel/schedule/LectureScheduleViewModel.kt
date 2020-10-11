package com.biggates.bumap.ViewModel.schedule

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.biggates.bumap.Model.LectureSchedule
import com.biggates.bumap.Retrofit.RetrofitClient
import com.biggates.bumap.Retrofit.RetrofitService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object LectureScheduleViewModel : ViewModel() {
    private val myAPI = RetrofitClient.getInstance().create(RetrofitService::class.java)
    private val _lectureSchedule = MutableLiveData<LectureSchedule>().apply {
        value = LectureSchedule()
    }
    val lectureSchedule: LiveData<LectureSchedule> = _lectureSchedule

    private val _isViewLoading = MutableLiveData<Boolean>()
    val isViewLoading: LiveData<Boolean> = _isViewLoading

    private var context : Context? = null

    fun setContext(context: Context){
        this.context = context
    }

    fun getContext(context: Context) = this.context

    fun loadSchedule(){
        _isViewLoading.postValue(true)
        myAPI.getSchedule().enqueue(object  : Callback<LectureSchedule> {
            override fun onFailure(call: Call<LectureSchedule>, t: Throwable) {
                _isViewLoading.postValue(false)
                Toast.makeText(context,"강좌목록 불러오기 실패\n서버 또는 네트워크에 문제가 생겼습니다.", Toast.LENGTH_LONG).show()
            }

            override fun onResponse(
                call: Call<LectureSchedule>,
                response: Response<LectureSchedule>
            ) {
                if(response.isSuccessful){
                    var response =response.body()

                    if(response != null) _lectureSchedule.value = response
                    Toast.makeText(context,"강좌목록 불러오기 완료", Toast.LENGTH_SHORT).show()
                    _isViewLoading.postValue(false)

                }
            }

        })
    }


}