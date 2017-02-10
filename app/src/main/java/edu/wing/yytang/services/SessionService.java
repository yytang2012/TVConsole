package edu.wing.yytang.services;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.IBinder;
import android.util.Log;

import edu.wing.yytang.client.SensorHandler;
import edu.wing.yytang.common.StateMachine;
import edu.wing.yytang.common.StateMachine.STATE;

import edu.wing.yytang.common.Constants;
import edu.wing.yytang.performance.PerformanceAdapter;
import edu.wing.yytang.protocol.SVMPProtocol;

/**
 * Created by yytang on 2/9/17.
 */

public class SessionService extends Service implements SensorEventListener, Constants {
    private static final String TAG = SessionService.class.getName();

    // only one service is started at a time, acts as a singleton for static getters
    private static SessionService service;
    private StateMachine machine;
    private PerformanceAdapter performanceAdapter;
    private SensorHandler sensorHandler;

    @Override
    public void onCreate() {
        Log.v(TAG, "onCreate");

        service = this;
        machine = new StateMachine();
        performanceAdapter = new PerformanceAdapter();
    }

    // public getters for state and connectionID (used by activities)
    public static STATE getState() {
        STATE value = STATE.NEW;
        if (service != null && service.machine != null)
            value = service.machine.getState();
        return value;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.v(TAG, "onStartCommand");

        if (ACTION_STOP_SERVICE.equals(intent.getAction())) {
            stopSelf();
        }
        else if (getState() == STATE.NEW) {
            // change state and get connectionID from intent
            machine.setState(STATE.STARTED, 0);
            int connectionID = intent.getIntExtra("connectionID", 0);

            // begin connecting to the server
            startup(connectionID);
        }

        return START_NOT_STICKY; // run until explicitly stopped.
    }

    @Override
    public void onDestroy() {
        Log.v(TAG, "onDestroy");

        // before we destroy this service, shut down its components
        shutdown();

        super.onDestroy();
    }

    private void startup(int connectionID) {
        Log.i(TAG, "Starting background service");

        // create a sensor handler object
        sensorHandler = new SensorHandler(this);
        sensorHandler.initSensors(); // start forwarding sensor data
    }

    private void shutdown() {
        Log.i(TAG, "Shutting down background service.");

        // reset singleton
        service = null;

        // clean up sensor updates
        if (sensorHandler != null)
            sensorHandler.cleanupSensors();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
