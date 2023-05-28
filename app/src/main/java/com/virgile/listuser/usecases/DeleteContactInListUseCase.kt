package com.virgile.listuser.usecases

import com.virgile.listuser.data.local.repository.ContactLocalRepository
import java.lang.Exception
import javax.inject.Inject

interface DeleteContactInListUseCase {
    suspend fun invoke(id: Int): Result<Unit>
}

class DeleteContactInListUseCaseImpl @Inject constructor(
    private val contactLocalRepository: ContactLocalRepository
) : DeleteContactInListUseCase {
    override suspend fun invoke(id: Int): Result<Unit> {
        return try {
            contactLocalRepository.deleteContactById(id)
            Result.success(Unit)
        } catch (exception: Exception) {
            Result.failure(DeleteContactException("Failed to delete contact with id: $id", exception))
        }
    }
}

class DeleteContactException(message: String, cause: Throwable) : RuntimeException(message, cause)

