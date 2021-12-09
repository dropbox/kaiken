package test.com.dropbox.kaiken.testing

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.dropbox.kaiken.skeleton.scoping.AuthAwareBroadcastReceiver

abstract class TestAuthAwareBroadcastReceiver : BroadcastReceiver(), AuthAwareBroadcastReceiver {
    var resolvedDependencies: MyDependencies? = null

    override fun onReceive(context: Context, intent: Intent) {
        resolvedDependencies = super.onReceiveResolveDependencyProvider(context, intent)
    }
}
