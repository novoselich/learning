package ua.novoselich.services.boundservice;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

public class MyBoundService extends Service {

    static final String NOTIFICATION = "ua.novoselich.services.boundservice";
    static final String WORKING = "working";
    static final String VALUE = "value";

    class MyBinder extends Binder {
        public void setValue(int value) {
            Intent intent = new Intent(NOTIFICATION);
            intent.putExtra(VALUE, value);
            sendBroadcast(intent);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        publish(true);
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new MyBinder();
    }

    @Override
    public void onDestroy() {
        publish(false);
        super.onDestroy();
    }

    private void publish(boolean working) {
        Intent intent = new Intent(NOTIFICATION);
        intent.putExtra(WORKING, working);
        sendBroadcast(intent);
    }
}
