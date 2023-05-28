package com.virgile.listuser.data.remote.interfaces

import com.virgile.listuser.data.remote.model.Response
import kotlinx.coroutines.flow.Flow
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("?seed=lydia")
    suspend fun getContacts(@Query("results") results : Int , @Query("page") page : Int) : Response
}
