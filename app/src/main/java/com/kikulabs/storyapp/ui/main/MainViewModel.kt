package com.kikulabs.storyapp.ui.main

import android.util.Log
import androidx.lifecycle.*
import com.kikulabs.storyapp.data.model.StoryModel
import com.kikulabs.storyapp.data.model.UserModel
import com.kikulabs.storyapp.data.model.UserPreference
import com.kikulabs.storyapp.data.response.StoriesResponse
import com.kikulabs.storyapp.data.retrofit.ApiConfig
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainViewModel(private val pref: UserPreference) : ViewModel() {

    private val _listStory = MutableLiveData<ArrayList<StoryModel>>()
    val listStories: LiveData<ArrayList<StoryModel>> = _listStory

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun getUser(): LiveData<UserModel> {
        return pref.getUser().asLiveData()
    }

    fun logout() {
        viewModelScope.launch {
            pref.logout()
        }
    }

    fun getAllStories(auth: String) {
        _isLoading.value = true
        ApiConfig().getApiService().getListStory("Bearer $auth")
            .enqueue(object : Callback<StoriesResponse> {
                override fun onResponse(
                    call: Call<StoriesResponse>,
                    response: Response<StoriesResponse>,
                ) {
                    _isLoading.value = false
                    if (response.isSuccessful) {
                        _listStory.postValue(response.body()?.listStory)
                        Log.d("RETROFIT_TAG", response.body()?.listStory.toString())
                    }

                }

                override fun onFailure(call: Call<StoriesResponse>, t: Throwable) {
                    _isLoading.value = false
                    Log.d("RETROFIT_TAG", t.message.toString())
                }

            })
    }
}
