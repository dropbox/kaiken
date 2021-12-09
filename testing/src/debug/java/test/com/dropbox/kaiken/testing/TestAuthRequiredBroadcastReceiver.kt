package test.com.dropbox.kaiken.testing

import com.dropbox.kaiken.skeleton.scoping.AuthRequiredBroadcastReceiver

class TestAuthRequiredBroadcastReceiver :
    TestAuthAwareBroadcastReceiver(), AuthRequiredBroadcastReceiver
