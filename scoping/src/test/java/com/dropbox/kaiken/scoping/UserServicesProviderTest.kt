package com.dropbox.kaiken.scoping

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import kotlin.test.fail

class UserServicesProviderTest {
    @Test
    fun `GIVEN valid user WHEN provideUserServicesOf(vs) THEN returns user services`() {
        // GIVEN
        val testObject: UserServicesProvider = FakeUserServicesProvider()
        val viewingUserSelector = ViewingUserSelector.fromUserId("42")

        // WHEN
        val userServices = testObject.provideUserServicesOf(viewingUserSelector)

        // THEN
        assertThat(userServices).isNotNull()
    }

    @Test
    fun `GIVEN invalid user WHEN provideUserServicesOf(vs) THEN returns null`() {
        // GIVEN
        val testObject: UserServicesProvider = FakeUserServicesProvider()
        val viewingUserSelector = ViewingUserSelector.fromUserId("123")

        // WHEN
        val userServices = testObject.provideUserServicesOf(viewingUserSelector)

        // THEN
        assertThat(userServices).isNull()
    }
}

private class FakeUserServicesProvider : UserServicesProvider {
    override fun provideUserServicesOf(userId: String): UserServices? {
        return if (userId == "42") {
            FakeUserServices()
        } else {
            null
        }
    }
}

private class FakeUserServices : UserServices {
    override fun getTeardownHelper(): TeardownHelper {
        fail("Unreachable")
    }
}
