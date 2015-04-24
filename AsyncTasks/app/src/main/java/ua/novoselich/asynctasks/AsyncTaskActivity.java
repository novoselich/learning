package ua.novoselich.asynctasks;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.math.BigInteger;

public class AsyncTaskActivity extends Activity implements View.OnClickListener {

    public static final int DELAY = 50;

    private View mStartBtn;
    private TextView mProgresTv;
    private TextView mResultTv;
    private EditText mNumberEd;

    private int mNumber;

    private AsyncTask<Integer, Long, BigInteger> fibonAsyncTask = new AsyncTask<Integer, Long, BigInteger>() {

        private int count;
        private BigInteger mult;
        private Long n1, n2;

        @Override
        protected BigInteger doInBackground(Integer... integers) {
            count = integers[0];
            mult = new BigInteger("1");

            n1 = 1l;
            publishProgress(n1);
            delay();
            count--;

            n2 = 1l;
            publishProgress(n2);
            delay();
            count--;

            while (count > 0) {
                long sum = n1 + n2;
                n1 = n2;
                n2 = sum;
                mult = mult.multiply(new BigInteger(String.valueOf(sum)));
                publishProgress(sum);
                delay();
                count --;
            }

            return mult;

        }

        @Override
        protected void onProgressUpdate(Long... values) {
            mProgresTv.setText(Long.toString(values[0]));
        }

        @Override
        protected void onPostExecute(BigInteger result) {
            mResultTv.setText(result.toString());
        }

        private void delay(){
            try{
                wait(DELAY);
            } catch (Exception e){
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        setContentView(R.layout.async_task_activity);

        mStartBtn = findViewById(R.id.start);
        mResultTv = (TextView)findViewById(R.id.result);
        mProgresTv = (TextView)findViewById(R.id.progress);
        mNumberEd = (EditText)findViewById(R.id.number);

        mStartBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.start:
                if (TextUtils.isEmpty(mNumberEd.getText())){
                    return;
                }
                mNumber = Integer.parseInt(mNumberEd.getText().toString());
                if(mNumber < 2){
                    return;
                }
                fibonAsyncTask.execute(mNumber);
                break;
        }
    }
}
