package com.pratik.agricole

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.google.firebase.auth.FirebaseAuth


class Splash_Screen : AppCompatActivity() {
    var h: Handler? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        h = Handler()
        h!!.postDelayed({
            val auth = FirebaseAuth.getInstance()
            if (auth.currentUser == null) startActivity(Intent(this@Splash_Screen, Signup::class.java))//login pe redirect
            else {
                startActivity(Intent(this@Splash_Screen, MainActivity::class.java))
            }
        }, 2000)
    }
}