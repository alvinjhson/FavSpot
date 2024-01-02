package com.example.favspot

import android.content.Intent
import android.media.Image
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.RatingBar
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
const val REQUEST_CODE = 0


class CreateAndEditSpotList : AppCompatActivity() {

    lateinit var db : FirebaseFirestore
    lateinit var auth : FirebaseAuth

    lateinit var ratingBar: RatingBar
    lateinit var faveSpotImageView : ImageView
    lateinit var nameEditText: EditText
    lateinit var descEditText: EditText
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
        val saveButton = findViewById<ImageButton>(R.id.saveButton)
        val deleteButton = findViewById<ImageButton>(R.id.deleteImageButton)


        //val itemPosistion = intent.getIntExtra(ITEM_POSISTION_KEY, POSISTION_NOT_SET)
        itemPosistion = intent.getIntExtra(ITEM_POSISTION_KEY, POSISTION_NOT_SET)


        if (itemPosistion != POSISTION_NOT_SET) {
            displayItem(itemPosistion)

            saveButton.setOnClickListener {

                editItem(itemPosistion)

            }
            ratingBar.setOnRatingBarChangeListener { ratingBar, rating, fromUser ->

                editRating(itemPosistion)
            }

            deleteButton.setOnClickListener {

                removeItem(itemPosistion)

            }
            faveSpotImageView.setOnClickListener {
                //changeImage(itemPosistion)
                chooseImage()

            }
        } else {
            ratingBar.setOnRatingBarChangeListener { ratingBar, rating, fromUser ->
            userRating = rating


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

        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && data != null && data.data != null) {
            val filePath = data.data
            if (filePath != null) {
                uploadImage(filePath)
            }
        }
    }
    fun addName() {
        val name = nameEditText.text.toString()
        val desc = descEditText.text.toString()

        val item = SpotList(name,"","",userRating,desc)
        val user = auth.currentUser
        Log.d("!!!","current id $currentId")
        val id = currentId

        if (user == null) {
            return
        }
        db.collection("users").document(user.uid).collection("items").document(id).update("itemName", item.itemName)
        db.collection("users").document(user.uid).collection("items").document(id).update("rating", item.rating)
        db.collection("users").document(user.uid).collection("items").document(id).update("itemDesc", item.itemDesc)

        val items = DataManager.item.find { it.id == id }
        if (items != null) {
            items.itemName = name
            items.rating = userRating
            items.itemDesc = desc
        }
        finish()
    }

    fun displayItem(position : Int) {
        val item = DataManager.item[position]
        ratingBar.setRating(item.rating)
        nameEditText.setText(item.itemName)
        descEditText.setText(item.itemDesc)
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
        startActivityForResult(intent, REQUEST_CODE)
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

    fun editRating(position: Int) {
        val user = auth.currentUser
        DataManager.item[position].rating = ratingBar.rating
        val id = DataManager.item[position].id
        if (user != null) {
            db.collection("users").document(user.uid).collection("items").document(id).set(DataManager.item[position])
        }
        //finish()
    }









    fun uploadImage(file: Uri) {
        val name = nameEditText.text.toString()
        val item = SpotList(name,"","",0.0f,"")
        val storageRef = FirebaseStorage.getInstance().reference.child("bilder")
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
}