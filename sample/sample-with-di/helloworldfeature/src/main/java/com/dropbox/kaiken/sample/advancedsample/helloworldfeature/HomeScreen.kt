package com.dropbox.kaiken.sample.advancedsample.helloworldfeature

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import com.dropbox.common.inject.AuthRequiredScreenScope
import com.dropbox.kaiken.skeleton.scoping.SingleIn
import com.squareup.anvil.annotations.ContributesMultibinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

abstract class HomePresenter : Presenter<HomePresenter.HomeEvent, HomePresenter.HomeModel, HomePresenter.HomeEffect>(HomeModel(true)) {
    sealed interface HomeEvent
    object LoadSomething : HomeEvent

    data class HomeModel(
        val loading: Boolean,
        val userId: String = "",
        val userList: List<String> = listOf()
    )

    sealed interface HomeEffect
}

@SingleIn(AuthRequiredScreenScope::class)
@ContributesMultibinding(
    AuthRequiredScreenScope::class,
    boundType = BasePresenter::class
)
class RealHomePresenter @Inject constructor(val userApi: UserApi, profile: UserProfile) : HomePresenter() {
    init {
        model = model.copy(loading = false, userId = profile.name)
    }

    override suspend fun eventHandler(event: HomeEvent) {
        when (event) {
            is LoadSomething -> {
                model = model.copy(loading = true)
                CoroutineScope(Dispatchers.IO).launch {
                    val list = userApi.getList()
                    model = model.copy(loading = false, userList = list)
                }
            }
        }
    }
}

@Composable
fun HomeScreen(
    model: HomePresenter.HomeModel,
    handleLoadSomething: () -> Boolean,
) {
    MaterialTheme {
        if (!model.loading) {
            Column() {
                Text("Welcome ${model.userId}")
                if (model.userList.isNotEmpty()) {
                    Text("List is here now.")
                }

                Button(
                    onClick = { handleLoadSomething() }
                ) {
                    Text("Load Something")
                }
            }
        } else {
            CircularProgressIndicator()
        }
    }
}
