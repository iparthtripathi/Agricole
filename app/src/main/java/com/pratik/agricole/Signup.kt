package com.pratik.agricole

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.*
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.core.view.View
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.pratik.agricole.databinding.ActivitySignupBinding
import com.pratik.agricole.models.FarmModel


class Signup : AppCompatActivity() {

    lateinit var edit_username_input: EditText
    private lateinit var edit_password_input: EditText
    lateinit var edit_password_input_confirm: EditText
    private lateinit var button_login: Button
    lateinit var tvRedirectLogin: TextView
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var database: FirebaseDatabase
    private lateinit var UserdatabaseRef: DatabaseReference
    private lateinit var mBinding: ActivitySignupBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        edit_username_input = findViewById(R.id.edit_username_input)
        edit_password_input_confirm = findViewById(R.id.edit_password_input_confirm)
        edit_password_input = findViewById(R.id.edit_password_input)
        button_login = findViewById(R.id.button_login)
        tvRedirectLogin = findViewById(R.id.tvRedirectLogin)
        database = Firebase.database
        auth = Firebase.auth
        button_login.setOnClickListener {
            signUpUser()

        }
        tvRedirectLogin.setOnClickListener {
            val intent = Intent(this, Login_page::class.java)
            startActivity(intent)
        }


    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

    private fun signUpUser() {
        val email = edit_username_input.text.toString().trim() + "@gmail.com"
        val pass = edit_password_input.text.toString()
        val confirmPassword = edit_password_input_confirm.text.toString()


        if (email.isBlank() || pass.isBlank() || confirmPassword.isBlank()) {
            Toast.makeText(this, "Email and Password can't be blank", Toast.LENGTH_SHORT).show()
            return
        }

        if (pass != confirmPassword) {
            Toast.makeText(this, "Password and Confirm Password do not match", Toast.LENGTH_SHORT)
                .show()
            return
        }
        auth.fetchSignInMethodsForEmail(email)
            .addOnCompleteListener{
                val newu = it.result.signInMethods!!.isEmpty()
                if (newu){
                    auth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(this) {
                        if (it.isSuccessful) {
                            adddefaultfarm()
                        } else {
                            Toast.makeText(this, "Singed Up Failed!", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                else{
                    Toast.makeText(this, " Account Already Exists!", Toast.LENGTH_SHORT).show()

                }
            }



    }

    private fun adddefaultfarm() {
        UserdatabaseRef = database.getReference("users").child(auth.uid.toString()).child("farms").child("1")
        val farmModel: FarmModel = FarmModel("Tomato Farm 1", "1.2 ha",
        "1" , "https://bit.ly/3wJmFtF")

        UserdatabaseRef.setValue(farmModel).addOnCompleteListener {
            if (it.isSuccessful){
                Toast.makeText(this, "Successfully Singed Up", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, Login_page::class.java)
                startActivity(intent)
            }
            else{
                Toast.makeText(this,"Registration Failed with uid ${auth.uid}", Toast.LENGTH_SHORT).show()

            }
        }
    }


}