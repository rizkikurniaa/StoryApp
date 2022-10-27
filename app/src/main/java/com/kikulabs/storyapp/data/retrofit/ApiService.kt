package com.kikulabs.storyapp.data.retrofit

import com.kikulabs.storyapp.data.response.AddStoryResponse
import com.kikulabs.storyapp.data.response.LoginResponse
import com.kikulabs.storyapp.data.response.RegisterResponse
import com.kikulabs.storyapp.data.response.StoriesResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {
    @FormUrlEncoded
    @POST("register")
    fun registerUser(
        @Field ("name") name: String,
        @Field ("email") email: String,
        @Field ("password") password: String
    ) : Call<RegisterResponse>

    @FormUrlEncoded
    @POST("login")
    fun loginUser(
        @Field("email") email: String,
        @Field("password")password: String
    ): Call<LoginResponse>

    @GET("stories")
    fun getListStory(
        @Header("Authorization") auth: String
    ): Call<StoriesResponse>

    @Multipart
    @POST("stories")
    fun uploadStory(
        @Header("Authorization") auth: String,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
    ): Call<AddStoryResponse>
}