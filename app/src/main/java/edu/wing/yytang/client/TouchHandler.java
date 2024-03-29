package edu.wing.yytang.client;

import android.graphics.Point;
import android.util.Log;
import android.view.MotionEvent;

import edu.wing.yytang.common.Constants;
import edu.wing.yytang.performance.PerformanceAdapter;
import edu.wing.yytang.protocol.SVMPProtocol;
import edu.wing.yytang.tvconsole.TouchScreenActivity;


/**
 * Created by yytang on 3/30/16.
 */
public class TouchHandler implements Constants {

    private static final String TAG = TouchHandler.class.getName();

    private TouchScreenActivity activity;
    private PerformanceAdapter spi;
    private Point displaySize;
    private final int batchingSize = 8;

    private float xScaleFactor, yScaleFactor = 1;
    private boolean gotScreenInfo = false;
    private boolean batching = false;
    private SVMPProtocol.Request.Builder msg = null;

    public TouchHandler(TouchScreenActivity activity, Point displaySize, PerformanceAdapter spi) {
        this.activity = activity;
        this.displaySize = displaySize;
        this.spi = spi;
//        gotScreenInfo = true;
//        xScaleFactor = (float)1024/(float)1080;
//        yScaleFactor = (float)768/(float)1776;
    }

    public void sendScreenInfoMessage() {
        SVMPProtocol.Request.Builder msg = SVMPProtocol.Request.newBuilder();
        msg.setType(SVMPProtocol.Request.RequestType.SCREENINFO);

        activity.sendMessage(msg.build());
        Log.d(TAG, "Sent screen info request");
    }

    public boolean handleScreenInfoResponse(SVMPProtocol.Response msg) {
        if (!msg.hasScreenInfo())
            return false;

        final int x = msg.getScreenInfo().getX();
        final int y = msg.getScreenInfo().getY();

        Log.d(TAG, "Got the ServerInfo: xsize=" + x + " ; ysize=" + y);
        this.xScaleFactor = (float)x/(float)displaySize.x;
        this.yScaleFactor = (float)y/(float)displaySize.y;
        Log.i(TAG, "Scale factor: " + xScaleFactor + " ; " + yScaleFactor);

        gotScreenInfo = true;

        return true;
    }

    public boolean onTouchEvent(final MotionEvent event) {
        if (!activity.isConnected() || !gotScreenInfo) return false;

        // increment the touch update count for performance measurement
        spi.incrementTouchUpdates();

        // Create Protobuf message builders
//        SVMPProtocol.Request.Builder msg = SVMPProtocol.Request.newBuilder();

        if(batching) {
            if(event.getAction() != MotionEvent.ACTION_MOVE) {
                activity.sendMessage(msg.build());
                batching = false;
            }
        }
        if(!batching) {
            msg = SVMPProtocol.Request.newBuilder();
        }
        SVMPProtocol.TouchEvent.Builder eventmsg = SVMPProtocol.TouchEvent.newBuilder();
        SVMPProtocol.TouchEvent.PointerCoords.Builder p = SVMPProtocol.TouchEvent.PointerCoords.newBuilder();
        SVMPProtocol.TouchEvent.HistoricalEvent.Builder h = SVMPProtocol.TouchEvent.HistoricalEvent.newBuilder();

        // Set general touch event information
        eventmsg.setAction(event.getAction());
        eventmsg.setDownTime(event.getDownTime());
        if(event.getAction() != MotionEvent.ACTION_MOVE)
            eventmsg.setEventTime(event.getEventTime());
        eventmsg.setEdgeFlags(event.getEdgeFlags());

        // Loop and set pointer/coordinate information
        final int pointerCount = event.getPointerCount();
        for (int i = 0; i < pointerCount; i++) {
            final float adjX = event.getX(i) * this.xScaleFactor;
            final float adjY = event.getY(i) * this.yScaleFactor;
            p.clear();
            p.setId(event.getPointerId(i));
            p.setX(adjX);
            p.setY(adjY);
            eventmsg.addItems(p.build());
        }

        // Loop and set historical pointer/coordinate information
        final int historicalCount = event.getHistorySize();
        for (int i = 0; i < historicalCount; i++) {
            h.clear();
            for (int j = 0; j < pointerCount; j++) {
                p.clear();
                p.setId(event.getPointerId(j));
                p.setX(event.getHistoricalX(j,i) * this.xScaleFactor);
                p.setY(event.getHistoricalY(j,i) * this.yScaleFactor);
                h.addCoords(p.build());
            }
            h.setEventTime(event.getHistoricalEventTime(i));
            eventmsg.addHistorical(h.build());
        }

        // Add Request wrapper around touch event
        msg.setType(SVMPProtocol.Request.RequestType.TOUCHEVENT);
        msg.addTouch(eventmsg); // TODO: batch touch events

        // Send touch event to VM
        if(event.getAction() != MotionEvent.ACTION_MOVE || msg.getTouchList().size() > batchingSize) {
            activity.sendMessage(msg.build());
            batching = false;
        }
        else {
            batching = true;
        }

        return true;
    }
}
