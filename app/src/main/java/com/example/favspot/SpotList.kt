package com.example.favspot

import android.widget.ImageView

class SpotList() {
    var itemName: String = ""
    var itemImage: String = ""  // Ändra detta till ett lämpligt defaultvärde
    var id: String = ""

    constructor(itemName: String, itemImage: String, id: String) : this() {
        this.itemName = itemName
        this.itemImage = itemImage
        this.id = id
    }
}
