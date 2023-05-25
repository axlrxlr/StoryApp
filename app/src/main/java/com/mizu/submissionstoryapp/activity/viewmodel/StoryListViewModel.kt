package com.mizu.submissionstoryapp.activity.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.mizu.submissionstoryapp.api.ApiConfig
import com.mizu.submissionstoryapp.api.ListStoryItem
import com.mizu.submissionstoryapp.api.ListStoryResponse
import com.mizu.submissionstoryapp.di.Injection
import com.mizu.submissionstoryapp.paging.StoryRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StoryListViewModel(private val storyRepository: StoryRepository): ViewModel() {

    fun getAllStory(token: String) : LiveData<PagingData<ListStoryItem>> {
        return storyRepository.getAllStory(token).cachedIn(viewModelScope)
    }
}

class ViewModelFactoryPaging(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StoryListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return StoryListViewModel(Injection.provideRepository(context)) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}