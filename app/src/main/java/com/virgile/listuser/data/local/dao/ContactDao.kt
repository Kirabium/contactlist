package com.virgile.listuser.data.local.dao

import androidx.room.*
import com.virgile.listuser.data.local.entity.ContactLocal
import kotlinx.coroutines.flow.Flow

@Dao
interface ContactDao {

    @Query("SELECT * FROM contacts")
    fun getAll(): Flow<List<ContactLocal>>

    @Query("SELECT * FROM contacts WHERE id = :id")
    fun getById(id: Int): Flow<ContactLocal>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(contact: ContactLocal)

    @Query("SELECT * FROM contacts WHERE page = :page")
    fun getByPage(page: Int): Flow<List<ContactLocal>>

    @Update
    suspend fun update(contact: ContactLocal)

    @Delete
    suspend fun delete(contact: ContactLocal)

    @Query("DELETE FROM contacts")
    suspend fun deleteAll()

    @Query("DELETE FROM contacts WHERE id = :id")
    suspend fun deleteById(id: Int)

}
