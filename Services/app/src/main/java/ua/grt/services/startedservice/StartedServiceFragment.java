package ua.grt.services.startedservice;

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

public class StartedServiceFragment extends Fragment implements View.OnClickListener {

    private Context context;

    private Button mStartBtn;
    private Button mStopBtn;
    private TextView mStatusText;
    private TextView mQueuedText;
    private SeekBar mDuration;
    private LinearLayout mProgressesContainer;
    private Map<Integer, ProgressBar> mProgresses;

    private int mQueuedProcesses;

    private Intent mServiceIntent;

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            if(bundle != null){
                if (bundle.getInt(MyStartedService.PROGRESS)>=0 ) {
                    // service still working
                    int progress = bundle.getInt(MyStartedService.PROGRESS);
                    int pid = bundle.getInt(MyStartedService.PID);
                    if(mProgresses.containsKey(pid)){
                        mProgresses.get(pid).setProgress(progress);
                    } else if(mQueuedProcesses >0) {
                        LinearLayout v = (LinearLayout)getActivity().getLayoutInflater().inflate(R.layout.service_progress,null,false);
                        ((TextView)v.findViewById(R.id.pid)).setText(getString(R.string.pid_caption, pid));
                        ProgressBar pb = (ProgressBar)v.findViewById(R.id.progress);
                        mProgresses.put(pid, pb);
                        mProgressesContainer.addView(v);
                        pb.setProgress(progress);
                        mQueuedProcesses--;
                        if(mQueuedProcesses == 0){
                            mQueuedText.setText("");
                        }else {
                            mQueuedText.setText(getString(R.string.queued_msg, mQueuedProcesses));
                        }

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
        View view = inflater.inflate(R.layout.started_service_layout, null);

        mServiceIntent = new Intent(context, MyStartedService.class);

        mStartBtn = (Button) view.findViewById(R.id.start_startedServiceBtn);
        mStopBtn = (Button) view.findViewById(R.id.stop_startedServiceBtn);
        mStatusText = (TextView) view.findViewById(R.id.startedService_status);
        mQueuedText = (TextView) view.findViewById(R.id.startedService_queued);
        mDuration = (SeekBar) view.findViewById(R.id.startedService_duration);
        mProgressesContainer = (LinearLayout) view.findViewById(R.id.progresses_container);

        mProgresses = new HashMap<>();
        mQueuedProcesses = 0;

        mStartBtn.setOnClickListener(this);
        mStopBtn.setOnClickListener(this);

        return view;

    }


    @Override
    public void onResume() {
        super.onResume();
        context.registerReceiver(receiver, new IntentFilter(MyStartedService.NOTIFICATION));
    }

    @Override
    public void onPause() {
        super.onPause();
        context.unregisterReceiver(receiver);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.start_startedServiceBtn:
                mServiceIntent.putExtra(MyStartedService.DELAY, mDuration.getProgress());
                context.startService(mServiceIntent);
                mQueuedProcesses++;
                mQueuedText.setText(getString(R.string.queued_msg, mQueuedProcesses));
                mStopBtn.setEnabled(true);
                mStatusText.setText(getString(R.string.started_msg));
                break;
            case R.id.stop_startedServiceBtn:
                context.stopService(mServiceIntent);
                break;

        }
    }


    private void onServiceCompleted() {

        mProgressesContainer.removeAllViews();
        mProgresses.clear();
        mQueuedProcesses = 0;
        mQueuedText.setText("");
        mStopBtn.setEnabled(false);
    }
}
