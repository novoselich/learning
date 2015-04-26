package ua.novoselich.asynctask;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import java.math.BigInteger;

public class AsyncTaskFragment extends Fragment implements View.OnClickListener {

    public static final int DELAY = 100;

    private View mStartBtn;
    private TextView mProgresTv;
    private TextView mStatusTv;
    private SeekBar mNumberSb;

    private int mNumber;
    private boolean running;

    private class FibonAsyncTask extends AsyncTask<Integer, BigInteger, Void> {

        private int count;
        private BigInteger n1, n2;

        @Override
        protected Void doInBackground(Integer... integers) {
            count = integers[0];

            if(count == 0){
                publishProgress(new BigInteger("0"));
                return null;
            }

            n1 = new BigInteger("1");
            publishProgress(n1);
            delay();
            count--;

            n2 = new BigInteger("1");
            publishProgress(n2);
            delay();
            count--;

            while (count > 0) {
                BigInteger sum = n1.add(n2);
                n1 = n2;
                n2 = sum;
                publishProgress(sum);
                delay();
                count--;
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(BigInteger... values) {
            mProgresTv.setText(values[0].toString());
        }

        @Override
        protected void onPreExecute() {
            mStatusTv.setText(R.string.inprogress_msg);
            mStartBtn.setEnabled(false);
            running = true;
        }

        @Override
        protected void onPostExecute(Void result) {
            mStatusTv.setText(R.string.done_msg);
            mStartBtn.setEnabled(true);
            running = false;
        }


        private void delay() {
            synchronized (this) {
                try {
                    wait(DELAY);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.start:
                if (running) {
                    return;
                }
                mNumber = mNumberSb.getProgress();
                new FibonAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mNumber);
                break;
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        View v = inflater.inflate(R.layout.fragment_layout, null, false);

        mStartBtn = v.findViewById(R.id.start);
        mStatusTv = (TextView) v.findViewById(R.id.status);
        mProgresTv = (TextView) v.findViewById(R.id.progress);
        mNumberSb = (SeekBar) v.findViewById(R.id.number);

        mStartBtn.setOnClickListener(this);


        return v;

    }


}


