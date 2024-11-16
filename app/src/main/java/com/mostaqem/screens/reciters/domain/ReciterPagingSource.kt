package com.mostaqem.screens.reciters.domain

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.mostaqem.screens.reciters.data.reciter.Reciter
import com.mostaqem.screens.surahs.data.Surah

class ReciterPagingSource (
    private val api: ReciterService
) : PagingSource<Int, Reciter>(){
    override fun getRefreshKey(state: PagingState<Int, Reciter>): Int? {
        return null
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Reciter> {
        try {
            val nextPage = params.key ?: 1
            val repos: List<Reciter> = api.getReciters(page = nextPage).response.reciters
            return LoadResult.Page(
                data = repos,
                prevKey = if (nextPage == 1) null else nextPage - 1,
                nextKey = nextPage + 1
            )
        } catch (e: Exception) {
            return LoadResult.Error(e)
        }
    }

}