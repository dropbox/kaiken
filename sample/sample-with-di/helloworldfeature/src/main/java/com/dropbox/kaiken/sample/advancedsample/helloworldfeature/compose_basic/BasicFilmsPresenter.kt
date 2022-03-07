package com.dropbox.kaiken.sample.advancedsample.helloworldfeature.compose_basic

import com.dropbox.common.inject.AuthRequiredScreenScope
import com.dropbox.kaiken.sample.advancedsample.helloworldfeature.BasePresenter
import com.dropbox.kaiken.sample.advancedsample.helloworldfeature.Presenter
import com.dropbox.kaiken.sample.advancedsample.helloworldfeature.authed_example.repository.Favorite
import com.dropbox.kaiken.sample.advancedsample.helloworldfeature.authed_example.repository.FavoritesRepository
import com.dropbox.kaiken.sample.advancedsample.helloworldfeature.authed_example.repository.Film
import com.dropbox.kaiken.sample.advancedsample.helloworldfeature.authed_example.repository.GhibliRepository
import com.dropbox.kaiken.skeleton.scoping.SingleIn
import com.squareup.anvil.annotations.ContributesMultibinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

abstract class BasicFilmsPresenter : Presenter<BasicFilmsPresenter.Event, BasicFilmsPresenter.Model, BasicFilmsPresenter.Effect>(Model()) {
    sealed interface Event
    object LoadFilms : Event
    data class AddFavorite(val filmId: String) : Event

    data class Model(
        val films: List<Film> = listOf()
    )

    sealed interface Effect
    data class ShowSnackbar(val message: String) : Effect
}

@SingleIn(AuthRequiredScreenScope::class)
@ContributesMultibinding(
    AuthRequiredScreenScope::class,
    boundType = BasePresenter::class
)
class RealBasicFilmsPresenter @Inject constructor(
    val ghibliRepository: GhibliRepository,
    val favoritesRepository: FavoritesRepository
) : BasicFilmsPresenter() {
    override suspend fun eventHandler(event: Event) {
        when(event) {
            LoadFilms -> loadFilms()
            is AddFavorite -> addFavorite(event.filmId)
        }
    }

    private fun loadFilms() = CoroutineScope(Dispatchers.IO).launch {
            ghibliRepository.fetchFilms().collect {
                model = model.copy(films = it)
            }
        }

    private suspend fun addFavorite(filmId: String) = CoroutineScope(Dispatchers.IO).launch {
        favoritesRepository.addFavorite(Favorite(filmId))
        CoroutineScope(Dispatchers.Main).launch {
            emitEffect(ShowSnackbar("Favorite Added!"))
        }
    }
}