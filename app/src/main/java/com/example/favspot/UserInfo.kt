package com.example.favspot

class UserInfo() {
    var rememberCheckBox : Boolean = false
    var id: String = ""

    constructor(rememberCheckBox : Boolean, id: String) : this() {
        this.rememberCheckBox = rememberCheckBox
        this.id = id
    }


}