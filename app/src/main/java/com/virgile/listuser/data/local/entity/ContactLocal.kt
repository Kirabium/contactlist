package com.virgile.listuser.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "contacts")
data class ContactLocal(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val firstname: String,
    val lastname: String,
    val city: String,
    val email: String,
    val phone: String,
    val picture: String,
    val country: String,
    val postcode: String,
    val page: Int
)
