package com.virgile.listuser.usecases

import android.util.Log
import com.virgile.listuser.data.local.entity.ContactLocal
import com.virgile.listuser.data.local.repository.ContactLocalRepository
import com.virgile.listuser.data.remote.repositories.ContactRemoteRepository
import com.virgile.listuser.model.Contact
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

interface GetContactUseCase {
    suspend fun invoke(id: String): Flow<Contact>
}

class GetContactUseCaseImpl @Inject constructor(
    private val contactLocalRepository: ContactLocalRepository,
) : GetContactUseCase {
    override suspend fun invoke(id: String): Flow<Contact> = flow {
        try {
            val localData = contactLocalRepository.getContactById(id).firstOrNull()

            if (localData != null) {
                emit(localData.toContact())
            } else {
                // todo : improve user experience with UI feedback
                throw Exception("No contact found with id $id")
            }
        } catch (e: Exception) {
            // Log and handle any unexpected exceptions
            Log.e("GetContactUseCase", "Error getting contact with id $id", e)
        }
    }
}
