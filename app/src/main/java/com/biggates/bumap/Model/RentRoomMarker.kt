package com.biggates.bumap.Model

data class RentRoomMarker (
    var rentRoomName :String = "",
    var info : ArrayList<HashMap<String,String>> = arrayListOf(),
    var location : Location = Location(),
    var range : Range = Range()
)

data class Range(
    var yearPriceRange : String = "",
    var halfYearPriceRange : String = ""
)


