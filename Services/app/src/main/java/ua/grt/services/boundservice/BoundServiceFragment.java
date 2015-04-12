package ua.grt.services.boundservice;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import ua.grt.services.R;

public class BoundServiceFragment extends Fragment implements View.OnClickListener {

    private Context context;
    private boolean started;
    private boolean bound;

    private Button mStartBtn;
    private Button mBindBtn;
    private TextView mStatusText;
    private SeekBar mIndicatorSize;
    private ProgressBar mProgressBar;

    private Intent mServiceIntent;

    // TODO define the receiver

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        context = activity.getApplicationContext();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bound_service_layout, null, false);

        // TODO create an intent

        mStartBtn = (Button) view.findViewById(R.id.start_stop_boundService_btn);
        mBindBtn = (Button) view.findViewById(R.id.bind_unbind_boundService_btn);
        mStatusText = (TextView) view.findViewById(R.id.boundService_status);
        mIndicatorSize = (SeekBar) view.findViewById(R.id.boundService_indicator_size);
        mProgressBar = (ProgressBar) view.findViewById(R.id.progress);

        mBindBtn.setOnClickListener(this);
        mStartBtn.setOnClickListener(this);

        return view;
    }

    // TODO register & unregister receiver


    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onClick(View view) {

    }
}
