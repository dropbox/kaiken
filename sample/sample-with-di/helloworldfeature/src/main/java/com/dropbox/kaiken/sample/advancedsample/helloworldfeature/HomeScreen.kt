package com.dropbox.kaiken.sample.advancedsample.helloworldfeature

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import com.dropbox.kaiken.skeleton.scoping.AuthOptionalScreenScope
import com.dropbox.kaiken.skeleton.scoping.AuthRequiredScreenScope
import com.dropbox.kaiken.skeleton.scoping.SingleIn
import com.squareup.anvil.annotations.ContributesMultibinding
import kotlinx.coroutines.flow.collect
import javax.inject.Inject

abstract class HomePresenter : Presenter<HomePresenter.HomeEvent, HomePresenter.HomeModel>(Loading) {
    sealed interface HomeEvent
    object Loaded: HomeEvent

    sealed interface HomeModel
    object Loading: HomeModel
    data class UserLoaded(val userId: String, val count: Int): HomeModel
}

@SingleIn(AuthRequiredScreenScope::class)
@ContributesMultibinding(
        AuthRequiredScreenScope::class,
        boundType = BasePresenter::class
)
class RealHomePresenter @Inject constructor() : HomePresenter() {
    override suspend fun eventHandler(event: HomeEvent) {
        when (event) {
            is Loaded -> { }
        }
    }
}

@Composable
fun HomeScreen(
        model: HomePresenter.HomeModel
) {
    MaterialTheme {
        Text(model.toString())
    }
}