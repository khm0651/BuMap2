package com.biggates.bumap.ViewModel.loginInfo

import android.content.SharedPreferences
import androidx.lifecycle.MutableLiveData

object LoginInfo {

    val autoLogin = MutableLiveData<Boolean>().apply {
        value = false
    }

}