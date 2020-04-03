package com.example.a82105.healthcare

import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface retrofit_interface {
    @Multipart
    @POST("upload.php/")
    fun post_request(
            @Part imageFile : MultipartBody.Part
    ): Call<String>
}