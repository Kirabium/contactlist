package com.virgile.listuser.ui.contactlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.virgile.listuser.model.Contact
import com.virgile.listuser.usecases.CleanContactListUseCaseImpl
import com.virgile.listuser.usecases.DeleteContactInListUseCaseImpl
import com.virgile.listuser.usecases.GetContactListUseCaseImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ListViewModel @Inject constructor(
    private val getContactListUseCaseImpl: GetContactListUseCaseImpl,
    private val deleteContactInListUseCaseImpl: DeleteContactInListUseCaseImpl,
    private val cleanContactListUseCaseImpl: CleanContactListUseCaseImpl
) : ViewModel() {

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean>
        get() = _isRefreshing.asStateFlow()

    private val _viewState = MutableStateFlow<ListViewState>(ListViewState.Loading)
    val viewState: StateFlow<ListViewState> =
        _viewState.asStateFlow() // Make the state flow read only from outside
    private var currentPage = 1
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> =
        _isLoading.asStateFlow() // Make loading indicator read only from outside

    // Assume initially that we can load more items
    private val _canLoadMoreItems = MutableStateFlow(true)
    val canLoadMoreItems: StateFlow<Boolean> = _canLoadMoreItems.asStateFlow()

    init {
        loadContacts()
    }

    fun loadMoreItems() = viewModelScope.launch {
        if (_isLoading.value || !_canLoadMoreItems.value) return@launch // If already loading, return early
        currentPage++
        _isLoading.emit(true) // Emit loading state
        loadContacts()
    }

    private fun loadContacts() = viewModelScope.launch {
        getContactListUseCaseImpl.invoke(currentPage)
            .catch { exception -> // Catch exceptions and handle it
                _viewState.emit(ListViewState.Error(exception.message ?: "Unknown error"))
                _isLoading.emit(false)
            }
            .collectLatest { result ->
                handleContactResult(result)
                _canLoadMoreItems.emit(result.isNotEmpty())
            }
    }

    fun deleteItem(contact: Contact) {
        viewModelScope.launch {
            val result = deleteContactInListUseCaseImpl.invoke(contact.id)
            if (result.isSuccess) {
                val currentViewState = _viewState.value
                if (currentViewState is ListViewState.Success) {
                    _viewState.emit(currentViewState.copy(contacts = currentViewState.contacts.filter { it != contact }))
                }
            } else {
                // Handle the error
                _viewState.emit(ListViewState.Error("Failed to delete contact with id: ${contact.id}"))
            }
        }
    }

    fun cleanAndRefreshData() {
        viewModelScope.launch {
            _isLoading.emit(true) // Emit loading state
            cleanContactListUseCaseImpl.invoke()
                .catch { exception -> // Catch exceptions and handle it
                    _viewState.emit(ListViewState.Error(exception.message ?: "Unknown error"))
                    _isLoading.emit(false)
                }
                .collectLatest { result ->
                    _viewState.emit(ListViewState.Success(result))
                    currentPage = 1
                    _isLoading.emit(false) // Emit loading complete
                }
        }
    }

    private suspend fun handleContactResult(result: List<Contact>) {
        val currentViewState = _viewState.value
        if (currentViewState is ListViewState.Success) {
            _viewState.emit(currentViewState.copy(contacts = currentViewState.contacts + result))
        } else {
            _viewState.emit(ListViewState.Success(result))
        }
        _isLoading.emit(false) // Emit loading complete
    }
}
