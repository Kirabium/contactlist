package com.virgile.listuser.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.virgile.listuser.data.local.dao.ContactDao
import com.virgile.listuser.data.local.entity.ContactLocal

@Database(
    entities = [ContactLocal::class],
    exportSchema = false,
    version = 1
)

abstract class database : RoomDatabase() {
    abstract val dao : ContactDao
}