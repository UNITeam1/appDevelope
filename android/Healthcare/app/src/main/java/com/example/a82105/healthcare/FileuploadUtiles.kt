package com.example.a82105.healthcare

import android.content.ContentValues.TAG
import android.util.Log
import okhttp3.*
import org.json.JSONObject

import java.io.File
import java.io.IOException
import kotlin.math.log

class FileuploadUtiles {
    companion object{

        val client:OkHttpClient= OkHttpClient()
        fun send2server(imagefile: String?){
            Log.d("TAG1", "to request")

            val file :File = File(imagefile)
            val PNG=MediaType.parse("image/*")
            val filename = imagefile?.substring(imagefile.lastIndexOf("/")+1)


            val body = MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("uploaded_file", filename, RequestBody.create(PNG, file))
                    .build()

            val request = Request.Builder()
                    .url("http://52.71.193.244/upload.php")
                    .post(body)
                    .build()


            Log.d("TAG1", "ERROR REPORT : "+filename)
            Log.d("TAG1", "ERROR REPORT2 : "+request.body())
            val response = client.newCall(request).execute()
            Log.d("TAG1", "to request"+response.body().toString())


        }
    }
}