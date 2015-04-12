package ua.grt.services.foregroundservice;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class MyForegroundService extends Service{

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
