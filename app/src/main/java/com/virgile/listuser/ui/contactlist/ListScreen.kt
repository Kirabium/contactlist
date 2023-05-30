package com.virgile.listuser.ui.contactlist

import android.util.Log
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.virgile.listuser.model.Contact
import kotlin.math.roundToInt

/**
 * A simple [Fragment] subclass.
 * Use the [ListFragment.newInstance] factory method to
 * create an instance of this fragment.
 */


@Composable
fun ListScreen(
    navController: NavController,
    viewModel: ListViewModel = hiltViewModel()
) {
    val viewState by viewModel.viewState.collectAsState()
    when (viewState) {
        is ListViewState.Loading -> LoadingScreen()

        is ListViewState.Success -> {
            val isLoading by viewModel.isLoading.collectAsState()
            val isRefreshing by viewModel.isRefreshing.collectAsState()
            ContactList(
                contacts = (viewState as ListViewState.Success).contacts,
                navController = navController,
                viewModel = viewModel,
                isLoading = isLoading,
                isRefreshing = isRefreshing
            )
        }

        is ListViewState.Error -> ErrorScreen(errorMessage = (viewState as ListViewState.Error).exception)
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ContactList(
    contacts: List<Contact>,
    navController: NavController,
    viewModel: ListViewModel,
    isLoading: Boolean,
    isRefreshing: Boolean
) {
    val lazyListState = rememberLazyListState()
    val canLoadMoreItems by viewModel.canLoadMoreItems.collectAsState()
    val pullRefreshState =
        rememberPullRefreshState(isRefreshing, { viewModel.cleanAndRefreshData() })

    Box() {
        PullRefreshIndicator(
            refreshing = isRefreshing,
            pullRefreshState,
            Modifier
                .align(Alignment.TopCenter)
                .zIndex(1f)
        )

        LazyColumn(
            state = lazyListState,
            modifier = Modifier
                .pullRefresh(pullRefreshState)
                .zIndex(0f)
        ) {
            items(contacts) { contact ->
                ContactRow(
                    contact = contact,
                    navController = navController,
                    viewModel = viewModel
                )
            }
            item {
                // Trigger loading more items when end of list is almost reached
                if ((lazyListState.layoutInfo.visibleItemsInfo.lastOrNull()?.index
                        ?: 0) >= contacts.size - 5 && canLoadMoreItems
                ) {
                    viewModel.loadMoreItems()
                }
            }
            item {
                // Show loading indicator if loading more items
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.fillMaxWidth())
                }
            }
        }
    }
}

@Composable
fun ContactRow(
    contact: Contact,
    navController: NavController,
    viewModel: ListViewModel
) {
    var offsetX by rememberSaveable { mutableStateOf(0f) }
    val offsetXAnimated by animateFloatAsState(targetValue = offsetX)
    val confirmationDialogVisible = remember { mutableStateOf(false) }

    if (confirmationDialogVisible.value) {
        ConfirmationDialog(
            onConfirm = {
                viewModel.deleteItem(contact); offsetX = 0f; confirmationDialogVisible.value = false
            },
            onDismiss = {
                confirmationDialogVisible.value = false; offsetX =
                0f; confirmationDialogVisible.value = false
            }
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .offset { IntOffset(offsetXAnimated.roundToInt(), 0) }
            .draggable(
                state = rememberDraggableState { delta ->
                    offsetX += delta
                    if (offsetX < -600f) {
                        confirmationDialogVisible.value = true
                    }
                },
                orientation = Orientation.Horizontal,
                onDragStopped = { velocity ->
                    if (offsetX < -500f) {
                        confirmationDialogVisible.value = true
                    }
                    offsetX =
                        0f
                }
            )
            .clickable {
                navController.safeNavigate("contact_details/${contact.id}")
            },
        elevation = 4.dp,
        shape = RoundedCornerShape(8.dp)
    ) {
        ContactDetails(contact)
    }
}

@Composable
fun ContactDetails(contact: Contact) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(8.dp)) {
        Image(
            painter = rememberImagePainter(data = contact.picture),
            contentDescription = "Contact avatar",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(134.dp)
        )

        Spacer(modifier = Modifier.width(8.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = contact.firstname,
                style = MaterialTheme.typography.h6
            )
            Text(
                text = contact.lastname,
                style = MaterialTheme.typography.subtitle1
            )
            Text(
                text = contact.city,
                style = MaterialTheme.typography.body1
            )
        }
    }
}

@Composable
fun ConfirmationDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text("Confirm Deletion") },
        text = { Text("Are you sure you want to delete this contact?") },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text("Delete")
            }
        },
        dismissButton = {
            Button(onClick = { onDismiss() }) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun LoadingScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
fun ErrorScreen(errorMessage: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = "Error: $errorMessage", color = MaterialTheme.colors.error)
    }
}

// Helper function to navigate safely
fun NavController.safeNavigate(directions: String) {
    currentBackStackEntry?.arguments?.putBoolean("allowRecreation", true)
    try {
        navigate(directions)
    } catch (e: Exception) {
        Log.e("Navigation", e.toString())
    }
}
