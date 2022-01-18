package com.dropbox.kaiken.skeleton.usermanagement

import app.cash.turbine.test
import com.dropbox.kaiken.skeleton.core.CoroutineScopes
import com.dropbox.kaiken.skeleton.core.SkeletonOauth2
import com.dropbox.kaiken.skeleton.core.SkeletonUser
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test

@ExperimentalCoroutinesApi
@Suppress("DEPRECATION")
class RealUserStoreTest {

    private val users = MutableStateFlow(emptySet<SkeletonUser>())
    private var userMapper = DummyUserSupplier(users)
    private fun createUserStore(scope: CoroutineScope): UserStore = RealUserStore(
        userMapper,
        object :
            CoroutineScopes {
            override val mainScope: CoroutineScope
                get() = TODO("Not yet implemented")
            override val globalScope: CoroutineScope
                get() = scope
        }
    )

    @Test
    fun `GIVEN user store WHEN get initial set THEN empty set returned`() = runBlockingTest {
        // GIVEN
        val userStore = createUserStore(this)

        // WHEN
        val events = userStore.getUserEvents()

        // THEN
        events.test {
            assertThat(expectMostRecentItem()).isEqualTo(UsersEvent(emptySet()))
            expectNoEvents()
        }
    }

    @Test
    @SuppressWarnings
    fun `GIVEN user store WHEN get user events and one user added THEN emit one user`() = runBlockingTest {
        // GIVEN
        val userStore = createUserStore(this)

        // WHEN
        val events = userStore.getUserEvents()
        users.emit(setOf(FAKE_USER_ONE))

        // THEN
        events.test {

            assertThat(expectMostRecentItem()).isEqualTo(
                UsersEvent(
                    setOf(FAKE_USER_ONE),
                    setOf(
                        FAKE_USER_ONE
                    ),
                    emptySet()
                )
            )
            expectNoEvents()
        }
    }

    @Test
    fun `GIVEN user store WHEN get user events and emit and remove user THEN emit one user`() = runBlockingTest {
        // GIVEN
        val userStore = createUserStore(this)

        // WHEN
        val events = userStore.getUserEvents()

        users.emit(setOf(FAKE_USER_ONE))
        users.emit(setOf(FAKE_USER_TWO))

        // THEN
        events.test {
            assertThat(expectMostRecentItem()).isEqualTo(
                UsersEvent(
                    setOf(FAKE_USER_TWO),
                    setOf(
                        FAKE_USER_TWO
                    ),
                    emptySet()
                )
            )
            expectNoEvents()
        }
    }

    @Test
    fun `GIVEN user store WHEN get user events and add and remove user THEN emit two user events`() = runBlockingTest {
        // GIVEN
        val userStore = createUserStore(this)

        // WHEN
        val events = userStore.getUserEvents()

        // THEN
        events.test {
            users.emit(setOf(FAKE_USER_ONE))
            assertThat(expectMostRecentItem()).isEqualTo(
                UsersEvent(
                    setOf(FAKE_USER_ONE),
                    setOf(FAKE_USER_ONE),
                    emptySet()
                )
            )
            users.emit(setOf(FAKE_USER_TWO))
            assertThat(expectMostRecentItem()).isEqualTo(
                UsersEvent(
                    setOf(FAKE_USER_TWO),
                    setOf(FAKE_USER_TWO),
                    setOf(FAKE_USER_ONE)
                )
            )
            expectNoEvents()
        }
    }

    companion object {
        const val FAKE_USER_ID_ONE = "fakeUserIdOne"
        const val FAKE_USER_ID_TWO = "fakeUserIdTwo"

        private val FAKE_OAUTH_TOKEN = SkeletonOauth2("fakeToken")

        val FAKE_USER_ONE = SkeletonUser(FAKE_USER_ID_ONE, FAKE_OAUTH_TOKEN)
        val FAKE_USER_TWO = SkeletonUser(FAKE_USER_ID_TWO, FAKE_OAUTH_TOKEN)
    }
}

open class DummyUserSupplier constructor(private val users: Flow<Set<SkeletonUser>>) : UserSupplier {
    override fun users(): Flow<Set<SkeletonUser>> = users
}
