package com.example.favspot

import android.widget.ImageView

class SpotList() {
    var itemName: String = ""
    var itemImage: String = ""  // Ändra detta till ett lämpligt defaultvärde
    var id: String = ""
    var rating:Float = 0.0f
    var itemDesc: String = ""
    var latitude: Double = 0.0
    var longitude: Double = 0.0

    constructor(itemName: String, itemImage: String, id: String,rating: Float,itemDesc: String, latitude: Double, longitude: Double) : this() {
        this.itemName = itemName
        this.itemImage = itemImage
        this.id = id
        this.rating = rating
        this.itemDesc = itemDesc
        this.latitude = latitude
        this.longitude = longitude
    }
}
