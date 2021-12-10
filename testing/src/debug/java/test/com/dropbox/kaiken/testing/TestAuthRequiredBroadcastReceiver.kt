package test.com.dropbox.kaiken.testing

import com.dropbox.kaiken.scoping.AuthRequiredBroadcastReceiver

class TestAuthRequiredBroadcastReceiver :
    TestAuthAwareBroadcastReceiver(), AuthRequiredBroadcastReceiver
