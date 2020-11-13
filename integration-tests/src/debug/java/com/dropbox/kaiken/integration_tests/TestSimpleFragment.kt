package com.dropbox.kaiken.integration_tests

import androidx.fragment.app.Fragment
import com.dropbox.kaiken.annotation.Injectable
import javax.inject.Inject

@Injectable
class TestSimpleFragment : Fragment() {
    @Inject
    var message: String? = null

    fun testInject() {
        inject()
    }
}
