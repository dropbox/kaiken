package com.dropbox.kaiken.sample.advancedsample.helloworldfeature.authed_example.ui

import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.filter
import androidx.compose.ui.test.hasAnyChild
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onChildren
import androidx.compose.ui.test.onNodeWithTag
import com.dropbox.kaiken.sample.advancedsample.helloworldfeature.authed_example.repository.Favorite
import com.dropbox.kaiken.sample.advancedsample.helloworldfeature.authed_example.repository.FavoriteWithFilm
import com.dropbox.kaiken.sample.advancedsample.helloworldfeature.authed_example.repository.Film
import org.junit.Rule
import org.junit.Test

class HomeScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun givenHomeScreen_WhenLoading_ThenShowsSpinnerAndLoadsData() {
        val model = HomePresenter.HomeModel(true)
        val eventHandler = { event: HomePresenter.HomeEvent ->
            assert(event is HomePresenter.LoadFavorites)
            true
        }

        composeTestRule.setContent {
            HomeScreenInner(model, eventHandler)
        }

        composeTestRule.onNodeWithTag("Loading Spinner").assertIsDisplayed()
    }

    @Test
    fun givenHomeScreen_WhenFavoritesLoaded_ThenShowsYourFavorites() {
        val favoritesList = listOf(
            FavoriteWithFilm(Film("", "Hello", "", "")),
            FavoriteWithFilm(Film("", "World", "", "")),
        )
        val model = HomePresenter.HomeModel(false, "", favoritesList)

        composeTestRule.setContent {
            HomeScreenInner(model, { true })
        }

        composeTestRule.onNodeWithTag("Favorites").assertIsDisplayed()
        composeTestRule.onNodeWithTag("Favorites")
            .onChildren()
            .filter(hasTestTag("Favorite"))
            .assertCountEquals(2)
    }
}