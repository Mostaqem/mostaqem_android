package com.mostaqem.features.surahs.domain.repository

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.mostaqem.features.surahs.data.Surah
import com.mostaqem.features.surahs.data.SurahSortBy
import javax.inject.Inject


class SurahPagingSource(
    private val api: SurahService, private val defaultSortBy: SurahSortBy
) : PagingSource<Int, Surah>() {

    override fun getRefreshKey(state: PagingState<Int, Surah>): Int? {
        return null
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Surah> {
        try {
            val nextPage = params.key ?: 1
            val response = api.getSurahs(page = nextPage).body()?.response
            val data = response?.surahData ?: emptyList()
            val sortedSurahs = when (defaultSortBy) {
                SurahSortBy.NAME -> data.sortedBy { it.arabicName }
                SurahSortBy.REVELATION_PLACE -> data.sortedBy { it.revelationPlace }
                SurahSortBy.ID -> data.sortedBy { it.id }
            }
            return LoadResult.Page(
                data = sortedSurahs,
                prevKey = if (nextPage == 1) null else nextPage - 1,
                nextKey = nextPage + 1
            )
        } catch (e: Exception) {
            return LoadResult.Error(e)
        }
    }


}