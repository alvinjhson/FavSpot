package com.example.favspot

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class MainActivity : AppCompatActivity() {

    lateinit var auth : FirebaseAuth
    lateinit var emailView: EditText
    lateinit var passwordView : EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = Firebase.auth
        emailView = findViewById(R.id.emailEditTextText)
        passwordView = findViewById(R.id.passwordEditTextText)

        val signInButton = findViewById<Button>(R.id.signInButton)
        val signUpButton = findViewById<Button>(R.id.signUpButton)

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
                   favSpotActivity()

                } else {
                    Log.d("!!!","not signed in: ${task.exception}")
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