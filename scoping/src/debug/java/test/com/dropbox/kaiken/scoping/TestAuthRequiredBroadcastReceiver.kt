package test.com.dropbox.kaiken.scoping

import com.dropbox.kaiken.scoping.AuthRequiredBroadcastReceiver

class TestAuthRequiredBroadcastReceiver :
    TestAuthAwareBroadcastReceiver(), AuthRequiredBroadcastReceiver
