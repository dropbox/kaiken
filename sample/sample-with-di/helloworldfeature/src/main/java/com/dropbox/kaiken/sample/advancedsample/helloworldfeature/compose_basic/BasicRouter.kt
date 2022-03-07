package com.dropbox.kaiken.sample.advancedsample.helloworldfeature.compose_basic

import android.util.Log
import androidx.annotation.StringRes
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarResult
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.dropbox.kaiken.sample.advancedsample.helloworldfeature.authRequiredComposable
import com.dropbox.kaiken.sample.advancedsample.helloworldfeature.authed_example.ui.FavoriteFilmsPresenter
import com.dropbox.kaiken.sample.advancedsample.helloworldfeature.authed_example.ui.FormPresenter
import com.dropbox.kaiken.sample_with_di.helloworldfeature.R
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

sealed class TabItem(val route: String, @StringRes val titleId: Int, val icon: ImageVector) {
    object Home : TabItem("home", R.string.home_tab_title, Icons.Filled.Home)
    object Form : TabItem("form", R.string.form_tab_title, Icons.Filled.CheckCircle)
    object Films : TabItem("films", R.string.films_tab_title, Icons.Filled.Face)
}

@Composable
fun BasicRouter() {
    val navController = rememberNavController()
    val scope = rememberCoroutineScope()
    val scaffoldState = rememberScaffoldState()

    val showSnackbar: (String) -> Job = { message ->
        scope.launch {
            scaffoldState.snackbarHostState.showSnackbar(message)
        }
    }

    val tabs = listOf(TabItem.Home, TabItem.Form, TabItem.Films)

    Scaffold(
        scaffoldState = scaffoldState,
        bottomBar = {
            BottomNavigation {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                tabs.forEach { screen ->
                    BottomNavigationItem(
                        icon = { Icon(screen.icon, contentDescription = null) },
                        label = { Text(stringResource(screen.titleId)) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            navController.navigate(screen.route) {
                                // Pop up to the start destination of the graph to
                                // avoid building up a large stack of destinations
                                // on the back stack as users select items
                                popUpTo(navController.graph.findStartDestination().id) {}
                                // Avoid multiple copies of the same destination when
                                // reselecting the same item
                                launchSingleTop = true
                            }
                        }
                    )
                }
            }
        }
    ) {
        NavHost(navController = navController, startDestination = TabItem.Home.route) {
            composable(TabItem.Home.route) {
                BasicScreen()
            }

            authRequiredComposable(TabItem.Form.route) { _: NavBackStackEntry, presenter: BasicPresenter ->
                BasicFormScreen(
                    model = presenter.model
                ) {
                    answer: BasicPresenter.Event -> presenter.events.tryEmit(answer)
                }
            }

            authRequiredComposable(TabItem.Films.route) { _: NavBackStackEntry, presenter: BasicFilmsPresenter ->
                LaunchedEffect(presenter.effects) {
                    presenter.effects.collect {
                        when (it) {
                            is BasicFilmsPresenter.ShowSnackbar -> showSnackbar(it.message)
                        }
                    }
                }

                BasicFilmsScreen(
                    model = presenter.model
                ) {
                    event: BasicFilmsPresenter.Event -> presenter.events.tryEmit(event)
                }
            }
        }
    }
}