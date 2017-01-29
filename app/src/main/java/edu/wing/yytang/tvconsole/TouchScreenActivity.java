package edu.wing.yytang.tvconsole;

import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import edu.wing.yytang.client.TouchHandler;
import edu.wing.yytang.common.Constants;
import edu.wing.yytang.performance.PerformanceAdapter;
import edu.wing.yytang.protocol.SVMPProtocol;

/**
 * Created by yytang on 1/25/17.
 */

public final class TouchScreenActivity extends AppCompatActivity implements Constants{

    private final String TAG = TouchScreenActivity.class.getName();
    private boolean proxying = false;
    private Socket mSocket = null;
    private TouchHandler touchHandler;
    private PerformanceAdapter spi;
    private InputStream in = null;
    private OutputStream out = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        spi = new PerformanceAdapter();
        Point displaySize = new Point();
        getWindowManager().getDefaultDisplay().getSize(displaySize);
        touchHandler = new TouchHandler(TouchScreenActivity.this, displaySize, spi);
        TouchScreenView tsView = new TouchScreenView(this, TouchScreenActivity.this, displaySize);
        if(proxying == false) {
            connectServer();
        }

        setContentView(tsView);
    }

    public void connectServer() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    mSocket = new Socket(ipAddr, PROXY_PORT);
                    in = mSocket.getInputStream();
                    out = mSocket.getOutputStream();
                    onOpen();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void onOpen() {
        proxying = true;
        onServerResponse();
        touchHandler.sendScreenInfoMessage();
    }

    private void onServerResponse () {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while(true) {
                        SVMPProtocol.Response data = SVMPProtocol.Response.parseDelimitedFrom(in);
                        Log.d(TAG, "Received incoming message object of type " + data.getType().name());
                        onResponseRUNNING(data);
                    }
                } catch (IOException e) {
                    Log.e(TAG, "Error on socket: " + e.getMessage());
                    e.printStackTrace();
                } finally {
                    try {
                        in.close();
                        out.close();
                        mSocket.close();
                    } catch (Exception e) {
                        // Don't care
                    } finally {
                        in = null;
                        out = null;
                    }
                    Log.d(TAG, "Client connection handler finished.");
                }
            }
        }).start();
    }

    private void onResponseRUNNING(final SVMPProtocol.Response data) {
        runOnUiThread(new Runnable() {
            public void run() {
                onMessage(data);
            }
        });
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
        if (isConnected()) {
            //webSocket.sendBinaryMessage(msg.toByteArray());
            // VM is expecting a message delimiter (varint prefix) so write a delimited message instead
            try {
                msg.writeDelimitedTo(out);

            } catch (IOException e) {
                Log.e(TAG, "Error writing delimited byte output:", e);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return touchHandler.onTouchEvent(event);
    }

    public boolean isConnected() {
        return mSocket != null && mSocket.isConnected() && !mSocket.isClosed();
    }

    /////////////////////////////////////////////////////////////////////
    // Bridge input callbacks to the Touch Input Handler
    /////////////////////////////////////////////////////////////////////
    private void handleScreenInfo(SVMPProtocol.Response msg) {
        touchHandler.handleScreenInfoResponse(msg);
    }
}
