package com.dropbox.kaiken.sample.advancedsample.helloworldfeature.compose_basic

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dropbox.kaiken.sample.advancedsample.helloworldfeature.authed_example.repository.Film

@Composable
fun BasicFilmsScreen(
    model: BasicFilmsPresenter.Model,
    handleEvent: (BasicFilmsPresenter.Event) -> Unit
) {
    if (model.films.isEmpty()) {
        CircularProgressIndicator()
        handleEvent(BasicFilmsPresenter.LoadFilms)
    } else {
        FilmList(model.films, handleEvent)
    }
}

@Composable
fun FilmList(
    films: List<Film>,
    handleEvent: (BasicFilmsPresenter.Event) -> Unit
) {
    LazyColumn {
        films.forEach {
            item { FilmRow(it, handleEvent) }
        }
    }
}

@Composable
fun FilmRow(
    film: Film,
    handleEvent: (BasicFilmsPresenter.Event) -> Unit
) {
    Column(Modifier
        .fillMaxHeight()
        .fillMaxWidth()
        .padding(12.dp)) {
        Row {
            IconButton(onClick = { handleEvent(BasicFilmsPresenter.AddFavorite(film.id)) }) {
                Icon(Icons.Filled.Add, "Add Favorite")
            }

            Text(
                text = "${film.releaseDate} - ${film.title}",
                style = MaterialTheme.typography.h5
            )
        }

        Text(
            text = film.description,
            style = MaterialTheme.typography.body1,
        )
    }
}