package com.example.favspot

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.firestore

class MainActivity : AppCompatActivity() {

    lateinit var auth : FirebaseAuth
    lateinit var db : FirebaseFirestore
    lateinit var emailView: EditText
    lateinit var passwordView : EditText
    lateinit var rememberCheckBox : CheckBox

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        db = Firebase.firestore
        auth = Firebase.auth
        emailView = findViewById(R.id.emailEditTextText)
        passwordView = findViewById(R.id.passwordEditTextText)
        rememberCheckBox = findViewById(R.id.rememberCheckBox)
        val signInButton = findViewById<Button>(R.id.signInButton)
        val signUpButton = findViewById<Button>(R.id.signUpButton)



        getUserData()
        //rememberCheckBox()

       autoSignIn()
        rememberCheckBox.setOnClickListener {
            checkBox()
        }

        signInButton.setOnClickListener {
            signIn()


        }
        signUpButton.setOnClickListener {
            signUp()

        }


    }

    fun favSpotActivity() {
        val intent = Intent(this,FavSpotactivity::class.java)
        startActivity(intent)
    }
    fun signIn() {
        val email = emailView.text.toString()
        val password = passwordView.text.toString()

        if (email.isEmpty() || password.isEmpty()) {
            return
        }

        auth.signInWithEmailAndPassword(email,password)
            .addOnCompleteListener{
                    task ->
                if (task.isSuccessful) {
                    Log.d("!!!","sgned in")
                    checkBox()
                   favSpotActivity()

                } else {
                    Log.d("!!!","not signed in: ${task.exception}")
                }
            }
    }
    /*
    fun autoSignIn() {
        val user = auth.currentUser
        if (user != null &&   db.collection("users").document(user.uid).collection("userInfo")
                .document(id).get(userCheckBox)) {
            favSpotActivity()
        }
    }
     */
    fun autoSignIn() {
        val user = auth.currentUser
        if (user != null) {
            db.collection("users").document(user.uid).collection("userInfo")
                .get().addOnSuccessListener { documents ->
                    for (document in documents) {
                        val item = document.toObject(UserInfo::class.java)
                        if (item?.rememberCheckBox == true) {
                            favSpotActivity()
                        }
                    }
                }
        }
    }

    fun rememberCheckBox() {
       val checkBox = rememberCheckBox.isChecked
        val user = auth.currentUser
        val userCheckBox = UserInfo(checkBox,"")
        if (user != null && checkBox == true ) {
            db.collection("users").document(user.uid).collection("userInfo")
                .get().addOnSuccessListener { documents ->
                    for (document in documents) {
                        val item = document.toObject(UserInfo::class.java)
                        if (item?.rememberCheckBox == true) {
                            autoSignIn()
                        } else {
                            db.collection("users").document(user.uid)
                                .collection("userInfo").add(userCheckBox).addOnSuccessListener { document ->
                                    val id = document.id
                                    //userCheckBox.id = id
                                    db.collection("users").document(user.uid).collection("userInfo")
                                        .document(id).set(userCheckBox)
                                    DataManager.user.add(userCheckBox)
                                }
                        }
                    }
                }


        }
    }

    fun checkBox() {

        var checkBox = rememberCheckBox.isChecked
        val userCheckBox = UserInfo(checkBox,"")
        val users = auth.currentUser
        Log.d("!!!","current user $users")
        if (users != null) {
            db.collection("users")
                .document(users.uid)
                .collection("userInfo")
                .document(users.uid)
                .set(mapOf("rememberCheckBox" to userCheckBox.rememberCheckBox), SetOptions.merge())

        } else {
            Log.d("!!!","null")
        }
    }
    fun getUserData() {
        val user = auth.currentUser

        if (user == null) {
            return
        }
        db.collection("users").document(user.uid).collection("userInfo").get().addOnSuccessListener { documents ->
            for (document in documents) {
                val item = document.toObject(UserInfo::class.java)
                DataManager.user.add(item)
            }
        }
    }

    fun signUp() {
        val email = emailView.text.toString()
        val password = passwordView.text.toString()

        if (email.isEmpty() || password.isEmpty()) {
            return
        }

        auth.createUserWithEmailAndPassword(email,password)
            .addOnCompleteListener{
                    task ->
                if (task.isSuccessful) {
                    Log.d("!!!","create sucess")
                    favSpotActivity()

                } else {
                    Log.d("!!!","not created: ${task.exception}")
                }
            }

    }
}