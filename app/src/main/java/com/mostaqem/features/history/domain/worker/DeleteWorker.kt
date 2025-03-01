package com.mostaqem.features.history.domain.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.mostaqem.features.history.domain.HomeRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class DeleteWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val repository: HomeRepository
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        repository.deleteOldSurahs()
        repository.deleteOldReciters()
        return Result.success()
    }

}