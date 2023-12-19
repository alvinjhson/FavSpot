package com.example.favspot

import android.widget.ImageView

class SpotList {
    var itemName:String = ""
    var itemImage : Int
    var id : String = ""

    constructor(itemName : String,itemImage : Int,id : String) {

        this.itemName = itemName
        this.itemImage = itemImage
        this.id = id

    }
}