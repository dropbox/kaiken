package com.dropbox.kaiken.sample.advancedsample.helloworldfeature.authed_example.repository

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = arrayOf(FilmDB::class, FavoriteDB::class), version = 1, exportSchema = false)
abstract class AppRoomDatabase : RoomDatabase() {
    abstract fun ghibliDao(): GhibliDao
    abstract fun favoritesDao(): FavoritesDao
}
