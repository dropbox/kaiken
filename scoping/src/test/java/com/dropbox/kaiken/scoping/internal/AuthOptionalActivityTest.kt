package com.dropbox.kaiken.scoping.internal

import androidx.lifecycle.ViewModelStore
import com.dropbox.kaiken.scoping.AuthOptionalActivity
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class AuthOptionalActivityTest {
    @Test
    fun `GIVEN AuthOptionalActivity WHEN authRequired THEN false`() {
        val authOptionalActivity: AuthOptionalActivity = object : AuthOptionalActivity {
            override fun getViewModelStore(): ViewModelStore {
                return ViewModelStore()
            }
        }
        assertThat(authOptionalActivity.authRequired).isFalse()
    }
}
