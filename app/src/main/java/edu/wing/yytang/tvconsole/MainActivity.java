package edu.wing.yytang.tvconsole;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import edu.wing.yytang.common.Constants;
import edu.wing.yytang.common.StateMachine;
import edu.wing.yytang.services.SessionService;

public class MainActivity extends AppCompatActivity implements Constants{
    private final static String TAG = MainActivity.class.getName();
    MainActivity mActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mActivity = this;

        Button btnTouchScreen = (Button)findViewById(R.id.touch_screen);
        Button btnStopService = (Button)findViewById(R.id.exit);
        btnTouchScreen.setOnClickListener(mOnClickListener);
        btnStopService.setOnClickListener(mOnClickListener);
    }

    public View.OnClickListener mOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            startDaemonService();
            switch(v.getId()) {
                case R.id.touch_screen:
                    startActivity(new Intent(MainActivity.this, TouchScreenActivity.class));
                    break;
                case R.id.exit:
                    stopConnection();
                    break;
                default:
                    break;
            }
        }
    };

    public void startDaemonService() {
        if(SessionService.getState() == StateMachine.STATE.NEW) {
            Intent intent = new Intent(MainActivity.this, SessionService.class).putExtra("connectionID", 0);
            startService(intent);
        }
    }

    private void stopConnection() {// Broadcast the custom intent
        Intent intent = new Intent();
        intent.setAction(ACTION_STOP_SERVICE);
        sendBroadcast(intent);
        stopService(new Intent(MainActivity.this, SessionService.class));

    }
    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestroy");

        super.onDestroy();
    }

}
