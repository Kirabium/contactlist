package com.virgile.listuser

import com.virgile.listuser.data.local.repository.ContactLocalRepository
import com.virgile.listuser.usecases.DeleteContactException
import com.virgile.listuser.usecases.DeleteContactInListUseCaseImpl
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

class DeleteContactInListUseCaseImplTest {

    @Mock
    private lateinit var contactLocalRepository: ContactLocalRepository

    private lateinit var deleteContactInListUseCaseImpl: DeleteContactInListUseCaseImpl

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        deleteContactInListUseCaseImpl = DeleteContactInListUseCaseImpl(contactLocalRepository)
    }

    @Test
    fun `invoke should delete contact and return success result`() = runBlocking {
        // Arrange
        val contactId = 1
        `when`(contactLocalRepository.deleteContactById(contactId)).thenReturn(Unit)

        // Act
        val result = deleteContactInListUseCaseImpl.invoke(contactId)

        // Assert
        assertEquals(Result.success(Unit), result)
    }

    @Test
    fun `invoke should return failure result when deleting contact throws an exception`() = runBlocking {
        // Arrange
        val contactId = 1
        val expectedException = Exception("Failed to delete contact")
        `when`(contactLocalRepository.deleteContactById(contactId)).thenAnswer { throw Exception("Failed to delete contact") }


        // Act
        val result = deleteContactInListUseCaseImpl.invoke(contactId)

        // Assert
        val expectedFailure = Result.failure<Unit>(DeleteContactException("Failed to delete contact with id: $contactId", expectedException))
        assertEquals(expectedFailure.isFailure, result.isFailure)
    }
}
