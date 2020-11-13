package com.dropbox.kaiken.scoping.internal

import androidx.lifecycle.ViewModelStore
import com.dropbox.kaiken.scoping.AuthRequiredActivity
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class AuthRequiredActivityTest {
    @Test
    fun `GIVEN AuthRequiredActivityTest WHEN authRequired THEN true`() {
        val authRequiredActivityTest: AuthRequiredActivity = object : AuthRequiredActivity {
            override fun getViewModelStore(): ViewModelStore {
                return ViewModelStore()
            }
        }
        assertThat(authRequiredActivityTest.authRequired).isTrue()
    }
}
