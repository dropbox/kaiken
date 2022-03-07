package com.dropbox.kaiken.sample.advancedsample.helloworldfeature.compose_basic

import androidx.compose.foundation.clickable
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.dropbox.kaiken.sample.advancedsample.helloworldfeature.authed_example.repository.FavoriteWithFilm
import com.dropbox.kaiken.sample.advancedsample.helloworldfeature.authed_example.repository.Film

@Composable
fun BasicFavoritesScreen(
    model: BasicFavoritesPresenter.Model,
    handleEvent: (BasicFavoritesPresenter.Event) -> Unit,
) {
    MaterialTheme {
        if (!model.loading) {
            Column(Modifier.padding(0.dp, 0.dp, 0.dp, 48.dp)) {
                Row {
                    Button(
                        onClick = { handleEvent(BasicFavoritesPresenter.RemoveAllFavorites) }
                    ) {
                        Text("Remove All Favorites")
                    }
                }

                if (!model.favorites.isEmpty()) {
                    YourFavorites(
                        model.favorites,
                        Modifier.weight(1f),
                        handleEvent,
                    )
                } else {
                    Text(
                        "You have no favorites!",
                        style = MaterialTheme.typography.h4,
                    )
                }
            }
        } else {
            CircularProgressIndicator(
                modifier = Modifier.testTag("Loading Spinner")
            )
            handleEvent(BasicFavoritesPresenter.LoadFavorites)
        }
    }
}

@Composable
fun YourFavorites(
    favorites: List<FavoriteWithFilm>,
    modifier: Modifier,
    handleEvent: (BasicFavoritesPresenter.Event) -> Unit,
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
    handleEvent: (BasicFavoritesPresenter.Event) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    val textModifier = Modifier.padding(12.dp, 0.dp)

    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(12.dp)
        .testTag("Favorite")
    ) {
        IconButton(
            onClick = { handleEvent(BasicFavoritesPresenter.RemoveFavorite(film.id)) },
        ) {
            Icon(Icons.Filled.Delete, "Remove Favorite")
        }
        Column {
            Text(
                modifier = textModifier.clickable(
                    onClick = { expanded = !expanded }
                ),
                style = MaterialTheme.typography.h5,
                text = "${film.releaseDate} - ${film.title}")
            if(expanded) {
                Text(
                    modifier = textModifier,
                    style = MaterialTheme.typography.body1,
                    text = film.description
                )
            }
        }
    }
}
