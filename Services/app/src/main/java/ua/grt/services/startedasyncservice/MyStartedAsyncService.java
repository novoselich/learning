package ua.grt.services.startedasyncservice;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MyStartedAsyncService extends Service {

    public static final int STEP = 20;

    public static final String RESULT = "result";
    public static final String PROGRESS = "progress";
    public static final String PID = "pid";
    public static final String NOTIFICATION = "ua.grt.services.startedasyncservice";
    public static final String DELAY = "delay";

    private int mResult;
    private volatile int mInProgress;
    private ExecutorService mExecutor;

    private class ServiceThread implements Runnable {

        private int pid;
        private int delay;

        public ServiceThread(int pid, int delay){
            this.pid = pid;
            this.delay = delay;
        }

        @Override
        public void run() {
            mInProgress++;
            mResult = Activity.RESULT_CANCELED;
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
                publishProgress(pid, progress);
            }

            mResult = Activity.RESULT_OK;
            mInProgress--;
            stopSelf(pid);

        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        mInProgress = 0;
        mExecutor = Executors.newCachedThreadPool();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Runnable worker =  new ServiceThread(startId, intent.getExtras().getInt(DELAY,5000));
        mExecutor.execute(worker);


        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(mInProgress!=0 ){
                }
                publishResults();
            }
        }).start();
    }

    private void publishProgress(int pid, int progress) {
        Intent intent = new Intent(NOTIFICATION);
        intent.putExtra(PID, pid);
        intent.putExtra(PROGRESS, progress);
        sendBroadcast(intent);
    }

    private void publishResults(){
        Intent intent = new Intent(NOTIFICATION);
        intent.putExtra(RESULT, mResult);
        sendBroadcast(intent);
    }
}
