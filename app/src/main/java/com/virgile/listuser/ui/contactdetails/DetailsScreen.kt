package com.virgile.listuser.ui.contactdetails

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.virgile.listuser.R
import com.virgile.listuser.model.Contact

@Composable
fun DetailsScreen(
    navController: NavController,
    viewModel: DetailsViewModel = hiltViewModel()
) {
    val contact = viewModel.state


    contact.let {
        val context = LocalContext.current

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(text = "${it.firstname} ${it.lastname}") },
                    backgroundColor = Color.Transparent,
                    elevation = 0.dp,
                    navigationIcon = {
                        IconButton(onClick = { navController.navigateUp() }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Retour")
                        }
                    }
                )
            },
            content = { paddingValues ->
                DetailsContent(
                    contact = it,
                    modifier = Modifier.padding(paddingValues)
                )
            }
        )
    }
}

@Composable
fun DetailsContent(contact: Contact, modifier: Modifier = Modifier) {
    Column(modifier = modifier
        .verticalScroll(rememberScrollState())
        .padding(horizontal = 32.dp)) {
        Image(
            painter = rememberImagePainter(contact.picture),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextFieldReadOnly(
            text = contact.phone,
            label = "Phone",
            leadingIconRes = R.drawable.baseline_local_phone_24

        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextFieldReadOnly(
            text = contact.city,
            label = "City",
            leadingIconRes = R.drawable.baseline_location_city_24

        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextFieldReadOnly(
            text = contact.email,
            label = "Email",
            leadingIconRes = R.drawable.baseline_mail_24

        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextFieldReadOnly(
            text = contact.country,
            label = "Country",
            leadingIconRes = R.drawable.baseline_flag_24
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextFieldReadOnly(
            text = contact.postcode,
            label = "Postcode",
            leadingIconRes = R.drawable.baseline_signpost_24
        )
    }
}

@Composable
fun OutlinedTextFieldReadOnly(text: String, label: String, leadingIconRes: Int) {
    val leadingIcon = painterResource(id = leadingIconRes)
    OutlinedTextField(
        value = text,
        onValueChange = {},
        label = { Text(text = label) },
        leadingIcon = { Icon(painter = leadingIcon, contentDescription = null) },
        readOnly = true,
        modifier = Modifier.fillMaxWidth()
    )
}
