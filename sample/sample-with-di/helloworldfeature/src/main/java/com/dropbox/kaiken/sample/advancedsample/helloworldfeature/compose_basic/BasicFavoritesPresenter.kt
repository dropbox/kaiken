package com.dropbox.kaiken.sample.advancedsample.helloworldfeature.compose_basic

import com.dropbox.common.inject.AuthRequiredScreenScope
import com.dropbox.kaiken.sample.advancedsample.helloworldfeature.BasePresenter
import com.dropbox.kaiken.sample.advancedsample.helloworldfeature.Presenter
import com.dropbox.kaiken.sample.advancedsample.helloworldfeature.authed_example.repository.FavoriteWithFilm
import com.dropbox.kaiken.sample.advancedsample.helloworldfeature.authed_example.repository.FavoritesRepository
import com.dropbox.kaiken.skeleton.scoping.SingleIn
import com.squareup.anvil.annotations.ContributesMultibinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

abstract class BasicFavoritesPresenter :
    Presenter<BasicFavoritesPresenter.Event, BasicFavoritesPresenter.Model, BasicFavoritesPresenter.Effect>(Model()) {
    sealed interface Event
    object LoadFavorites : Event
    data class RemoveFavorite(val filmId: String) : Event
    object RemoveAllFavorites : Event

    data class Model(
        val loading: Boolean = true,
        val favorites: List<FavoriteWithFilm> = listOf(),
    )

    sealed interface Effect
    data class ShowSnackbar(val message: String) : Effect
}

@SingleIn(AuthRequiredScreenScope::class)
@ContributesMultibinding(
    AuthRequiredScreenScope::class,
    boundType = BasePresenter::class
)
class RealBasicFavoritesPresenter @Inject constructor(
    val favoritesRepository: FavoritesRepository
) : BasicFavoritesPresenter() {
    override suspend fun eventHandler(event: Event) {
        when(event) {
            LoadFavorites -> loadFavorites()
            is RemoveFavorite -> removeFavorite(event.filmId)
            RemoveAllFavorites -> removeAllFavorites()
        }
    }

    private fun loadFavorites() = CoroutineScope(Dispatchers.IO).launch {
        favoritesRepository.fetchFavorites().collect { favorites ->
            model = model.copy(loading = false, favorites = favorites)
        }
    }

    private suspend fun removeFavorite(filmId: String) = CoroutineScope(Dispatchers.IO).launch {
        favoritesRepository.removeFavorite(filmId)
        CoroutineScope(Dispatchers.Main).launch {
            emitEffect(ShowSnackbar("Favorite Removed!"))
        }
    }

    private suspend fun removeAllFavorites() = CoroutineScope(Dispatchers.IO).launch {
        favoritesRepository.clearAllFavorites()
        CoroutineScope(Dispatchers.Main).launch {
            emitEffect(ShowSnackbar("All Favorites Removed!"))
        }
    }
}