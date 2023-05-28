package com.virgile.listuser.ui.contactlist

import com.virgile.listuser.model.Contact

sealed class ListViewState {
    object Loading : ListViewState()
    data class Success(val contacts: List<Contact>) : ListViewState()
    data class Error(val exception: String) : ListViewState()
}