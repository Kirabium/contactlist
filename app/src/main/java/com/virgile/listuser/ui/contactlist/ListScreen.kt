package com.virgile.listuser.ui.contactlist

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.virgile.listuser.model.Contact
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch

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
        is ListViewState.Loading -> {
            // Handle loading state if necessary
        }

        is ListViewState.Success -> ContactList(
            (viewState as ListViewState.Success).contacts,
            navController,
            viewModel
        )

        is ListViewState.Error -> Text(text = (viewState as ListViewState.Error).exception)
    }
}

@Composable
fun ContactList(
    contacts: List<Contact>,
    navController: NavController,
    viewModel: ListViewModel
) {
    val lazyListState = rememberLazyListState()
    val canLoadMoreItems by viewModel.canLoadMoreItems.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LazyColumn(state = lazyListState) {
        items(contacts) { contact ->
            ContactRow(
                contact, navController
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


@Composable
fun ContactRow(
    contact: Contact,
    navController: NavController
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable {
                println("id : " + contact.id)
                navController.navigate("contact_details/${contact.id}")
            },
        elevation = 4.dp,
        shape = RoundedCornerShape(8.dp)
    ) {
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
}
