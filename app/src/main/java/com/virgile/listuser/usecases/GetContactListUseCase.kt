package com.virgile.listuser.usecases

import com.virgile.listuser.data.local.entity.ContactLocal
import com.virgile.listuser.data.local.repository.ContactLocalRepository
import com.virgile.listuser.data.remote.repositories.ContactRemoteRepository
import com.virgile.listuser.model.Contact
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

interface GetContactListUseCase {
    suspend fun invoke(page: Int): Flow<List<Contact>>
}

class GetContactListUseCaseImpl @Inject constructor(
    private val contactRemoteRepository: ContactRemoteRepository,
    private val contactLocalRepository: ContactLocalRepository,
) : GetContactListUseCase {
    override suspend fun invoke(page: Int): Flow<List<Contact>> = flow {
        val localData = contactLocalRepository.getContactsByPage(page).firstOrNull()
        if (!localData.isNullOrEmpty()) {
            emit(localData.map { it.toContact() })
        } else {
            val remoteData = contactRemoteRepository.getContacts(10, page)
            remoteData.results.map {  contactRemote ->
                ContactLocal(
                    firstname = contactRemote.name.first,
                    lastname = contactRemote.name.last,
                    city = contactRemote.location.city,
                    email = contactRemote.email,
                    phone = contactRemote.phone,
                    picture = contactRemote.picture.large,
                    country = contactRemote.location.country,
                    postcode = contactRemote.location.postcode,
                    page = page,
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
}


fun ContactLocal.toContact(): Contact {
    return Contact(id, firstname, lastname, city, email, phone, picture, country, postcode)
}

