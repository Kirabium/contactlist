package com.virgile.listuser.ui.contactdetails

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.virgile.listuser.data.local.database.database
import com.virgile.listuser.model.Contact
import com.virgile.listuser.ui.contactlist.ListViewState
import com.virgile.listuser.usecases.GetContactUseCaseImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(
    private val getContactUseCaseImpl: GetContactUseCaseImpl,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    var state by mutableStateOf(Contact("", "", "", "", "","","","",""))

    init {
        val contactId = savedStateHandle.get<String>("contactId")
        println("contactId : " + contactId)

        if (contactId != null) {
            viewModelScope.launch {
                getContactUseCaseImpl.invoke(contactId).collect { contact ->
                    state = contact
                }
            }
        }
    }

    private fun setContact(contact: Contact) {
        state = contact
    }
}
