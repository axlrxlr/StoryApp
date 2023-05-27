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

class MapsViewModel: ViewModel() {

    companion object {
        private const val TAG = "MapsViewModel"
    }

    private val _listStory = MutableLiveData<List<ListStoryItem>>()
    val listStory: LiveData<List<ListStoryItem>> = _listStory

    fun getAllStoryWithLoc(token: String){
        val client = ApiConfig.getApiService().getStoriesWithLoc(token, 1)
        client.enqueue(object : Callback<ListStoryResponse>{
            override fun onResponse(
                call: Call<ListStoryResponse>,
                response: Response<ListStoryResponse>
            ) {
                if(response.isSuccessful){
                    _listStory.value = response.body()?.listStory
                    Log.d(TAG,response.body().toString())
                }else{
                    Log.e(TAG, "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<ListStoryResponse>, t: Throwable) {
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })
    }

}