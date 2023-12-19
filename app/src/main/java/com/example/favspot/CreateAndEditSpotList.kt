package com.example.favspot

import android.media.Image
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

const val ITEM_POSISTION_KEY = "ITEM_POSISTION"

const val POSISTION_NOT_SET = -1

class CreateAndEditSpotList : AppCompatActivity() {

    lateinit var db : FirebaseFirestore
    lateinit var auth : FirebaseAuth

    lateinit var nameEditText: EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_spot_list)

        auth = Firebase.auth
        db = Firebase.firestore

        nameEditText = findViewById(R.id.nameEditText)
        val saveButton = findViewById<ImageButton>(R.id.saveButton)
        val deleteButton = findViewById<ImageButton>(R.id.deleteImageButton)

        val itemPosistion = intent.getIntExtra(ITEM_POSISTION_KEY, POSISTION_NOT_SET)

        if (itemPosistion != POSISTION_NOT_SET) {
            displayItem(itemPosistion)

            deleteButton.setOnClickListener {
                removeItem(itemPosistion)

            }
        } else {

        saveButton.setOnClickListener {
            addItem()

        }



        }

    }
    fun displayItem(position : Int) {
        val item = DataManager.item[position]
        nameEditText.setText(item.itemName)

    }

    fun addItem() {
        val name = nameEditText.text.toString()
        val item = SpotList(name,0,"")
        val user = auth.currentUser
        if (user == null) {
            return
        }
        db.collection("users").document(user.uid)
            .collection("items").add(item).addOnSuccessListener { document ->
                val id = document.id
                item.id = id
                db.collection("users").document(user.uid).collection("items").document(id).set(item)
                DataManager.item.add(item)
                finish()
            }
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



}