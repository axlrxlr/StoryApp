package com.mizu.submissionstoryapp.paging

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.mizu.submissionstoryapp.api.ApiService
import com.mizu.submissionstoryapp.api.ListStoryItem
import com.mizu.submissionstoryapp.api.ListStoryResponse

class StoryPagingSource (private val apiService: ApiService, token: String) : PagingSource<Int, ListStoryItem>() {
    private companion object {
        const val INITIAL_PAGE_INDEX = 1
    }

    private val userToken = token

    override fun getRefreshKey(state: PagingState<Int, ListStoryItem>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ListStoryItem> {
        return try {
            val position = params.key ?: INITIAL_PAGE_INDEX
            val responseData = apiService.getAllStories(userToken, position, params.loadSize)
            Log.d("StoryPagingSource", "API response received. List size: ${responseData.listStory.size}")
            LoadResult.Page(
                data = responseData.listStory,
                prevKey = if (position == INITIAL_PAGE_INDEX) null else position - 1,
                nextKey = if (responseData.listStory.isNullOrEmpty()) null else position + 1
            )
        } catch (e: java.lang.Exception) {
            Log.e("StoryPagingSource", "Error loading data: ${e.message}", e)
            return LoadResult.Error(e)
        }
    }

}