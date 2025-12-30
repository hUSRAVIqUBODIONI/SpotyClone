package com.example.spotyclone

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.example.spotyclone.service.MusicService
import com.example.spotyclone.ui.screens.MusicScreen
import com.example.spotyclone.ui.screens.MusicScreenRoot
import com.example.spotyclone.ui.theme.SpotyCloneTheme
import com.example.spotyclone.ui.utils.Screens
import com.example.spotyclone.viewmodel.MusicViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var navController: NavHostController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



        startService(Intent(this, MusicService::class.java))


        setContent {
            SpotyCloneTheme {
                MainScreen(navController)
            }
        }
    }
}


@Composable
fun MainScreen(navController: NavHostController) {

    val screens = listOf(
        Screens.AppWriteScreen,
        Screens.LocalMusicScreen,

    )

    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing,
        bottomBar = { NavigationBottomBar(navController = navController, items = screens) }
    ) { innerPadding ->
        NavGraph(navController, Modifier.padding(innerPadding))
    }

}


@Composable
fun NavigationBottomBar(navController: NavController, items: List<Screens>) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route?.substringBefore("?")

    NavigationBar {
        items.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route + "?param=${item.screenParam}") {
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                label = { Text(item.title) },
                icon = { },
                modifier = Modifier,
                enabled = currentRoute != item.route,
                alwaysShowLabel = true
            )
        }
    }
}


@Composable
fun NavGraph(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(navController, startDestination = Screens.AppWriteScreen.route, modifier = modifier) {

        composable(
            route = "appWrite?param={param}",
            arguments = listOf(navArgument("param") { defaultValue = "root" })
        ) {
            val vm: MusicViewModel = hiltViewModel()
            MusicScreenRoot(vm)
        }

        composable(
            route = "local?param={param}",
            arguments = listOf(navArgument("param") { defaultValue = "room" })
        ) {
            val vm: MusicViewModel = hiltViewModel()
            MusicScreenRoot(vm)
        }
        composable(route = Screens.MusicScreen.route) {
            MusicScreen()
        }
    }
}