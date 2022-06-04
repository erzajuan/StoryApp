package com.bastilla.storyapp.module.main.home.pagging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.bastilla.storyapp.api.ApiService
import com.bastilla.storyapp.model.StoryModel

class StoryPagingSource(private val apiService: ApiService, private val token: String) :
    PagingSource<Int, StoryModel>() {
    override fun getRefreshKey(state: PagingState<Int, StoryModel>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, StoryModel> {
        return try {
            val position = params.key ?: INITIAL_PAGE_INDEX
            val responseData = apiService.getAllStories(
                "Bearer $token",
                position,
                params.loadSize,
                1
            ).listStory as List<StoryModel>
            LoadResult.Page(
                data = responseData,
                prevKey = if (position == INITIAL_PAGE_INDEX) null else position - 1,
                nextKey = if (responseData.isNullOrEmpty()) null else position + 1
            )
        } catch (exception: Exception) {
            return LoadResult.Error(exception)
        }
    }

    private companion object {
        const val INITIAL_PAGE_INDEX = 1
    }
}