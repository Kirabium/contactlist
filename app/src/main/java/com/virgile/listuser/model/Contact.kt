package com.virgile.listuser.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.io.Serializable

@Parcelize
data class Contact(
    val id: String,
    val firstname: String,
    val lastname: String,
    val city: String,
    val email: String,
    val phone: String,
    val picture: String,
    val country: String,
    val postcode: String,
) : Parcelable, Serializable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Contact

        if (id != other.id) return false
        if (firstname != other.firstname) return false
        if (lastname != other.lastname) return false
        if (city != other.city) return false
        if (email != other.email) return false
        if (phone != other.phone) return false
        if (picture != other.picture) return false
        if (country != other.country) return false
        if (postcode != other.postcode) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + firstname.hashCode()
        result = 31 * result + lastname.hashCode()
        result = 31 * result + city.hashCode()
        result = 31 * result + email.hashCode()
        result = 31 * result + phone.hashCode()
        result = 31 * result + picture.hashCode()
        result = 31 * result + country.hashCode()
        result = 31 * result + postcode.hashCode()
        return result
    }


}
