package com.mizu.submissionstoryapp.activity.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mizu.submissionstoryapp.datastore.LoginPreferences

class ViewModelFactory(private val pref: LoginPreferences): ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel(pref) as T
        }

        throw java.lang.IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }

}