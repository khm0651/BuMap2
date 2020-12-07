package com.biggates.bumap.Model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class RentRoom(
    var buildingName : String = "",
    var date:String = "",
    var halfYearPrice:String = "",
    var yearPrice:String = "",
    var lat: String = "",
    var lng:String = "",
    var num:String = "",
    var id:String = "",
    var updateDate : String = ""
) : Parcelable