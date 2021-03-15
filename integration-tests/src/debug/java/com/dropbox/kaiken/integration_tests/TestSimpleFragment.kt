package com.dropbox.kaiken.integration_tests

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.dropbox.kaiken.annotation.Injectable
import javax.inject.Inject

@Injectable
class TestSimpleFragment : Fragment() {
    @Inject
    lateinit var message: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        message = ""
    }

    fun testInject() {
        inject()
    }
}
