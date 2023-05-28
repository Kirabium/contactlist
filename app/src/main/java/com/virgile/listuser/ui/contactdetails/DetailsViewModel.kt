package com.virgile.listuser.ui.contactdetails

import androidx.lifecycle.ViewModel
import com.virgile.listuser.model.Contact
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class DetailsViewModel : ViewModel() {

    private val _contact = MutableStateFlow<Contact?>(null)
    val contact: StateFlow<Contact?> = _contact

    fun setContact(contact: Contact) {
        _contact.value = contact
    }
}
