package com.pratik.agricole

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton


class MainActivity : AppCompatActivity() {
    lateinit var bottomNavigationView: BottomNavigationView
    val homeFragment : Home = Home()
    val feilds : Feilds = Feilds()
    val tasks : Tasks = Tasks()
    lateinit var fab : FloatingActionButton
    val profile : Profile = Profile()
    val fragmentlist = arrayOf(homeFragment,tasks,feilds,profile)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        fab = findViewById(R.id.fab)
        fab.setOnClickListener{
            openchatgpt(it)
        }
        bottomNavigationView = findViewById(R.id.bottomNavigationView)
        bottomNavigationView.background = null
        supportFragmentManager.beginTransaction().replace(R.id.frame_layout,homeFragment).commit()
        bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.home -> replaceFragment(fragmentlist[0])
                R.id.task -> replaceFragment(fragmentlist[1])
                R.id.farm -> replaceFragment(fragmentlist[2])
                R.id.profile -> replaceFragment(fragmentlist[3])
            }
            true
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(R.id.frame_layout,fragment).commit()

    }
    fun openchatgpt(view: View) {
        startActivity(Intent(this, ChatGPT::class.java))

    }

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this, MainActivity::class.java))

    }
}