package test.com.dropbox.kaiken.testing

import android.content.Context
import androidx.fragment.app.Fragment
import com.dropbox.kaiken.scoping.AuthAwareFragment

class TestAuthAwareFragment : Fragment(), AuthAwareFragment {

    var resolvedDependencies: MyDependencies? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (finishIfInvalidAuth()) {
            return
        }

        resolvedDependencies = resolveDependencyProvider<MyDependencies>()
    }
}
