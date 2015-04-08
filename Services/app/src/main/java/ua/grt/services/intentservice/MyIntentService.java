package ua.grt.services.intentservice;

import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

public class MyIntentService extends IntentService {

    private static final int STEP = 20;

    public static final String RESULT = "result";
    public static final String PROGRESS = "progress";
    public static final String NOTIFICATION = "ua.grt.services.intentservice";
    public static final String DELAY = "delay";

    public MyIntentService(){
        super("DownloadService");
    }

    @Override
    protected void onHandleIntent(Intent intent){
        long delay = intent.getExtras().getInt(DELAY,5000);
        long startTime = System.currentTimeMillis();
        long endTime = startTime + delay;
        while (System.currentTimeMillis() < endTime) {
            synchronized (this) {
                try {
                    wait(STEP);
                } catch (Exception e) {
                }
            }
            int progress = (int) (((System.currentTimeMillis()-startTime)/(float)delay)*100);
            publishProgress(progress);
        }
        publishResults();
    }

    private void publishProgress(int progress) {
        Intent intent = new Intent(NOTIFICATION);
        intent.putExtra(PROGRESS, progress);
        sendBroadcast(intent);
    }

    private void publishResults(){
        int result = Activity.RESULT_OK;
        Intent intent = new Intent(NOTIFICATION);
        intent.putExtra(RESULT, result);
        sendBroadcast(intent);
    }
}
