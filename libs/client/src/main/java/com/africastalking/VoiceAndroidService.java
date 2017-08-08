package com.africastalking;

import android.app.*;
import android.content.Intent;
import android.os.IBinder;

public class VoiceAndroidService extends android.app.Service {
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
