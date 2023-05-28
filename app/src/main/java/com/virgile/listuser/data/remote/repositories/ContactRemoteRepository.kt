package com.virgile.listuser.data.remote.repositories

import com.virgile.listuser.data.remote.interfaces.ApiService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ContactRemoteRepository @Inject constructor(private val apiService: ApiService) {
    suspend fun getContacts(results: Int, page: Int) = apiService.getContacts(results, page)
}

