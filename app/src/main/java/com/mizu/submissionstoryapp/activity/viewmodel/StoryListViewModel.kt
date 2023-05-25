package com.mizu.submissionstoryapp.activity.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mizu.submissionstoryapp.api.ApiConfig
import com.mizu.submissionstoryapp.api.ListStoryItem
import com.mizu.submissionstoryapp.api.ListStoryResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StoryListViewModel: ViewModel() {

    private val _listStory = MutableLiveData<List<ListStoryItem>>()
    val listStory: LiveData<List<ListStoryItem>> = _listStory

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading : LiveData<Boolean> = _isLoading

    companion object{
        const val TAG = "StoryListViewModel"
    }

    fun getAllStories(token: String){
        _isLoading.value = true
        val client = ApiConfig.getApiService().getAllStories(token)
        client.enqueue(object: Callback<ListStoryResponse>{
            override fun onResponse(
                call: Call<ListStoryResponse>,
                response: Response<ListStoryResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    _listStory.value = response.body()?.listStory
                }else{
                    Log.e(TAG, "onFailure: ${response.code()} ${response.message()}")
                    Log.e(TAG, "onFailure: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<ListStoryResponse>, t: Throwable) {
                _isLoading.value = false
                Log.e(TAG, "onFailure: ${t.message.toString()}")
            }

        })
    }
}