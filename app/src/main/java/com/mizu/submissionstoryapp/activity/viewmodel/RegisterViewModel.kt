package com.mizu.submissionstoryapp.activity.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mizu.submissionstoryapp.api.ApiConfig
import com.mizu.submissionstoryapp.api.RegisterResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterViewModel: ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _returnRegister = MutableLiveData<RegisterResponse>()
    val returnRegister: LiveData<RegisterResponse> = _returnRegister

    private val _registerResponse = MutableLiveData<Boolean>()
    val registerResponse: LiveData<Boolean> = _registerResponse

    companion object{
        private const val TAG = "RegisterViewModel"
    }

    fun postRegister(name: String, email: String, password: String){
        _isLoading.value = true
        val client = ApiConfig.getApiService().postRegister(name, email, password)
        client.enqueue(object: Callback<RegisterResponse>{
            override fun onResponse(
                call: Call<RegisterResponse>,
                response: Response<RegisterResponse>
            ) {
                _isLoading.value = false
                val responseBody = response.body()
                if(response.isSuccessful && responseBody != null){
                    _returnRegister.value = response.body()
                } else {
                    _registerResponse.value = true
                    Log.e(TAG, "onFailure: ${response.code()} ${response.message()}")
                    Log.e(TAG, "onFailure: ${response.errorBody()?.string()}")
                }
                _registerResponse.value = false
            }

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                _isLoading.value = false
                Log.e(TAG, "onFailure: ${t.message.toString()}")
            }

        })
    }
}