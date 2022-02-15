package com.dropbox.kaiken.sample.advancedsample.helloworldfeature.authed_example.repository

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoritesDao {
    @Transaction
    @Query("SELECT * FROM favorites_table " +
        "INNER JOIN films_table ON films_table.id = favorites_table.filmId " +
        "WHERE userId = :userId")
    fun getFavorites(userId: String): Flow<List<FavoriteWithFilmDB>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(favorite: FavoriteDB)

    @Query("DELETE FROM favorites_table WHERE userId = :userId AND filmId = :filmId")
    suspend fun deleteFavorite(userId: String, filmId: String)

    @Query("DELETE FROM favorites_table WHERE userId = :userId")
    suspend fun deleteAllFavorites(userId: String)
}
