package com.pratik.agricole

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class Login_page : AppCompatActivity() {

    lateinit var edit_username_input: EditText
    private lateinit var edit_password_input: EditText

    lateinit var button_login: Button
    lateinit var tvRedirectsignup: TextView
    private lateinit var auth: FirebaseAuth

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_page)
        edit_username_input = findViewById(R.id.edit_username_input)

        edit_password_input = findViewById(R.id.edit_password_input)
        button_login = findViewById(R.id.buttonlogin2)
        tvRedirectsignup = findViewById(R.id.button_sign_up)

        auth = Firebase.auth

        button_login.setOnClickListener {

            login()

        }
        tvRedirectsignup.setOnClickListener {
            val intent = Intent(this, Signup::class.java)
            startActivity(intent)
            // using finish() to end the activity
            finish()
        }


    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

    private fun login() {
        val email = edit_username_input.text.toString().trim()+"@gmail.com"
        val pass = edit_password_input.text.toString()
        // calling signInWithEmailAndPassword(email, pass)
        // function using Firebase auth object
        // On successful response Display a Toast
        auth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(this) {
            if (it.isSuccessful) {

                Toast.makeText(this, "Successfully LoggedIn", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            } else
                Toast.makeText(this, "Log In failed ", Toast.LENGTH_SHORT).show()
        }
    }
}