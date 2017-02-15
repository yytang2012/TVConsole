package edu.wing.yytang.tvconsole;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Point;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by yytang on 1/25/17.
 */

public class TouchScreenView extends View {
    private String TAG = TouchScreenView.class.getName();
    Point displaySize;
    TouchScreenActivity activity;

    public TouchScreenView(Context context, TouchScreenActivity activity, Point displaySize) {
        super(context);
        this.activity = activity;
        this.displaySize = displaySize;
        Log.i(TAG, "display x = " + displaySize.x + ", y = " + displaySize.y);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return this.activity.onTouchEvent(event);
    }

}
