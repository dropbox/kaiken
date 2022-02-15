package com.dropbox.kaiken.sample.advancedsample.helloworldfeature.authed_example.repository

import com.dropbox.common.inject.UserScope
import com.dropbox.kaiken.sample.advancedsample.helloworldfeature.UserProfile
import com.dropbox.kaiken.skeleton.scoping.SingleIn
import com.squareup.anvil.annotations.ContributesBinding
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

interface FavoritesRepository {
    suspend fun fetchFavorites(): Flow<List<FavoriteWithFilm>>
    suspend fun addFavorite(favorite: Favorite)
    suspend fun removeFavorite(filmId: String)
    suspend fun clearAllFavorites()
}

@ContributesBinding(UserScope::class)
@SingleIn(UserScope::class)
class RealFavoritesRepository @Inject constructor(
    val dao: FavoritesDao,
    val user: UserProfile,
): FavoritesRepository {
    override suspend fun fetchFavorites(): Flow<List<FavoriteWithFilm>> {
        return dao.getFavorites(user.userId)
            .map {
                it.map { entity ->
                    entity.toFavoriteWithFilm()
                }
            }
    }

    override suspend fun addFavorite(favorite: Favorite) {
        dao.insert(FavoriteDB(user.userId, favorite.filmId))
    }

    override suspend fun removeFavorite(filmId: String) {
        dao.deleteFavorite(user.userId, filmId)
    }

    override suspend fun clearAllFavorites() {
        dao.deleteAllFavorites(user.userId)
    }
}
