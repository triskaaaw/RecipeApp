package com.example.recipeapp.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.recipeapp.R

class SplashScreenActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var btnIntent : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContentView(R.layout.activity_splash_screen)

        btnIntent = findViewById(R.id.btn_start)

        btnIntent.setOnClickListener(this)

    }
    override fun onClick(v: View) {
        when(v.id){
            R.id.btn_start ->{
                val firstIntent = Intent(this@SplashScreenActivity, LoginActivity::class.java)
                startActivity(firstIntent)
            }
        }
    }
}