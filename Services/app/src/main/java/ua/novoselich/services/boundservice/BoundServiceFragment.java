package ua.novoselich.services.boundservice;

import android.app.Activity;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import ua.novoselich.services.R;

public class BoundServiceFragment extends Fragment implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {

    private static final int DEFAULT_INDICATOR_SIZE = 30;

    private Context context;
    private boolean started;
    private boolean bound;

    private Button mStartBtn;
    private Button mBindBtn;
    private TextView mStatusText;
    private SeekBar mIndicatorSize;
    private ProgressBar mProgressBar;

    private Intent mServiceIntent;
    private MyBoundService.MyBinder service;

    private ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            bound = true;
            mBindBtn.setText(R.string.unbind_btn_caption);
            updateStatus();
            updateIndicator();
            service = (MyBoundService.MyBinder) iBinder;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            bound = false;
            mBindBtn.setText(R.string.bind_btn_caption);
            updateStatus();
            updateIndicator();
            service = null;
        }
    };

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                if (bundle.containsKey(MyBoundService.WORKING)) {
                    started = bundle.getBoolean(MyBoundService.WORKING);
                    if (!started) {
                        mStartBtn.setText(R.string.start_btn_caption);
                    } else {
                        mStartBtn.setText(R.string.stop_btn_caption);
                    }
                    updateStatus();
                }
                updateIndicator(bundle.getInt(MyBoundService.VALUE, DEFAULT_INDICATOR_SIZE));
            }
        }
    };

    private void updateStatus() {
        if (!started && !bound) {
            mStatusText.setText("");
            return;
        }
        if (started && !bound) {
            mStatusText.setText(R.string.started_msg);
            return;
        }
        if (!started && bound) {
            mStatusText.setText(R.string.bound_msg);
            return;
        }
        if (started && bound) {
            mStatusText.setText(R.string.started_and_bound_msg);
            return;
        }
    }

    private void updateIndicator() {
        updateIndicator(DEFAULT_INDICATOR_SIZE);
    }

    private void updateIndicator(int size) {
        if (!started && !bound) {
            mProgressBar.setVisibility(View.GONE);
            return;
        }
        mProgressBar.setVisibility(View.VISIBLE);
        mProgressBar.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, size));

    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        context = activity.getApplicationContext();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bound_service_layout, null, false);

        mServiceIntent = new Intent(context, MyBoundService.class);

        mStartBtn = (Button) view.findViewById(R.id.start_stop_boundService_btn);
        mBindBtn = (Button) view.findViewById(R.id.bind_unbind_boundService_btn);
        mStatusText = (TextView) view.findViewById(R.id.boundService_status);
        mIndicatorSize = (SeekBar) view.findViewById(R.id.boundService_indicator_size);
        mProgressBar = (ProgressBar) view.findViewById(R.id.progress);

        mBindBtn.setOnClickListener(this);
        mStartBtn.setOnClickListener(this);
        mIndicatorSize.setOnSeekBarChangeListener(this);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        context.registerReceiver(receiver, new IntentFilter(MyBoundService.NOTIFICATION));
    }

    @Override
    public void onPause() {
        super.onPause();
        context.unregisterReceiver(receiver);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.start_stop_boundService_btn:
                if (!started) {
                    context.startService(mServiceIntent);
                } else {
                    context.stopService(mServiceIntent);
                }
                break;
            case R.id.bind_unbind_boundService_btn:
                if (!bound) {
                    context.bindService(mServiceIntent, mServiceConnection, Context.BIND_AUTO_CREATE);
                } else {
                    context.unbindService(mServiceConnection);
                    bound = false;
                    mBindBtn.setText(R.string.bind_btn_caption);
                    updateStatus();
                    updateIndicator();
                }
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        if (bound && service != null) {
            service.setValue(i);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }
}
