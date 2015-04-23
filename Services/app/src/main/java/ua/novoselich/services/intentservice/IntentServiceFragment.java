package ua.novoselich.services.intentservice;

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
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import ua.novoselich.services.R;

public class IntentServiceFragment extends Fragment implements View.OnClickListener {

    private Context context;

    Button mStartBtn;
    TextView mStatusText;
    SeekBar mDuration;
    ProgressBar mProgress;
    CheckBox mForeground;

    Intent serviceIntent;

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            if(bundle != null){
                if(bundle.containsKey(MyIntentService.RESULT)) {
                    // Service finished
                    onServiceCompleted();

                    int resultCode = bundle.getInt(MyIntentService.RESULT);
                    if (resultCode == Activity.RESULT_OK) {
                        mStatusText.setText(getString(R.string.success_msg));
                    } else {
                        mStatusText.setText(getString(R.string.failed_msg));
                    }
                } else {
                    // service still working
                    int progress = bundle.getInt(MyIntentService.PROGRESS);
                    mProgress.setProgress(progress);
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
        View view = inflater.inflate(R.layout.intent_service_layout, null);

        serviceIntent = new Intent(context, MyIntentService.class);

        mStartBtn = (Button) view.findViewById(R.id.start_intentService_btn);
        mStatusText = (TextView) view.findViewById(R.id.intentService_status);
        mDuration = (SeekBar) view.findViewById(R.id.intentService_duration);
        mProgress = (ProgressBar) view.findViewById(R.id.progress);
        mForeground = (CheckBox) view.findViewById(R.id.foreground_chbx);

        mStartBtn.setOnClickListener(this);

        return view;

    }

    @Override
    public void onResume() {
        super.onResume();
        context.registerReceiver(receiver, new IntentFilter(MyIntentService.NOTIFICATION));
    }

    @Override
    public void onPause() {
        super.onPause();
        context.unregisterReceiver(receiver);
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.start_intentService_btn:
                serviceIntent.putExtra(MyIntentService.DELAY, mDuration.getProgress());
                serviceIntent.putExtra(MyIntentService.FOREGROUND, mForeground.isChecked());
                context.startService(serviceIntent);

                mStatusText.setText(getString(R.string.started_msg));
                onServiceStarted();
                break;

        }
    }


    private void onServiceStarted() {
        mStartBtn.setEnabled(false);
        mDuration.setEnabled(false);
        mProgress.setProgress(0);
        mProgress.setVisibility(View.VISIBLE);
    }
    private void onServiceCompleted() {

        mProgress.setVisibility(View.INVISIBLE);
        mStartBtn.setEnabled(true);
        mDuration.setEnabled(true);
    }
}
