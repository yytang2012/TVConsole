package edu.wing.yytang.tvconsole;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Point;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Toast;

import edu.wing.yytang.apprtc.AppRTCClient;
import edu.wing.yytang.client.RotationHandler;
import edu.wing.yytang.client.TouchHandler;
import edu.wing.yytang.common.Constants;
import edu.wing.yytang.common.StateMachine;
import edu.wing.yytang.common.StateObserver;
import edu.wing.yytang.performance.PerformanceAdapter;
import edu.wing.yytang.protocol.SVMPProtocol;
import edu.wing.yytang.services.SessionService;

/**
 * Created by yytang on 1/25/17.
 */

public final class TouchScreenActivity extends AppCompatActivity implements StateObserver, Constants{

    private final String TAG = TouchScreenActivity.class.getName();
    protected AppRTCClient appRtcClient;
    private boolean bound = false;

    private boolean proxying = false;
    private TouchHandler touchHandler;
    private RotationHandler rotationHandler;
    private PerformanceAdapter spi;
    private TouchScreenView tsView = null;
    private Toast logToast;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        spi = new PerformanceAdapter();
        Point displaySize = new Point();
        getWindowManager().getDefaultDisplay().getSize(displaySize);
        touchHandler = new TouchHandler(TouchScreenActivity.this, displaySize, spi);
        rotationHandler = new RotationHandler(TouchScreenActivity.this);
        rotationHandler.initRotationUpdates();
        tsView = new TouchScreenView(this, TouchScreenActivity.this, displaySize);

        setContentView(tsView);
        connectToRoom();
    }

    public void connectToRoom() {
        logAndToast(R.string.appRTC_toast_connection_start);

        bindService(new Intent(this, SessionService.class), serviceConnection, 0);
    }

    /** Defines callbacks for service binding, passed to bindService() */
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder iBinder) {
            // We've bound to SessionService, cast the IBinder and get SessionService instance
            appRtcClient = (AppRTCClient) iBinder;
            bound = true;

            // after we have bound to the service, begin the connection
            appRtcClient.connectToRoom(TouchScreenActivity.this);
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
        }
    };

    public void onOpen() {
        touchHandler.sendScreenInfoMessage();
        proxying = true;
    }

    protected void onClose() {
        if (rotationHandler != null)
            rotationHandler.cleanupRotationUpdates();
    }


    public boolean onMessage(SVMPProtocol.Response data) {
        switch (data.getType()) {
            case SCREENINFO:
                handleScreenInfo(data);
                break;
            default:
                // any messages we don't understand, pass to our parent for processing
        }
        return true;

    }

    public void sendMessage(SVMPProtocol.Request msg) {
        if (appRtcClient != null)
            appRtcClient.sendMessage(msg);
    }

    public boolean isConnected() {
        return proxying;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return touchHandler.onTouchEvent(event);
    }


    /////////////////////////////////////////////////////////////////////
    // Bridge input callbacks to the Touch Input Handler
    /////////////////////////////////////////////////////////////////////
    private void handleScreenInfo(SVMPProtocol.Response msg) {
        touchHandler.handleScreenInfoResponse(msg);
    }

    @Override
    public void onStateChange(StateMachine.STATE oldState, StateMachine.STATE newState, int resID) {

    }

    // Log |msg| and Toast about it.
    public void logAndToast(final int resID) {
        Log.d(TAG, getResources().getString(resID));
        this.runOnUiThread(new Runnable() {
            public void run() {
                if (logToast != null) {
                    logToast.cancel();
                }
                logToast = Toast.makeText(TouchScreenActivity.this, resID, Toast.LENGTH_SHORT);
                logToast.show();
            }
        });
    }
}
