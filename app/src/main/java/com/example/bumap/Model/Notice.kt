package com.example.bumap.Singleton

object Notice {

    private var artclTitle : String = "";
    private var artclDate : String = "";
    private var artclnum : String = "";
    private var arg1 : String = "";
    private var arg2 : String = "";
    private var href : String = "";

    fun getArtclTitle() = artclTitle
    fun setArtclTitle(value :String) {
        artclTitle = value
    }

    fun getArtclDate() = artclDate
    fun setArtclDate(value :String) {
        artclDate = value
    }

    fun getArtclnum() = artclnum
    fun setArtclnum(value :String) {
        artclnum = value
    }

    fun getArg1() = arg1
    fun setArg1(value :String) {
        arg1 = value
    }

    fun getArg2() = arg2
    fun setArg2(value :String) {
        arg2 = value
    }

    fun getHref() = href
    fun setHref(value :String) {
        href = value
        }


}