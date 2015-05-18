package com.example.oleg.gradlecustomplugin;

import android.content.res.AssetManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.io.IOException;
import java.util.Properties;


public class MainActivity extends ActionBarActivity {

    private TextView mTxtAuthor;
    private TextView mTxtCommit;
    private TextView mTxtFlavour;

    private AssetManager mAssetManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTxtAuthor = (TextView) findViewById(R.id.author);
        mTxtCommit = (TextView) findViewById(R.id.commit);
        mTxtFlavour = (TextView) findViewById(R.id.flavour);

        mAssetManager = getAssets();

        populate();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != mAssetManager){
            mAssetManager.close();
        }
    }

    private void populate () {
        try {
            Properties properties = new Properties();
            properties.load(mAssetManager.open("about.properties"));

            mTxtAuthor.setText(getSpannableString (AboutKeys.AUTHOR.key, properties.getProperty(AboutKeys.AUTHOR.key)));
            mTxtCommit.setText(getSpannableString(AboutKeys.COMMIT.key, properties.getProperty(AboutKeys.COMMIT.key)));
            mTxtFlavour.setText(getSpannableString(AboutKeys.FLAVOUR.key, properties.getProperty(AboutKeys.FLAVOUR.key)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private SpannableString getSpannableString (String field, String value) {
        String separator = ": ";
        SpannableString text = new SpannableString(field + separator + value);
        text.setSpan(new RelativeSizeSpan(1.5f), 0, field.length() + separator.length(), 0);
        return text;
    }

    enum AboutKeys {
        AUTHOR ("author"),
        COMMIT ("commit"),
        FLAVOUR ("flavour");

        String key;

        AboutKeys (String propKey) {
            key = propKey;
        }
    }
}
