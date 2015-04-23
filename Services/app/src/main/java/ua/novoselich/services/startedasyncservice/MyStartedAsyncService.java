package ua.novoselich.services.startedasyncservice;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MyStartedAsyncService extends Service {

    private static final int STEP = 20;

    static final String RESULT = "result";
    static final String PROGRESS = "progress";
    static final String PID = "pid";
    static final String NOTIFICATION = "ua.novoselich.services.startedasyncservice";
    static final String DELAY = "delay";
    static final String FINISHED = "finished";

    private int mResult;
    private volatile int mInProgress;
    private ExecutorService mExecutor;
    private boolean quit;

    private class ServiceThread implements Runnable {

        private int pid;
        private int delay;

        public ServiceThread(int pid, int delay) {
            this.pid = pid;
            this.delay = delay;
        }

        @Override
        public void run() {
            mInProgress++;
            long startTime = System.currentTimeMillis();
            long endTime = startTime + delay;
            while (System.currentTimeMillis() < endTime) {
                synchronized (this) {
                    if (quit) {
                        return;
                    }
                    try {
                        wait(STEP);
                    } catch (Exception e) {
                    }
                    int progress = (int) (((System.currentTimeMillis() - startTime) / (float) delay) * 100);
                    publishProgress(pid, progress);
                }
            }
            synchronized (MyStartedAsyncService.this) {
                mInProgress--;
                if (mInProgress == 0) {
                    mResult = Activity.RESULT_OK;
                    stopSelf();
                }
            }

        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        mResult = Activity.RESULT_CANCELED;
        mInProgress = 0;
        quit = false;
        mExecutor = Executors.newCachedThreadPool();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Runnable worker = new ServiceThread(startId, intent.getExtras().getInt(DELAY, 5000));
        mExecutor.execute(worker);


        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        quitThreads();
        publishResults();
    }

    private void quitThreads() {
        quit = true;
    }

    private void publishProgress(int pid, int progress) {
        Intent intent = new Intent(NOTIFICATION);
        intent.putExtra(PID, pid);
        intent.putExtra(FINISHED, quit);
        intent.putExtra(PROGRESS, progress);
        sendBroadcast(intent);
    }

    private void publishResults() {
        Intent intent = new Intent(NOTIFICATION);
        intent.putExtra(RESULT, mResult);
        intent.putExtra(FINISHED, true);
        sendBroadcast(intent);
    }
}
