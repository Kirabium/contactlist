package com.virgile.listuser.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.virgile.listuser.model.Contact
import com.virgile.listuser.ui.contactdetails.DetailsScreen
import com.virgile.listuser.ui.contactlist.ListScreen

@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "list_screen" ){
        composable(
            route = "list_screen"
        ){
            ListScreen(
                navController = navController

            )
        }
        composable(route = "contact_details"+"/{contactId}"){
            DetailsScreen(navController = navController)
        }
    }
}
