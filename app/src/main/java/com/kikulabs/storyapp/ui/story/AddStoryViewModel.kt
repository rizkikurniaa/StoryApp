package com.kikulabs.storyapp.ui.story

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.kikulabs.storyapp.data.model.UserModel
import com.kikulabs.storyapp.data.model.UserPreference
import com.kikulabs.storyapp.data.response.AddStoryResponse
import com.kikulabs.storyapp.data.retrofit.ApiConfig
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddStoryViewModel(private val pref: UserPreference) : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String>()
    val errorText: LiveData<String> = _error

    fun getUser(): LiveData<UserModel> {
        return pref.getUser().asLiveData()
    }

    fun addStory(auth: String, imageMultipart: MultipartBody.Part, description: RequestBody) {

        _isLoading.value = true
        ApiConfig().getApiService().uploadStory("Bearer $auth", imageMultipart, description)
            .enqueue(object : Callback<AddStoryResponse> {
                override fun onResponse(
                    call: Call<AddStoryResponse>,
                    response: Response<AddStoryResponse>,
                ) {
                    _isLoading.value = false
                    if (response.isSuccessful) {
                        _error.value = response.body()?.error.toString()
                        Log.d("RETROFIT_TAG", response.body()?.message.toString())

                    }
                }

                override fun onFailure(call: Call<AddStoryResponse>, t: Throwable) {
                    _isLoading.value = false
                    Log.d("RETROFIT_TAG", t.message.toString())
                }
            })
    }
}