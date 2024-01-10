package com.example.favspot

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.animation.AnimationUtils
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch

class FavSpotactivity : AppCompatActivity() {
    lateinit var recyclerView: RecyclerView
    lateinit var db : FirebaseFirestore
    lateinit var auth : FirebaseAuth
    lateinit var favspot : TextView
    lateinit var rectangleImageView : ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fav_spot)
        db = Firebase.firestore
        auth = Firebase.auth

        rectangleImageView = findViewById(R.id.rectangleImageView)
        favspot = findViewById(R.id.favSpotTextView)
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


    override fun onBackPressed() {
        super.onBackPressed()
        val user = auth.currentUser
        if (user != null) {
            Log.d("!!!","user not null")
            db.collection("users")
                .document(user.uid)
                .collection("userInfo")
                .document(user.uid)
                .set(mapOf("rememberCheckBox" to false), SetOptions.merge())
        } else {
            Log.d("!!!","null")
        }
        DataManager.item.clear()
        auth.signOut()
        val intent = Intent(this,MainActivity::class.java)
        startActivity(intent)


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
        val taskAddButton = findViewById<FloatingActionButton>(R.id.addFloatingButton)
        recyclerView.adapter?.notifyDataSetChanged()
        val addBtAnim = AnimationUtils.loadAnimation(this,R.anim.btadd)
        val recyclerViewAnim = AnimationUtils.loadAnimation(this,R.anim.recycleview)
        recyclerView.startAnimation(recyclerViewAnim)
        taskAddButton.startAnimation(addBtAnim)
        rectangleImageView.startAnimation(addBtAnim)
        favspot.startAnimation(addBtAnim)



    }
}