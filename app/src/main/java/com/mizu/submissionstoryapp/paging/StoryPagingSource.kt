package com.mizu.submissionstoryapp.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.mizu.submissionstoryapp.api.ApiService
import com.mizu.submissionstoryapp.api.ListStoryItem

class StoryPagingSource (private val apiService: ApiService, token: String) : PagingSource<Int, ListStory>() {
    private companion object {
        const val INITIAL_PAGE_INDEX = 1
    }

    private val token = "Bearer $token"

    override fun getRefreshKey(state: PagingState<Int, ListStoryItem>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ListStoryItem> {
        return try {
            val position = params.key ?: INITIAL_PAGE_INDEX
            val responseData = apiService.getAllStories(token, position, params.loadSize)

            LoadResult.Page(
                data = responseData,
                prevKey = if (position == INITIAL_PAGE_INDEX) null else position - 1,
                nextKey = if (responseData.listStory.isNullOrEmpty()) null else position + 1
            )
        } catch (e: java.lang.Exception) {
            return LoadResult.Error(e)
        }
    }

}