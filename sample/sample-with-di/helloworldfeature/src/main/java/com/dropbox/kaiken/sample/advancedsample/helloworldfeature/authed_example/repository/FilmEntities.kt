package com.dropbox.kaiken.sample.advancedsample.helloworldfeature.authed_example.repository

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "films_table")
data class FilmDB(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "release_date") val releaseDate: String,
)

data class FilmRemote(
    val id: String,
    val title: String,
    val description: String,
    val release_date: String,
)

data class Film(
    val id: String,
    val title: String,
    val description: String,
    val releaseDate: String,
)

fun FilmDB.toFilm() = Film(
    id,
    title,
    description,
    releaseDate,
)

fun FilmRemote.toFilm() = Film(
    id,
    title,
    description,
    release_date,
)

fun FilmRemote.toFilmDB() = FilmDB(
    id,
    title,
    description,
    release_date,
)

fun Film.toFilmDB() = FilmDB(
    id,
    title,
    description,
    releaseDate,
)
