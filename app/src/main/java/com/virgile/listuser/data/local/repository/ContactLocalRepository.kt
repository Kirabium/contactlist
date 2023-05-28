package com.virgile.listuser.data.local.repository

import com.virgile.listuser.data.local.dao.ContactDao
import com.virgile.listuser.data.local.entity.ContactLocal
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import javax.inject.Singleton

@Singleton
class ContactLocalRepository @Inject constructor(private val contactDao: ContactDao) {


    suspend fun insertContact(contact: ContactLocal) = contactDao.insert(contact)

    suspend fun getContactsByPage(page: Int): Flow<List<ContactLocal>> = contactDao.getByPage(page)


    suspend fun deleteContactById(id: Int) = contactDao.deleteById(id)

    suspend fun clearAll() = contactDao.deleteAll()


}
