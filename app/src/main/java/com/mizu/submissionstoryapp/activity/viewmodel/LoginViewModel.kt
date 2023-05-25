package com.mizu.submissionstoryapp.activity.viewmodel

import android.util.Log
import androidx.lifecycle.*
import com.mizu.submissionstoryapp.api.ApiConfig
import com.mizu.submissionstoryapp.api.LoginResponse
import com.mizu.submissionstoryapp.api.LoginResult
import com.mizu.submissionstoryapp.datastore.LoginPreferences
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class LoginViewModel(private val pref: LoginPreferences): ViewModel()
{

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _loginResult = MutableLiveData<LoginResult>()
    val loginResult: LiveData<LoginResult> = _loginResult

    private val _loginResponse = MutableLiveData<Boolean>()
    val loginResponse: LiveData<Boolean> = _loginResponse

    companion object {
        private const val TAG = "LoginViewModel"
    }

    fun postLogin(email: String, password: String){
        _isLoading.value = true
        val client = ApiConfig.getApiService().postLogin(email, password)
        client.enqueue(object: Callback<LoginResponse>{
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                _isLoading.value = false
                val responseBody = response.body()
                if(response.isSuccessful && responseBody != null){
                    _loginResult.value = response.body()?.loginResult
                }else{
                    _loginResponse.value = true
                    Log.e(TAG, "onFailure: ${response.code()} ${response.message()}")
                    Log.e(TAG, "onFailure: ${response.errorBody()?.string()}")

                }
                _loginResponse.value = false
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {

                _isLoading.value = false
                Log.e(TAG, "onFailure: ${t.message.toString()}")
            }

        })
    }

    fun getSessionToken(): LiveData<String>{
        return pref.getSessionToken().asLiveData()
    }

    fun saveSessionToken(sessionToken: String){
        viewModelScope.launch {
            pref.saveSessionToken(sessionToken)
        }
    }

    fun removeSessionToken(){
        viewModelScope.launch {
            pref.removeSessionToken()
        }
    }

}