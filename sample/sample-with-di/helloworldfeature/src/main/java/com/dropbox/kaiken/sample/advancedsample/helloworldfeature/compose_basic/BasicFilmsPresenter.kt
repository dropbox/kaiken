package com.dropbox.kaiken.sample.advancedsample.helloworldfeature.compose_basic

import com.dropbox.common.inject.AuthRequiredScreenScope
import com.dropbox.kaiken.sample.advancedsample.helloworldfeature.BasePresenter
import com.dropbox.kaiken.sample.advancedsample.helloworldfeature.Presenter
import com.dropbox.kaiken.sample.advancedsample.helloworldfeature.authed_example.repository.Film
import com.dropbox.kaiken.sample.advancedsample.helloworldfeature.authed_example.repository.GhibliRepository
import com.dropbox.kaiken.sample.advancedsample.helloworldfeature.authed_example.ui.FavoriteFilmsPresenter
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

    data class Model(
        val films: List<Film> = listOf()
    )

    sealed interface Effect
}

@SingleIn(AuthRequiredScreenScope::class)
@ContributesMultibinding(
    AuthRequiredScreenScope::class,
    boundType = BasePresenter::class
)
class RealBasicFilmsPresenter @Inject constructor(val ghibliRepository: GhibliRepository) :
    BasicFilmsPresenter() {
    override suspend fun eventHandler(event: Event) {
        when(event) {
            LoadFilms -> loadFilms()
        }
    }

    private fun loadFilms() {
        CoroutineScope(Dispatchers.IO).launch {
            ghibliRepository.fetchFilms().collect {
                model = model.copy(films = it)
            }
        }
    }
}