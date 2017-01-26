package edu.wing.yytang.tvconsole;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by yytang on 1/25/17.
 */

public final class TouchScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TouchScreenView tsView = new TouchScreenView(this);
        setContentView(tsView);
    }
}
