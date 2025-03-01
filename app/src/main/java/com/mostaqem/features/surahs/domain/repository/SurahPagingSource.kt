package com.mostaqem.features.surahs.domain.repository

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.mostaqem.features.surahs.data.Surah
import javax.inject.Inject


class SurahPagingSource(
    private val api: SurahService,
) : PagingSource<Int, Surah>() {
    override fun getRefreshKey(state: PagingState<Int, Surah>): Int? {
        return null
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Surah> {
        try {
            val nextPage = params.key ?: 1
            val repos: List<Surah> = api.getSurahs(page = nextPage).body()!!.response.surahData
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