package com.dropbox.kaiken.integration_tests

import androidx.fragment.app.Fragment
import com.dropbox.kaiken.annotation.Injectable
import com.dropbox.kaiken.scoping.AuthAwareFragment
import javax.inject.Inject

@Injectable
class TestAuthAwareFragment : Fragment(), AuthAwareFragment {
    @Inject
    var message: String? = null

    fun testInject() {
        inject()
    }
}
