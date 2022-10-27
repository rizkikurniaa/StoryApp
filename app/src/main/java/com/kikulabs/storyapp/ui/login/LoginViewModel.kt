package com.kikulabs.storyapp.ui.login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kikulabs.storyapp.data.model.UserModel
import com.kikulabs.storyapp.data.model.UserPreference
import com.kikulabs.storyapp.data.response.LoginResponse
import com.kikulabs.storyapp.data.response.LoginResult
import com.kikulabs.storyapp.data.retrofit.ApiConfig
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginViewModel(private val pref: UserPreference) : ViewModel() {
    private val _userLogin = MutableLiveData<LoginResult>()
    val userLogin: LiveData<LoginResult> = _userLogin

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _message = MutableLiveData<String>()
    val message: LiveData<String> = _message


    fun saveUser(user: UserModel) {
        viewModelScope.launch {
            pref.saveUser(user)
        }
    }

    fun loginUser(email: String, password: String) {
        _isLoading.value = true
        ApiConfig().getApiService().loginUser(email, password)
            .enqueue(object : Callback<LoginResponse> {
                override fun onResponse(
                    call: Call<LoginResponse>,
                    response: Response<LoginResponse>,
                ) {
                    _isLoading.value = false
                    if (response.isSuccessful) {
                        _message.value = response.body()?.message
                        _userLogin.value = response.body()?.loginResult
                    }
                    if (!response.isSuccessful) {
                        _message.value = response.message()
                    }

                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    _isLoading.value = false
                    Log.d("RETROFIT_TAG", t.message.toString())
                }

            })
    }

}