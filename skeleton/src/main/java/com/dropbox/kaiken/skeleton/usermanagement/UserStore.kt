package com.dropbox.kaiken.skeleton.usermanagement

import com.dropbox.android.external.store4.Fetcher
import com.dropbox.android.external.store4.MemoryPolicy
import com.dropbox.android.external.store4.Store
import com.dropbox.android.external.store4.StoreBuilder
import com.dropbox.android.external.store4.StoreRequest
import com.dropbox.android.external.store4.StoreResponse
import com.dropbox.kaiken.skeleton.core.CoroutineScopes
import com.dropbox.kaiken.skeleton.core.SkeletonUser
import com.dropbox.kaiken.skeleton.scoping.AppScope
import com.dropbox.kaiken.skeleton.scoping.SingleIn
import com.squareup.anvil.annotations.ContributesTo
import dagger.Binds
import dagger.Module
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.scan
import javax.inject.Inject
import kotlin.time.ExperimentalTime

/**
 * Manages the set of authenticated users for an app.
 */
interface UserStore {
    /**
     * Provides a flow of the [UsersEvent].  Observe this for changes related to users added and removed.
     *
     */
    fun getUserEvents(): Flow<UsersEvent>

    /**
     * Provides an individual user based on the ID
     */
    suspend fun getUserById(userId: String): SkeletonUser?
}

class RealUserStore @Inject constructor(
    private val userSupplier: UserSupplier,
    scope: CoroutineScopes
) : UserStore {

    @OptIn(ExperimentalTime::class)
    private val store: Store<Unit, Set<SkeletonUser>> = StoreBuilder.from(
        Fetcher.ofFlow { _: Unit -> userSupplier.users() }
    )
        .cachePolicy(
            MemoryPolicy.builder<Unit, Set<SkeletonUser>>()
                .setMaxSize(1)
                .build()
        )
        .scope(scope.globalScope)
        .build()

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getUserEvents(): Flow<UsersEvent> =
        store.stream(StoreRequest.cached(key = Unit, refresh = true))
            .filter { it is StoreResponse.Data }
            .map { it.requireData() }
            .scan(UsersEvent(emptySet())) { prev, next ->
                val usersRemoved = prev.users.minusById(next)
                val usersAdded = next.minusById(prev.users)

                UsersEvent(next, usersAdded, usersRemoved)
            }
            .drop(1)

    override suspend fun getUserById(userId: String): SkeletonUser? {
        return getUserEvents().first().users.firstOrNull { it.userId == userId }
    }
}

internal fun Set<SkeletonUser>.minusById(elements: Set<SkeletonUser>): Set<SkeletonUser> {
    val result = toMutableSet()
    onEach { item ->
        if (elements.any { item.userId == it.userId }) {
            result.remove(item)
        }
    }
    return result
}

@ContributesTo(AppScope::class)
@Module
abstract class StoreBinding {
    @SingleIn(AppScope::class)
    @Binds
    abstract fun bindUserStore(real: RealUserStore): UserStore
}