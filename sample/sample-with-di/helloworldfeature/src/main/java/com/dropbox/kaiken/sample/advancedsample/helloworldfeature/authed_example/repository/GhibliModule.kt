package com.dropbox.kaiken.sample.advancedsample.helloworldfeature.authed_example.repository

import android.app.Application
import androidx.room.Room
import com.dropbox.android.external.store4.Fetcher
import com.dropbox.android.external.store4.SourceOfTruth
import com.dropbox.android.external.store4.Store
import com.dropbox.android.external.store4.StoreBuilder
import com.dropbox.common.inject.AppScope
import com.dropbox.kaiken.skeleton.scoping.SingleIn
import com.squareup.anvil.annotations.ContributesBinding
import com.squareup.anvil.annotations.ContributesTo
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Inject

@Module
@ContributesTo(AppScope::class)
class GhibliModule {
    @Provides
    @SingleIn(AppScope::class)
    fun provideAppDatabase(
        application: Application
    ): AppRoomDatabase =
        Room.databaseBuilder(
            application,
            AppRoomDatabase::class.java,
            "app_database"
        ).build()

    @Provides
    @SingleIn(AppScope::class)
    fun provideGhibliDao(
        db: AppRoomDatabase
    ): GhibliDao = db.ghibliDao()

    @Provides
    @SingleIn(AppScope::class)
    fun provideGhibliApi(): GhibliApi {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://ghibliapi.herokuapp.com")
            .addConverterFactory(MoshiConverterFactory.create())
            .build()

        return retrofit.create(GhibliApi::class.java)
    }
}

interface FilmStore {
    val innerStore:Store<String, List<FilmDB>>
}
@ContributesBinding(AppScope::class)
class RealFilmStore @Inject constructor(val db: AppRoomDatabase,
                                       val api: GhibliApi) : FilmStore{
    override val innerStore: Store<String, List<FilmDB>> = StoreBuilder
        .from(
            Fetcher.of {
                api.getFilms("id,title,description,release_date", 50)
            },
            sourceOfTruth = SourceOfTruth.of(
                reader = { db.ghibliDao().getFilms() },
                writer = { _, values: List<FilmRemote> ->
                    val dbValues = values.map {
                        it.toFilmDB()
                    }
                    db.ghibliDao().insertAll(dbValues)
                },
                delete = { key: String -> db.ghibliDao().deleteFilm(key) },
                deleteAll = { db.ghibliDao().deleteAllFilms() }
            )
        ).build()

}
