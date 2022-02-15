package com.dropbox.kaiken.sample.advancedsample.helloworldfeature.authed_example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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
import kotlinx.coroutines.withContext
import javax.inject.Inject

abstract class FavoriteFilmsPresenter : Presenter<FavoriteFilmsPresenter.Event, FavoriteFilmsPresenter.Model, FavoriteFilmsPresenter.Effect>(Model()) {
    sealed interface Event
    data class AddFilm(val title: String) : Event
    data class AddFavorite(val id: String) : Event
    object LoadFilms : Event

    data class Model(
        val films: List<Film> = listOf(),
    )

    sealed interface Effect
    data class ShowSnackbar(val message: String, val action: String): Effect
}

@SingleIn(AuthRequiredScreenScope::class)
@ContributesMultibinding(
    AuthRequiredScreenScope::class,
    boundType = BasePresenter::class
)
class RealFavoriteFilmsPresenter @Inject constructor(
    val ghibliRepository: GhibliRepository,
    val favoritesRepository: FavoritesRepository
) : FavoriteFilmsPresenter() {
    override suspend fun eventHandler(event: Event) {
        when (event) {
            is AddFilm -> addFilm(event.title)
            is AddFavorite -> addFavorite(event.id)
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

    private suspend fun addFilm(title: String) = withContext(Dispatchers.IO) {
        ghibliRepository.addFilm(Film("123", title, "", ""))
        CoroutineScope(Dispatchers.Main).launch {
            emitEffect(ShowSnackbar("New film added!", "OK"))
        }
    }

    private suspend fun addFavorite(id: String) = withContext(Dispatchers.IO) {
        favoritesRepository.addFavorite(Favorite(id))
        CoroutineScope(Dispatchers.Main).launch {
            emitEffect(ShowSnackbar("Favorite added!", "OK"))
        }
    }
}

@ExperimentalAnimationApi
@Composable
fun FavoriteFilmsScreen(
    model: FavoriteFilmsPresenter.Model,
    handleEvent: (FavoriteFilmsPresenter.Event) -> Boolean,
) {
    MaterialTheme {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(12.dp)
        ) {
            Text(
                "What are your favorite Miyazaki films?",
                style = MaterialTheme.typography.h4,
            )

            FilmsPage(model.films, handleEvent)
        }
    }
}

@Composable
fun FilmsPage(
    films: List<Film>,
    handleEvent: (FavoriteFilmsPresenter.Event) -> Boolean,
) {
    if (films.isEmpty()) {
        CircularProgressIndicator()
        handleEvent(FavoriteFilmsPresenter.LoadFilms)
    } else {
        var newFilmTitle by remember { mutableStateOf("") }

        Column {
            LazyColumn(Modifier.weight(1f)) {
                films.forEach {
                    item { FilmRow(it, handleEvent) }
                }
            }

            Row(
                modifier = Modifier.padding(0.dp, 24.dp, 0.dp, 48.dp),
            ) {
                Text(text = "Add another film")
                TextField(value = newFilmTitle, onValueChange = { newFilmTitle = it })
                Button(
                    modifier = Modifier.padding(24.dp, 0.dp),
                    onClick = { handleEvent(FavoriteFilmsPresenter.AddFilm(newFilmTitle)) },
                ) {
                    Text("Add Film")
                }
            }
        }
    }
}

@Composable
fun FilmRow(
    film: Film,
    handleEvent: (FavoriteFilmsPresenter.Event) -> Boolean
) {
    val textModifier = Modifier.padding(12.dp, 0.dp)

    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(12.dp)) {
        IconButton(
            onClick = { handleEvent(FavoriteFilmsPresenter.AddFavorite(film.id)) },
        ) {
            Icon(Icons.Filled.Add, "Add Favorite")
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