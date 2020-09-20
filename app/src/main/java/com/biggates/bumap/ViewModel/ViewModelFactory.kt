package com.biggates.bumap.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.biggates.bumap.ViewModel.bus.BusViewModel
import com.biggates.bumap.ViewModel.notice.NoticeViewModel
import com.biggates.bumap.ViewModel.calendar.CalendarViewModel

class ViewModelFactory (private var viewModelName : String) : ViewModelProvider.Factory{
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        when(viewModelName){
            "notice" -> return NoticeViewModel as T

            "bus" -> return BusViewModel as T

            "calendar" -> return CalendarViewModel as T
        }

        return null as T
    }


}