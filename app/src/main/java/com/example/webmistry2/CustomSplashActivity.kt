package com.example.webmistry2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class CustomSplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_custom_splash)
        val main=object :Thread(){
            override fun run() {
                super.run()
                try {
                    sleep(1500)
                }catch (e:Exception){
                    e.printStackTrace()
                }finally {
                    startActivity(Intent(this@CustomSplashActivity,MainActivity::class.java))
                    finish()
                }
            }
        }.start()
    }
}