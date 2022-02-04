package com.dropbox.kaiken.sample.advancedsample.helloworldfeature.authed_example.repository

import com.dropbox.android.external.store4.StoreRequest
import com.dropbox.android.external.store4.StoreResponse
import com.dropbox.common.inject.UserScope
import com.dropbox.kaiken.skeleton.scoping.SingleIn
import com.squareup.anvil.annotations.ContributesBinding
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import javax.inject.Inject

interface GhibliRepository {
    suspend fun fetchFilms(): Flow<List<Film>>
    suspend fun addFilm(film: Film)
}

@ContributesBinding(UserScope::class)
@SingleIn(UserScope::class)
class RealGhibliRepository @Inject constructor(
    val store: FilmStore,
    val dao: GhibliDao
): GhibliRepository {
    override suspend fun fetchFilms(): Flow<List<Film>> {
        return store.innerStore.stream(StoreRequest.cached("", refresh = true))
            .distinctUntilChanged()
            .filter { it is StoreResponse.Data<*> }
            .map {
                it.requireData().map {
                    it.toFilm()
                }
            }
    }

    override suspend fun addFilm(film: Film) {
        dao.insertAll(listOf(film.toFilmDB()))
    }
}
