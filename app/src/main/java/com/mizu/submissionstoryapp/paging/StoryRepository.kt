package com.mizu.submissionstoryapp.paging

import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.mizu.submissionstoryapp.api.ApiService
import com.mizu.submissionstoryapp.api.ListStoryItem

class StoryRepository (private val apiService: ApiService) {
    fun getAllStory(token: String): LiveData<PagingData<ListStoryItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            pagingSourceFactory = {
                StoryPagingSource(apiService, token)
            }
        ).liveData
    }
}