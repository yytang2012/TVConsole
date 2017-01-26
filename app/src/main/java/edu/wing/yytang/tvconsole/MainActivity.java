package edu.wing.yytang.tvconsole;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnTouchScreen = (Button)findViewById(R.id.touch_screen);
        btnTouchScreen.setOnClickListener(mOnClickListener);
    }

    public View.OnClickListener mOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            switch(v.getId()) {
                case R.id.touch_screen:
                    startActivity(new Intent(MainActivity.this, TouchScreenActivity.class));
                    break;
            }
        }
    };
}
