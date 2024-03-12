package com.chanho.compose

import androidx.compose.material.DrawerState
import androidx.compose.material.DrawerValue
import androidx.compose.material.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.CoroutineScope

@Composable
fun TodoNavGraph(
    modifier:Modifier= Modifier,
    navController :NavHostController = rememberNavController(),
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    drawerState: DrawerState = rememberDrawerState(initialValue = DrawerValue.Closed),
    startDestination:String = TodoDestinations.TASKS_ROUTE,
    navActions:TodoNavigationActions = remember(navController){
        TodoNavigationActions(navController)
    }
){
    val currentNavBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentNavBackStackEntry?.destination?.route?:startDestination

//    NavHost(
//        navController = navController,
//        startDestination = startDestination,
//        modifier = modifier
//    ){
//
//    }
}