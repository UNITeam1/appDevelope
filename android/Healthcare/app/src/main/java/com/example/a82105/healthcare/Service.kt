package com.example.a82105.healthcare

import retrofit2.Call
import retrofit2.http.GET

interface Service{
    @GET("members/1")
    fun ApiService(): Call<beans>
}