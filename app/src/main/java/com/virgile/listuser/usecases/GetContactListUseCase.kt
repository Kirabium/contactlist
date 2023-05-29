package com.virgile.listuser.usecases

import android.util.Log
import com.virgile.listuser.data.local.entity.ContactLocal
import com.virgile.listuser.data.local.repository.ContactLocalRepository
import com.virgile.listuser.data.remote.repositories.ContactRemoteRepository
import com.virgile.listuser.model.Contact
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import java.io.IOException
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
            try {
                val remoteData = contactRemoteRepository.getContacts(10, page)
                val contactLocalList = remoteData.results.map { contactRemote ->
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
                        page = page,
                    )
                }

                contactLocalList.forEach { contactLocal ->
                    contactLocalRepository.insertContact(contactLocal)
                }

                emit(contactLocalList.map { it.toContact() })
            } catch (e: IOException) {
                // Handle network failure
                Log.e("internet", "no signal")
                // Emit an empty list or an error state
                emit(emptyList())
            }
        }
    }
}


fun ContactLocal.toContact(): Contact {
    return Contact(id, firstname, lastname, city, email, phone, picture, country, postcode)
}

