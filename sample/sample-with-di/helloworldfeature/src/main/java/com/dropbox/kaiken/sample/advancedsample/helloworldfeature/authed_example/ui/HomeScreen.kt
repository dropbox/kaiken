package com.dropbox.kaiken.sample.advancedsample.helloworldfeature.authed_example.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.dropbox.common.inject.AuthRequiredScreenScope
import com.dropbox.kaiken.sample.advancedsample.helloworldfeature.BasePresenter
import com.dropbox.kaiken.sample.advancedsample.helloworldfeature.Presenter
import com.dropbox.kaiken.sample.advancedsample.helloworldfeature.UserProfile
import com.dropbox.kaiken.sample.advancedsample.helloworldfeature.authed_example.repository.FavoriteWithFilm
import com.dropbox.kaiken.sample.advancedsample.helloworldfeature.authed_example.repository.FavoritesRepository
import com.dropbox.kaiken.sample.advancedsample.helloworldfeature.authed_example.repository.Film
import com.dropbox.kaiken.skeleton.scoping.SingleIn
import com.squareup.anvil.annotations.ContributesMultibinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

abstract class HomePresenter : Presenter<HomePresenter.HomeEvent, HomePresenter.HomeModel, HomePresenter.HomeEffect>(HomeModel(true)) {
    sealed interface HomeEvent
    data class DeleteFavorite(val id: String): HomeEvent
    object LoadFavorites: HomeEvent
    object DeleteAllFavorites: HomeEvent

    data class HomeModel(
        val loading: Boolean,
        val userName: String = "",
        val favorites: List<FavoriteWithFilm> = listOf(),
    )

    sealed interface HomeEffect
    data class ShowSnackbar(val message: String, val action: String): HomeEffect
}

@SingleIn(AuthRequiredScreenScope::class)
@ContributesMultibinding(
    AuthRequiredScreenScope::class,
    boundType = BasePresenter::class
)
class RealHomePresenter @Inject constructor(
    val profile: UserProfile,
    val favoritesRepository: FavoritesRepository,
) : HomePresenter() {
    override suspend fun eventHandler(event: HomeEvent) {
        when (event) {
            LoadFavorites -> loadFavorites()
            is DeleteFavorite -> deleteFavorite(event.id)
            is DeleteAllFavorites -> deleteAllFavorites()
        }
    }

    private fun loadFavorites() = CoroutineScope(Dispatchers.IO).launch {
        favoritesRepository.fetchFavorites().collect {
            model = model.copy(favorites = it, loading = false, userName = profile.name)
        }
    }

    private fun deleteFavorite(id: String) = CoroutineScope(Dispatchers.IO).launch {
        favoritesRepository.removeFavorite(id)
        CoroutineScope(Dispatchers.Main).launch {
            emitEffect(ShowSnackbar("Favorite deleted", "OK"))
        }
    }

    private fun deleteAllFavorites() = CoroutineScope(Dispatchers.IO).launch {
        favoritesRepository.clearAllFavorites()
        CoroutineScope(Dispatchers.Main).launch {
            emitEffect(ShowSnackbar("All favorites deleted", "OK"))
        }
    }
}

@Composable
fun HomeScreen(
    model: HomePresenter.HomeModel,
    handleEvent: (HomePresenter.HomeEvent) -> Boolean
) {
    HomeScreenInner(model, handleEvent)
}

@Composable
fun HomeScreenInner(
    model: HomePresenter.HomeModel,
    handleEvent: (HomePresenter.HomeEvent) -> Boolean
) {
    MaterialTheme {
        if (!model.loading) {
            Column(Modifier.padding(0.dp, 0.dp, 0.dp, 48.dp)) {
                Text("Welcome ${model.userName}")

                Row {
                    Button(
                        onClick = { handleEvent(HomePresenter.DeleteAllFavorites) }
                    ) {
                        Text("Clear Favorites")
                    }
                }

                if (!model.favorites.isEmpty()) {
                    YourFavorites(
                        model.favorites,
                        Modifier.weight(1f).testTag("Favorites"),
                        handleEvent,
                    )
                }
            }
        } else {
            CircularProgressIndicator(
                modifier = Modifier.testTag("Loading Spinner")
            )
            handleEvent(HomePresenter.LoadFavorites)
        }
    }
}

@Composable
fun YourFavorites(
    favorites: List<FavoriteWithFilm>,
    modifier: Modifier,
    handleEvent: (HomePresenter.HomeEvent) -> Boolean,
) {
    LazyColumn(modifier) {
        favorites.forEach {
            item { FavoriteFilmRow(it.film, handleEvent) }
        }
    }
}

@Composable
fun FavoriteFilmRow(
    film: Film,
    handleEvent: (HomePresenter.HomeEvent) -> Boolean,
) {
    val textModifier = Modifier.padding(12.dp, 0.dp)

    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(12.dp)
        .testTag("Favorite")
    ) {
        IconButton(
            onClick = { handleEvent(HomePresenter.DeleteFavorite(film.id)) },
        ) {
            Icon(Icons.Filled.Delete, "Remove Favorite")
        }
        Column {
            Text(modifier = textModifier, text = film.title)
            Row {
                Text(modifier = textModifier, text = film.releaseDate)
                Text(modifier = textModifier, text = film.description)

            }
        }

    }
}
