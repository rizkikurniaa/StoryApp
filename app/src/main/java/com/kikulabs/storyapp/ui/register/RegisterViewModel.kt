package com.kikulabs.storyapp.ui.register

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kikulabs.storyapp.data.response.RegisterResponse
import com.kikulabs.storyapp.data.retrofit.ApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterViewModel : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String>()
    val errorText: LiveData<String> = _error

    private val _message = MutableLiveData<String>()
    val message: LiveData<String> = _message

    fun registerUser(name: String, email: String, password: String) {
        _isLoading.value = true
        ApiConfig().getApiService().registerUser(name, email, password)
            .enqueue(object : Callback<RegisterResponse> {
                override fun onResponse(
                    call: Call<RegisterResponse>,
                    response: Response<RegisterResponse>,
                ) {
                    _isLoading.value = false

                    if (response.isSuccessful) {
                        _error.value = response.body()?.error.toString()
                        _message.value = response.body()?.message
                    }
                    if (!response.isSuccessful) {
                        _error.value = response.body()?.error.toString()
                        _message.value = response.message()
                    }
                }

                override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                    _isLoading.value = false
                    Log.d("RETROFIT_TAG", t.message.toString())
                }

            })
    }
}