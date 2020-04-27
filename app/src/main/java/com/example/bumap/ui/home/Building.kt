package com.example.bumap.ui.home

class Building (){
    lateinit var  name : String
    lateinit var high :String
    lateinit var low : String
    lateinit var  location : Location
    var  floor : HashMap<String,Floor> = HashMap()


}

class Location(){
    lateinit var lat : String
    lateinit var lng : String
}

class Floor(){
    var room : HashMap<String,Room> = HashMap()
}

class Room(){
    lateinit var location : Location
    lateinit var name : String
}