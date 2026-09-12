package com.example.data.repository

import com.example.data.local.FeedbackDao
import com.example.data.local.QrDao
import com.example.data.model.FeedbackItem
import com.example.data.model.QrItem
import kotlinx.coroutines.flow.Flow

class QrRepository(
    private val qrDao: QrDao,
    private val feedbackDao: FeedbackDao
) {
    val allScannedItems: Flow<List<QrItem>> = qrDao.getScannedQrItems()
    val allCreatedItems: Flow<List<QrItem>> = qrDao.getCreatedQrItems()
    val allFeedback: Flow<List<FeedbackItem>> = feedbackDao.getAllFeedback()

    suspend fun saveQrItem(item: QrItem): Long {
        return qrDao.insertQrItem(item)
    }

    suspend fun deleteQrItem(item: QrItem) {
        qrDao.deleteQrItem(item)
    }

    suspend fun deleteQrItemById(id: Long) {
        qrDao.deleteById(id)
    }

    suspend fun clearHistory(isCreated: Boolean) {
        qrDao.deleteAllByType(isCreated)
    }

    suspend fun submitFeedback(feedback: FeedbackItem): Long {
        return feedbackDao.insertFeedback(feedback)
    }

    suspend fun deleteFeedback(id: Long) {
        feedbackDao.deleteFeedbackById(id)
    }
}
