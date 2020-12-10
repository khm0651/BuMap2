package com.biggates.bumap.Model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class RentRoomInfo (
    var info : HashMap<String,String> = hashMapOf()
) : Parcelable

