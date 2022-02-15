package com.dropbox.kaiken.sample.advancedsample.helloworldfeature.authed_example.repository

import retrofit2.http.GET
import retrofit2.http.Query

interface GhibliApi {
    @GET("/films")
    suspend fun getFilms(
        @Query("fields") fields: String,
        @Query("limit") limit: Int
    ): List<FilmRemote>
}
