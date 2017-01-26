package edu.wing.yytang.tvconsole;

import android.content.Context;
import android.graphics.Canvas;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by yytang on 1/25/17.
 */

public class TouchScreenView extends View {
    public TouchScreenView(Context context) {
        super(context);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        final String TAG = "onTouchEvent";
        float touched_x = event.getX();
        float touched_y = event.getY();
        String string = "x = " + touched_x + ", y = " + touched_y + ", ";
        int action = event.getAction();

        switch(action){
            case MotionEvent.ACTION_DOWN:
                string = string + "DOWN";
                break;
            case MotionEvent.ACTION_MOVE:
                string = string + "MOVE";
                break;
            case MotionEvent.ACTION_UP:
                string = string + "UP";
                break;
            case MotionEvent.ACTION_CANCEL:
                string = string + "CANCEL";
                break;
            case MotionEvent.ACTION_OUTSIDE:
                string = string + "OUTSIDE";
                break;
            default:
        }

        Log.i(TAG, string);
        return true;
    }
}
