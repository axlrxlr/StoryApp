package com.mizu.submissionstoryapp.activity.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mizu.submissionstoryapp.api.ApiConfig
import com.mizu.submissionstoryapp.api.RegisterResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddStoryViewModel: ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading : LiveData<Boolean> = _isLoading

    private val _postResult = MutableLiveData<RegisterResponse>()
    val postResult: LiveData<RegisterResponse> = _postResult


    companion object {
        const val TAG = "AddStoryViewModel"
    }

    fun postStory(token: String, photo: MultipartBody.Part, description: RequestBody){

        _isLoading.value = true
        val client = ApiConfig.getApiService().postStory(token, photo, description)
        client.enqueue(object: Callback<RegisterResponse> {
            override fun onResponse(
                call: Call<RegisterResponse>,
                response: Response<RegisterResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful && response.body() != null){
                 _postResult.value = response.body()
                }else{
                    Log.e(TAG, "onFailure: ${response.code()} ${response.message()}")
                    Log.e(TAG, "onFailure: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                _isLoading.value = false
                Log.e(TAG, "onFailure: ${t.message.toString()}")
            }

        })

    }

}