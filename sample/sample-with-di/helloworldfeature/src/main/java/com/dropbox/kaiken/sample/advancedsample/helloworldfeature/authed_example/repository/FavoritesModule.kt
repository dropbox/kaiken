package com.dropbox.kaiken.sample.advancedsample.helloworldfeature.authed_example.repository

import com.dropbox.common.inject.UserScope
import com.dropbox.kaiken.skeleton.scoping.SingleIn
import com.squareup.anvil.annotations.ContributesTo
import dagger.Module
import dagger.Provides

@Module
@ContributesTo(UserScope::class)
class FavoritesModule {
    @Provides
    @SingleIn(UserScope::class)
    fun provideFavoritesDao(
        db: AppRoomDatabase
    ): FavoritesDao = db.favoritesDao()
}
