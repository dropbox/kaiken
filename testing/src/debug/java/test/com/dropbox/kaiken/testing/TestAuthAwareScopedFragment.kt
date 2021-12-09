package test.com.dropbox.kaiken.testing

import android.content.Context
import androidx.fragment.app.Fragment
import com.dropbox.kaiken.skeleton.scoping.AuthAwareScopeOwnerFragment

abstract class TestAuthAwareScopedFragment : Fragment(), AuthAwareScopeOwnerFragment {

    var resolvedDependencies: MyDependencies? = null
    var onAttachExceptionThrown: Throwable? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)

        try {
            if (callFinishIfInvalidAuth && finishIfInvalidAuth()) {
                return
            }

            resolvedDependencies = resolveDependencyProvider<MyDependencies>()
        } catch (t: Throwable) {
            onAttachExceptionThrown = t
        }
    }

    companion object {
        var callFinishIfInvalidAuth = true
    }
}
