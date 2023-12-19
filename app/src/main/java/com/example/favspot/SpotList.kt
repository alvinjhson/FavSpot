package com.example.favspot

import android.widget.ImageView

class SpotList() {
    var itemName: String = ""
    var itemImage: Int = 0 // Ändra detta till ett lämpligt defaultvärde
    var id: String = ""

    constructor(itemName: String, itemImage: Int, id: String) : this() {
        this.itemName = itemName
        this.itemImage = itemImage
        this.id = id
    }
}
