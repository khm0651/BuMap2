package com.example.bumap.Model



class Building (){
    lateinit var  name : String
    lateinit var high :String
    lateinit var low : String
    lateinit var  location : Location
    var  floor : HashMap<String, Floor> = HashMap()


}