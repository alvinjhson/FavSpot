package com.example.favspot

import android.content.Intent
import android.media.Image
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputFilter
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.FirebaseStorage

const val ITEM_POSISTION_KEY = "ITEM_POSISTION"

const val POSISTION_NOT_SET = -1
const val REQUEST_CODE_CHOOSE_IMAGE = 0
const val REQUEST_CODE_CHOOSE_LOCATION = 1


class CreateAndEditSpotList : AppCompatActivity() {

    lateinit var db : FirebaseFirestore
    lateinit var auth : FirebaseAuth

    lateinit var ratingBar: RatingBar
    lateinit var faveSpotImageView : ImageView
    lateinit var nameEditText: EditText
    lateinit var descEditText: EditText
    lateinit var latitudeTextView : TextView
    lateinit var longitudeTextView : TextView
    var currentId : String = ""
    var userRating = 0f
    private var itemPosistion = POSISTION_NOT_SET

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_spot_list)

        auth = Firebase.auth
        db = Firebase.firestore
        faveSpotImageView = findViewById(R.id.favSpotImageView)
        nameEditText = findViewById(R.id.nameEditText)
        descEditText = findViewById(R.id.descEditText)
        ratingBar = findViewById(R.id.addRatingBar)
        latitudeTextView = findViewById(R.id.latitudeTextView)
        longitudeTextView = findViewById(R.id.longitudeTextView)
        val saveButton = findViewById<ImageButton>(R.id.saveButton)
        val deleteButton = findViewById<ImageButton>(R.id.deleteImageButton)
        val googleMapButton = findViewById<ImageButton>(R.id.googleMapImageButton)

        val backButton = findViewById<ImageButton>(R.id.backImageButton)
        val maxLength = 8
        setCharacterLimit(latitudeTextView,maxLength)
        setCharacterLimit(longitudeTextView,maxLength)

        //val itemPosistion = intent.getIntExtra(ITEM_POSISTION_KEY, POSISTION_NOT_SET)
        itemPosistion = intent.getIntExtra(ITEM_POSISTION_KEY, POSISTION_NOT_SET)

        backButton.setOnClickListener {
            finish()
        }


        if (itemPosistion != POSISTION_NOT_SET) {
            displayItem(itemPosistion)

            saveButton.setOnClickListener {

                editItem(itemPosistion)
                editDesc(itemPosistion)

            }
            ratingBar.setOnRatingBarChangeListener { ratingBar, rating, fromUser ->

                editRating(itemPosistion)
            }

            deleteButton.setOnClickListener {

                removeItem(itemPosistion)

            }
            //publish.setOnClickListener {
            //    publish(itemPosistion)
            //}
            faveSpotImageView.setOnClickListener {
                //changeImage(itemPosistion)
                chooseImage()

            }
            googleMapButton.setOnClickListener {
                addLocation()
            }
        } else {
            ratingBar.setOnRatingBarChangeListener { ratingBar, rating, fromUser ->
            userRating = rating


            }
            googleMapButton.setOnClickListener{
                addLocation()

            }

        faveSpotImageView.setOnClickListener {
           chooseImage()
        }
            saveButton.setOnClickListener {
                addName()
            }
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_CHOOSE_IMAGE && resultCode == RESULT_OK && data != null && data.data != null) {
            val filePath = data.data
            if (filePath != null) {
                uploadImage(filePath)
            }
        }

        if (requestCode == REQUEST_CODE_CHOOSE_LOCATION && resultCode == RESULT_OK && data != null ) {
            val latitude = data?.getDoubleExtra("latitude", 0.0)
            val longitude = data?.getDoubleExtra("longitude", 0.0)

            if (latitude != null && longitude != null) {
                    uploadLocation(latitude,longitude)
            }
        }


    }
    fun setCharacterLimit(textView: TextView, maxLength: Int) {
        val inputFilters = arrayOf<InputFilter>(InputFilter.LengthFilter(maxLength))

        textView.filters = inputFilters
    }
    fun addName() {
        val name = nameEditText.text.toString()
        val desc = descEditText.text.toString()
        val latitude = latitudeTextView.text.toString()
        val longitude = longitudeTextView.text.toString()


        val item = SpotList(name,"","",userRating,desc,latitude.toDouble(),longitude.toDouble())
        val user = auth.currentUser
        Log.d("!!!","current id $currentId")
        val id = currentId

        if (user == null) {
            return
        }
        db.collection("users").document(user.uid).collection("items").document(id).update("itemName", item.itemName)
        db.collection("users").document(user.uid).collection("items").document(id).update("rating", item.rating)
        db.collection("users").document(user.uid).collection("items").document(id).update("itemDesc", item.itemDesc)
        db.collection("users").document(user.uid).collection("items").document(id).update("latitude", item.latitude)
        db.collection("users").document(user.uid).collection("items").document(id).update("longitude", item.longitude)
        val items = DataManager.item.find { it.id == id }
        if (items != null) {
            items.itemName = name
            items.rating = userRating
            items.itemDesc = desc
            items.latitude = latitude.toDouble()
            items.longitude = longitude.toDouble()
        }
        finish()
    }

    fun displayItem(position : Int) {
        val item = DataManager.item[position]
        ratingBar.setRating(item.rating)
        nameEditText.setText(item.itemName)
        descEditText.setText(item.itemDesc)
        longitudeTextView.setText(item.longitude.toString())
        latitudeTextView.setText(item.latitude.toString())


        Glide.with(this )
            .load(item.itemImage)
            .into(faveSpotImageView)
    }



    fun removeItem(position: Int) {
        val itemId = DataManager.item[position].id
        val user = auth.currentUser
        if (user == null) {
            return
        }
        db.collection("users").document(user.uid).collection("items").document(itemId).delete()
        removeItemFromFirestore(itemId)
        DataManager.item.removeAt(position)
        finish()
    }
    fun removeItemFromFirestore(itemId: String) {
        db.collection("items").document(itemId).delete()
    }
    fun chooseImage() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_CODE_CHOOSE_IMAGE)
    }

    fun editItem(position: Int) {
        val user = auth.currentUser
        DataManager.item[position].itemName = nameEditText.text.toString()
        val id = DataManager.item[position].id
        if (user != null) {
            db.collection("users").document(user.uid).collection("items").document(id).set(DataManager.item[position])
        }
        finish()
    }
    fun editDesc(position: Int) {
        val user = auth.currentUser
        DataManager.item[position].itemDesc = descEditText.text.toString()
        val id = DataManager.item[position].id
        if (user != null) {
            db.collection("users").document(user.uid).collection("items").document(id).set(DataManager.item[position])
        }
        finish()
    }

    fun editRating(position: Int) {
        val user = auth.currentUser
        DataManager.item[position].rating = ratingBar.rating
        val id = DataManager.item[position].id
        if (user != null) {
            db.collection("users").document(user.uid).collection("items").document(id).set(DataManager.item[position])
        }
        //finish()
    }
    /*
    fun publish(position: Int) {
        val user = auth.currentUser
        DataManager.item[position].isPublic = !DataManager.item[position].isPublic
        val id = DataManager.item[position].id
        if (user != null) {
            db.collection("users").document(user.uid).collection("items").document(id)
                .update("isPublic", DataManager.item[itemPosistion].isPublic)
        }
    }

     */









    fun uploadImage(file: Uri) {
        val name = nameEditText.text.toString()
        val item = SpotList(name,"","",0.0f,"",0.0,0.0)
        val uniqueID = System.currentTimeMillis().toString()
       val storageRef = FirebaseStorage.getInstance().reference.child("bilder/$uniqueID")
        storageRef.putFile(file)
            .addOnSuccessListener {
                storageRef.downloadUrl.addOnSuccessListener { uri ->
                    // nu har du URL:en till bilden i 'uri'-variabeln
                    // här kan du sedan ladda bilden i din ImageView
                    Glide.with(this /* context */)
                        .load(uri)
                        .into(faveSpotImageView)

                    // Nu sparar vi URL:en till bilden i Firestore


                    if (itemPosistion == POSISTION_NOT_SET) {
                        val user = auth.currentUser
                        if (user != null) {
                            item.itemImage = uri.toString() // Sätter URL:en till bilden
                            db.collection("users").document(user.uid)
                                .collection("items").add(item).addOnSuccessListener { document ->
                                    val id = document.id
                                    currentId = id
                                    Log.d("!!!", "current id $currentId")
                                    item.id = id
                                    db.collection("users").document(user.uid).collection("items")
                                        .document(id).set(item)
                                    DataManager.item.add(item)
                                }
                        }
                    }else {

                            val user = auth.currentUser
                              DataManager.item[itemPosistion].itemImage = uri.toString()
                            val id = DataManager.item[itemPosistion].id
                            Log.d("!!!", "current id $id")
                            if (user != null) {
                                db.collection("users").document(user.uid).collection("items").document(id)
                                    .update("itemImage", DataManager.item[itemPosistion].itemImage)
                            }

                    }
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(applicationContext,"Image Failed",
                    Toast.LENGTH_SHORT).show()
            }
    }

    fun addLocation() {
        val intent = Intent(this,MapsActivity::class.java)

        if (itemPosistion != POSISTION_NOT_SET) {
            var lat = latitudeTextView.text.toString().toDouble()
            var long = longitudeTextView.text.toString().toDouble()
            var name = nameEditText.text.toString()
            Log.d("!!!", "Latr: $lat, Longr: $long , name $name")
            intent.putExtra("lat", lat)
            intent.putExtra("long", long)
            intent.putExtra("name", name)


        }
        startActivityForResult(intent, REQUEST_CODE_CHOOSE_LOCATION)

    }
    fun uploadLocation(latitude : Double,longitude : Double) {
        if (itemPosistion == POSISTION_NOT_SET) {
            latitudeTextView.text = latitude.toString()
            longitudeTextView.text = longitude.toString()
            val item = SpotList("", "", "", 0.0f, "", latitude, longitude)

            val user = auth.currentUser
            Log.d("!!!", "current id $currentId")
            val id = currentId

            if (user == null) {
                return
            }
            Log.d("!!!", "It Works")
        } else {

            val user = auth.currentUser
            DataManager.item[itemPosistion].longitude = longitude  //longitudeTextView.text.toString().toDouble()
            DataManager.item[itemPosistion].latitude = latitude //latitudeTextView.text.toString().toDouble()
            val id = DataManager.item[itemPosistion].id
            Log.d("!!!", "current id $id")
            if (user != null) {
                db.collection("users").document(user.uid).collection("items").document(id)
                    .update("latitude", DataManager.item[itemPosistion].latitude)
                db.collection("users").document(user.uid).collection("items").document(id)
                    .update("longitude", DataManager.item[itemPosistion].longitude)
            }
            latitudeTextView.text = latitude.toString()
            longitudeTextView.text = longitude.toString()





        }
       // db.collection("users").document(user.uid).collection("items").document(id).update("latitude", item.latitude)
       // db.collection("users").document(user.uid).collection("items").document(id).update("longitude", item.longitude)
       // Log.d("!!!", "Latitude är: $latitude och Longitude är: $longitude")
       // val items = DataManager.item.find { it.id == id }
       // if (items != null) {
        //    items.latitude = latitude
        //    items.longitude = longitude

       // }




    }



}