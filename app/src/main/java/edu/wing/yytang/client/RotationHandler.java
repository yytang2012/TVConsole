package edu.wing.yytang.client;

import android.content.Context;
import android.hardware.SensorManager;
import android.os.Handler;
import android.util.Log;
import android.view.OrientationEventListener;
import android.view.Surface;

import edu.wing.yytang.common.Utility;
import edu.wing.yytang.protocol.SVMPProtocol;
import edu.wing.yytang.tvconsole.TouchScreenActivity;

/**
 * Created by yytang on 1/30/17.
 */

public class RotationHandler extends OrientationEventListener {
    private static final String TAG = RotationHandler.class.getName();

    // the current rotation has a wider allowance of degrees before triggering a rotation change event
    private static final int ANGLE_ALLOWANCE = 130;
    // the number of milliseconds we wait before triggering a rotation change event
    private static final int TIME_ALLOWANCE = 500;

    private TouchScreenActivity activity;
    private boolean running = false;
    private int rotation = 0; // valid values are : 0, 1, 2, 3
    private int proposedRotation = 0;
    private int currentMin;
    private int currentMax;
    private Handler taskHandler = null;
    private RotateTask rotateTask = null;

    public RotationHandler(TouchScreenActivity touchScreenActivity) {
        super(touchScreenActivity, SensorManager.SENSOR_DELAY_NORMAL);
        this.activity = touchScreenActivity;
    }

    public void initRotationUpdates() {
        if(canDetectOrientation()) {
            running = true;
            taskHandler = new Handler();

            // get the current rotation
            rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
            proposedRotation = rotation;
            setCurrentMinMax();

            // enable listener for rotation changes
            enable();
            Log.d(TAG, "Can detect orientation, RotationHandler has been enabled");

            // send initial rotation
            sendRotationInfo();
        }
        else
            Log.d(TAG, "Can NOT detect orientation, RotationHandler has NOT been enabled");
    }

    public void cleanupRotationUpdates() {
        running = false;
        disable();
    }

    @Override
    public void onOrientationChanged(int orientation) {
        if(orientation != ORIENTATION_UNKNOWN) {
            int newRotation =getUpdatedRotation(orientation);
            if (rotateTask == null && rotation != newRotation) {
                proposedRotation = newRotation;
                rotateTask = new RotateTask();
                taskHandler.postDelayed(rotateTask, TIME_ALLOWANCE);
            }
            else if (rotateTask != null && rotation != newRotation && proposedRotation != newRotation && newRotation != 2) {
                // sometimes the user may continue to rotate the screen
                // (for instance, from 90 to 0, then to 270, before the rotation change has triggered)
                // note: only change a screen rotation that's in progress if the newRotation isn't ROTATION_180
                // (180 is upside down, which some devices don't support - rotate to ROTATION_270 or ROTATION_90 first)
                proposedRotation = newRotation;
            }
            else if (rotateTask != null && rotation == newRotation) {
                // we've reached our original rotation before the proposedRotation could trigger a change
                // cancel the RotateTask and remove it
                proposedRotation = rotation;
                rotateTask.cancel();
                rotateTask = null;
            }
        }
    }

    // takes input of 0 to 359, outputs the rotation detected (0, 1, 2, or 3)
    // weighted based on current rotation, i.e. has an angle allowance greater than 90 degrees
    private int getUpdatedRotation(int degrees) {
        int value = rotation;
        if (outsideCurrentMinMax(degrees)) {
            if (degrees >= 315 || degrees < 45)
                value = Surface.ROTATION_0;
            else if (degrees >= 45 && degrees < 135)
                value = Surface.ROTATION_270;
            else if (degrees >= 135 && degrees < 225)
                value = Surface.ROTATION_180;
            else if (degrees >= 225 && degrees < 315)
                value = Surface.ROTATION_90;
        }
        return value;
    }
    // called when the rotation is changed
    // sets the current minimum and maximum values that will trigger another rotation change
    private void setCurrentMinMax() {
        int multiplier = 0;
        if (rotation == 1)
            multiplier = 3;
        else if (rotation == 2)
            multiplier = 2;
        else if (rotation == 3)
            multiplier = 1;

        // the minimum and maximum degrees for the current rotation should correspond to the ANGLE_ALLOWANCE
        currentMin = (multiplier*90) - (ANGLE_ALLOWANCE/2);
        currentMax = (multiplier*90) + (ANGLE_ALLOWANCE/2);
        if (currentMin < 0)
            currentMin += 360;
    }

    private boolean outsideCurrentMinMax(int degrees) {
        boolean value;
        if (rotation == 0)
            value = degrees < currentMin && degrees > currentMax;
        else
            value = degrees < currentMin || degrees > currentMax;
        return value;
    }

    private void sendRotationInfo() {
        Log.i(TAG, "rotation = " + rotation);
        if (activity.isConnected()) {
            // construct a Request object
            SVMPProtocol.Request request = Utility.toRequest_RotationInfo(rotation);

            // send the Request to the VM
            activity.sendMessage(request);
        }
    }

    private class RotateTask implements Runnable {
        private boolean cancelled = false;

        @Override
        public void run() {
            if (running && !cancelled) {
                // the task has finished and the current rotation has not changed back to the original rotation
                // trigger a rotation change message
                rotation = proposedRotation;
                setCurrentMinMax();
                rotateTask = null;
                sendRotationInfo();
            }
        }

        public void cancel() {
            cancelled = true;
        }
    }
}
