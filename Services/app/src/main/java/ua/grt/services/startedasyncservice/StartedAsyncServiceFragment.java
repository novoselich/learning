package ua.grt.services.startedasyncservice;

import android.app.Activity;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

import ua.grt.services.R;
import ua.grt.services.startedservice.MyStartedService;

public class StartedAsyncServiceFragment extends Fragment implements View.OnClickListener {

    private Context context;

    private Button mStartBtn;
    private Button mStopBtn;
    private TextView mStatusText;
    private SeekBar mDuration;
    private LinearLayout mProgressesContainer;
    private Map<Integer, ProgressBar> mProgresses;

    private boolean mIsInterrupted;

    private Intent mServiceIntent;

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(mIsInterrupted){
                return;
            }
            Bundle bundle = intent.getExtras();
            if(bundle != null){
                if(bundle.containsKey(MyStartedService.PROGRESS)){
                    //service still working
                    int progress = bundle.getInt(MyStartedService.PROGRESS);
                    int pid = bundle.getInt(MyStartedService.PID);
                    if(mProgresses.containsKey(pid)){
                        mProgresses.get(pid).setProgress(progress);
                    } else {
                        LinearLayout v = (LinearLayout)getActivity().getLayoutInflater().inflate(R.layout.service_progress,null,false);
                        ((TextView)v.findViewById(R.id.pid)).setText(getString(R.string.pid_caption, pid));
                        ProgressBar pb = (ProgressBar)v.findViewById(R.id.progress);
                        mProgresses.put(pid, pb);
                        mProgressesContainer.addView(v);
                        pb.setProgress(progress);
                    }
                } else {
                    //Service finished
                    onServiceCompleted();

                    int resultCode = bundle.getInt(MyStartedService.RESULT);
                    if(resultCode == Activity.RESULT_OK){
                        mStatusText.setText(getString(R.string.success_msg));
                    } else {
                        mStatusText.setText(getString(R.string.failed_msg));
                    }
                }
            }
        }
    };

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        context = activity.getApplicationContext();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.started_async_service_layout,null,false);

        mServiceIntent = new Intent(context, MyStartedAsyncService.class);

        mStartBtn = (Button) view.findViewById(R.id.start_startedAsyncServiceBtn);
        mStopBtn = (Button) view.findViewById(R.id.stop_startedAsyncServiceBtnServiceBtn);
        mStatusText = (TextView) view.findViewById(R.id.startedAsyncService_status);
        mDuration = (SeekBar) view.findViewById(R.id.startedAsyncService_duration);
        mProgressesContainer = (LinearLayout) view.findViewById(R.id.progresses_container);

        mProgresses = new HashMap<>();

        mStartBtn.setOnClickListener(this);
        mStopBtn.setOnClickListener(this);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        context.registerReceiver(receiver, new IntentFilter(MyStartedAsyncService.NOTIFICATION));
    }

    @Override
    public void onPause() {
        super.onPause();
        context.unregisterReceiver(receiver);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.start_startedAsyncServiceBtn:
                mIsInterrupted = false;
                mServiceIntent.putExtra(MyStartedService.DELAY, mDuration.getProgress());
                context.startService(mServiceIntent);
                mStopBtn.setEnabled(true);
                mStatusText.setText(getString(R.string.started_msg));
                break;
            case R.id.stop_startedAsyncServiceBtnServiceBtn:
                mIsInterrupted = true;
                context.stopService(mServiceIntent);
                onServiceCompleted();
                mStatusText.setText(getString(R.string.interrupted_msg));
                break;
        }
    }

    private void onServiceCompleted() {

        mProgressesContainer.removeAllViews();
        mProgresses.clear();
        mStopBtn.setEnabled(false);
    }
}
