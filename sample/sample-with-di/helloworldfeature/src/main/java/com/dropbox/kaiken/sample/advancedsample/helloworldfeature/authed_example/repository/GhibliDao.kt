package com.dropbox.kaiken.sample.advancedsample.helloworldfeature.authed_example.repository

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface GhibliDao {
    @Query("SELECT * FROM films_table")
    fun getFilms(): Flow<List<FilmDB>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(films: List<FilmDB>)

    @Query("DELETE FROM films_table WHERE id = :id")
    suspend fun deleteFilm(id: String)

    @Query("DELETE FROM films_table")
    suspend fun deleteAllFilms()
}
