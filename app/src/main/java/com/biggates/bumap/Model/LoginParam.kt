package com.biggates.bumap.Model

class LoginParam() {
    private var id : String = ""
    private var pw : String = ""

    fun getId() = id
    fun setId(id : String) {
        this.id = id
    }
    fun getPw() = pw
    fun setPw(pw : String) {
        this.pw = pw
    }

}