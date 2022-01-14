package com.dropbox.kaiken.sample.advancedsample.helloworldfeature

import android.os.Bundle
import androidx.annotation.StringRes
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.dropbox.kaiken.sample_with_di.helloworldfeature.R


// * Navigate to a lot of different activities
// * Screen that has a lot of properties on model that get modified
// * Screen that loads data on entry and needs a spinner
// * Screen with a complex list of things
// * Something that needs dependencies at a lower level but not at the higher level

// Asks:
// * How to do navigation and passing objects between screens
//   * Usually with identifiers querying local entries etc.
//   * If there's a case where we want to pass a model that's not persisted
// * How we deal with transient state
// * How do we handle deeplinks into a compose activity (deep-link will be auth optional to redirect to login)
// * Theming dark mode
// * Managing backstack while traversing different activities with different NavGraphs
// * Verify Receivers, Services, make sure they play well as intended
// * Activity Transitions (if there's anything new with Compose)
//  * Potentially cannot transition


class HomeActivity: AuthRequiredComposeActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HomeRouter()
        }
    }
}

@Composable
fun HomeRouter() {
    val navController = rememberNavController()
    val tabs = listOf(Tab.Home, Tab.Settings)
    Scaffold(
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
        NavHost(navController = navController, startDestination = "home") {
            authRequiredComposable(Tab.Home.route) { _, presenter: HomePresenter ->
                HomeScreen(presenter.model,
                        { presenter.events.tryEmit(HomePresenter.LoadSomething) }
                )
            }
            authRequiredComposable(Tab.Settings.route) { _, presenter: HomePresenter ->
                HomeScreen(presenter.model,
                        { presenter.events.tryEmit(HomePresenter.LoadSomething) }
                )
            }
        }
    }
}

sealed class Tab(val route: String, @StringRes val titleId: Int, val icon: ImageVector) {
    object Home : Tab("home", R.string.home_tab_title, Icons.Filled.Home)
    object Settings : Tab("settings", R.string.settings_tab_title, Icons.Filled.Settings)
}
