package com.virgile.listuser.usecases

import com.virgile.listuser.data.local.entity.ContactLocal
import com.virgile.listuser.data.local.repository.ContactLocalRepository
import com.virgile.listuser.data.remote.repositories.ContactRemoteRepository
import com.virgile.listuser.model.Contact
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject

interface CleanContactListUseCase {
    suspend fun invoke(): Flow<List<Contact>>
}

class CleanContactListUseCaseImpl @Inject constructor(
    private val contactRemoteRepository: ContactRemoteRepository,
    private val contactLocalRepository: ContactLocalRepository,
) : CleanContactListUseCase {
    override suspend fun invoke(): Flow<List<Contact>> = flow {
        try {
            // Fetch the first page of contacts from the remote repository
            val remoteData = contactRemoteRepository.getContacts(10, 1)

            val localContacts = remoteData.results.map { contactRemote ->
                ContactLocal(
                    id = contactRemote.login.uuid,
                    firstname = contactRemote.name.first,
                    lastname = contactRemote.name.last,
                    city = contactRemote.location.city,
                    email = contactRemote.email,
                    phone = contactRemote.phone,
                    picture = contactRemote.picture.large,
                    country = contactRemote.location.country,
                    postcode = contactRemote.location.postcode,
                    page = 1,
                )
            }

            // Clear the database
            contactLocalRepository.clearAll()

            // Inserting into local database
            localContacts.forEach { contactLocal ->
                contactLocalRepository.insertContact(contactLocal)
            }

            emit(localContacts.map { it.toContact() })

        } catch (e: IOException) {
            // If there is an IOException (no internet connection), return data from local cache
            val localContacts = contactLocalRepository.getContactsByPage(1).first()
            emit(localContacts.map { it.toContact() })
        } catch (e: Exception) {
            // For other exceptions, you can either handle them or rethrow
            throw e
        }
    }
}
