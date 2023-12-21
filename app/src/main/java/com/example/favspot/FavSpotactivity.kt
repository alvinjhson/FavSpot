package com.example.favspot

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

class FavSpotactivity : AppCompatActivity() {
    lateinit var recyclerView: RecyclerView
    lateinit var db : FirebaseFirestore
    lateinit var auth : FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fav_spot)
        db = Firebase.firestore
        auth = Firebase.auth


        recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = SpotListRecyclerAdapter(this,DataManager.item)
        val taskAddButton = findViewById<FloatingActionButton>(R.id.addFloatingButton)

        loadItems()

        taskAddButton.setOnClickListener {
            val intent = Intent(this,CreateAndEditSpotList::class.java)
            startActivity(intent)
        }

    }




    fun loadItems() {
        val user = auth.currentUser
        if (user == null) {
            return
        }
        db.collection("users").document(user.uid).collection("items").get().addOnSuccessListener { documents ->
            for (document in documents) {
                val item = document.toObject(SpotList::class.java)
                DataManager.item.add(item)
            }
            recyclerView.adapter?.notifyDataSetChanged()
        }
    }
    override fun onResume() {
        super.onResume()
        recyclerView.adapter?.notifyDataSetChanged()

    }
}