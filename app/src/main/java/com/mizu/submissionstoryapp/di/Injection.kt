package com.mizu.submissionstoryapp.di

import android.content.Context
import com.mizu.submissionstoryapp.api.ApiConfig

object Injection {
    fun provideRepository(context: Context): StoryRepository {
        val apiService = ApiConfig.getApiService()
        return StoryRepository(apiService)
    }
}