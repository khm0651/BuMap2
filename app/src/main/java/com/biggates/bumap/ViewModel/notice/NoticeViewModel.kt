package com.biggates.bumap.ViewModel.notice

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.biggates.bumap.Retrofit.RetrofitService
import com.biggates.bumap.Model.Notice
import com.biggates.bumap.Retrofit.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object NoticeViewModel : ViewModel(){

    private val myAPI = RetrofitClient.getInstance().create(RetrofitService::class.java)

    private val _noticeList = MutableLiveData<ArrayList<Notice>>().apply {
        value = arrayListOf()
    }
    val noticeList: LiveData<ArrayList<Notice>> =
        _noticeList

    private val _isViewLoading = MutableLiveData<Boolean>()
    val isViewLoading: LiveData<Boolean> =
        _isViewLoading

    private var context : Context? = null

    fun setContext(context: Context){
        this.context = context
    }

    fun getContext(context: Context) = this.context

    fun loadNoitce(){
        _isViewLoading.postValue(true)
        myAPI.getNotice().enqueue(object  : Callback<ArrayList<Notice>> {
            override fun onFailure(call: Call<ArrayList<Notice>>, t: Throwable) {
                _isViewLoading.postValue(false)
                Toast.makeText(context,"공지사항 불러오기 실패\n서버 또는 네트워크에 문제가 생겼습니다.", Toast.LENGTH_LONG).show()
            }

            override fun onResponse(
                call: Call<ArrayList<Notice>>,
                response: Response<ArrayList<Notice>>
            ) {
                if(response.isSuccessful){
                    var response =response.body()

                    if(response != null) _noticeList.value = response
                    Toast.makeText(context,"공지사항 불러오기 완료", Toast.LENGTH_SHORT).show()
                    _isViewLoading.postValue(false)

                }
            }

        })
    }

}