package com.virgile.listuser.usecases

import com.virgile.listuser.data.local.entity.ContactLocal
import com.virgile.listuser.data.local.repository.ContactLocalRepository
import com.virgile.listuser.data.remote.repositories.ContactRemoteRepository
import com.virgile.listuser.model.Contact
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

interface CleanContactListUseCase {
    suspend fun invoke(): Flow<List<Contact>>
}

class CleanContactListUseCaseImpl @Inject constructor(
    private val contactRemoteRepository: ContactRemoteRepository,
    private val contactLocalRepository: ContactLocalRepository,
) : CleanContactListUseCase {
    override suspend fun invoke(): Flow<List<Contact>> = flow {
        // Clear the database
        contactLocalRepository.clearAll()

        // Fetch the first page of contacts from the remote repository
        val remoteData = contactRemoteRepository.getContacts(10, 1)
        remoteData.results.map { contactRemote ->
            ContactLocal(
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
            .let { contactLocalList ->
                contactLocalList.forEach { contactLocal ->
                    contactLocalRepository.insertContact(contactLocal)
                }
                emit(contactLocalList.map { it.toContact() })
            }
    }

}

