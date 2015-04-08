package ua.grt.services.startedservice;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.os.*;
import android.os.Process;
import android.widget.Toast;

public class MyStartedService extends Service {

    private static final int STEP = 20;

    public static final String RESULT = "result";
    public static final String PROGRESS = "progress";
    public static final String PID = "pid";
    public static final String NOTIFICATION = "ua.grt.services.startedservice";
    public static final String DELAY = "delay";

    private int mResult;
    private Looper mServiceLooper;
    private ServiceHandler mServiceHandler;
    private HandlerThread mThread;

    //Handler that receives messages from the thread
    private final class ServiceHandler extends Handler{
        public ServiceHandler(Looper looper){
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            mResult = Activity.RESULT_CANCELED;
            long delay = msg.arg2;
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
                publishProgress(msg.arg1, progress);
            }

            mResult = Activity.RESULT_OK;
            stopSelf(msg.arg1);
        }
    }

    @Override
    public void onCreate() {
        //Start service work in separate thread
        //Make it background priority
        mThread = new HandlerThread("ServiceStartArguments",
                Process.THREAD_PRIORITY_BACKGROUND);
        mThread.start();

        // Get the HandlerThread's Looper and use it for our Handler
        mServiceLooper = mThread.getLooper();
        mServiceHandler = new ServiceHandler(mServiceLooper);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Message msg = mServiceHandler.obtainMessage();
        msg.arg1 = startId;
        msg.arg2 = intent.getExtras().getInt(DELAY,5000);
        mServiceHandler.sendMessage(msg);

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        mThread.quit();
        mServiceLooper.quit();
        publishResults();
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
