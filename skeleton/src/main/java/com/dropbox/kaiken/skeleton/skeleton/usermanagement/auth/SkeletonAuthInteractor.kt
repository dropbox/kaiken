package com.dropbox.kaiken.skeleton.skeleton.usermanagement.auth


import com.dropbox.android.external.store4.Fetcher
import com.dropbox.android.external.store4.Store
import com.dropbox.android.external.store4.StoreBuilder
import com.dropbox.android.external.store4.StoreRequest
import com.dropbox.android.external.store4.StoreResponse
import com.dropbox.kaiken.scoping.AppScope
import com.squareup.anvil.annotations.ContributesBinding
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Bridges the app's account persistence layer and Skeleton's [UserManager].
 *  We provide a naive implementation of a [SkeletonAuthInteractor]
 * AppSkeleton consumers can leverage [ContributesBinding] (AppScope::class, replaces = SkeletonAuthInteractor)
 * when supplying their own implementation
 * See [RealSkeletonAuthInteractor]
 */
interface SkeletonAuthInteractor {
    /**
     * This should be called and observed for the life of the application as it will feed changes from the Account Maker into the User Manager.
     */
    fun observeAlLUsers(): Flow<Set<UserInput>>
}


@FlowPreview
@ExperimentalCoroutinesApi
@ContributesBinding(AppScope::class)
class RealSkeletonAuthInteractor @Inject constructor(
        val newUser: @JvmSuppressWildcards MutableSharedFlow<String>,
) : SkeletonAuthInteractor {

    private val users = mutableSetOf<UserInput>()

    private val authStore: Store<Any, MutableSet<UserInput>> = StoreBuilder.from(Fetcher.ofFlow {
        newUser.asSharedFlow().map {value->
            users.add(UserInput(value, value))
            users
        }
    }).build()

    override fun observeAlLUsers(): Flow<Set<UserInput>> {
        return authStore
                .stream(StoreRequest.cached("", refresh = false))
                .filter { storeResponse -> storeResponse is StoreResponse.Data }
                .map { it.requireData() }
    }
}

