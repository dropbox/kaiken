package com.dropbox.kaiken.sample.advancedsample.helloworldfeature.authed_example.repository

import androidx.room.Embedded
import androidx.room.Entity

@Entity(
    tableName = "favorites_table",
    primaryKeys = arrayOf("userId", "filmId")
)
data class FavoriteDB(
    val userId: String,
    val filmId: String,
)

@Entity
data class FavoriteWithFilmDB(
    @Embedded val film: FilmDB
)

data class FavoriteWithFilm(
    val film: Film
)

data class Favorite(
    val filmId: String
)

fun FavoriteWithFilmDB.toFavoriteWithFilm() = FavoriteWithFilm(film.toFilm())
