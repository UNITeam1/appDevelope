package com.example.a82105.healthcare

import android.app.Activity
import android.app.DownloadManager
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Window
import android.view.Window.FEATURE_NO_TITLE
import android.widget.Toast
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.introduce_popup.*
import okhttp3.*
import java.net.URL


class IntroducePopup :  AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.introduce_popup)
        Log.d("TAG1","팝업창 ")
        button2.setOnClickListener()
        {
            val intent=Intent(this,MainActivity::class.java)
            intent.putExtra("flag","true")
            setResult(RESULT_OK,intent)
            finish()
        }
        button3.setOnClickListener()
        {
            val intent=Intent(this,MainActivity::class.java)
            intent.putExtra("flag","false")
            setResult(RESULT_OK,intent)
            finish()
        }

    }
}