package edu.wing.yytang.tvconsole;

import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
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
    private BufferedInputStream in = null;
    private BufferedOutputStream out = null;
    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        spi = new PerformanceAdapter();
        Point displaySize = new Point();
        getWindowManager().getDefaultDisplay().getSize(displaySize);
        touchHandler = new TouchHandler(TouchScreenActivity.this, displaySize, spi);
        TouchScreenView tsView = new TouchScreenView(this, TouchScreenActivity.this, displaySize);
        connectServer();
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message message) {
//                InputStream stream = ByteArrayOutputStream.
//                ByteArrayInputStream stream = new ByteArrayInputStream(message.obj);
//                msg.writeDelimitedTo(stream);
//                SVMPProtocol.Request msg = SVMPProtocol.Request.parseDelimitedFrom(message.obj)
                Log.i(TAG, "msg = " + message.obj);
                if(out != null) {
                    try {
                        out.write((byte[]) message.obj);
                        out.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        setContentView(tsView);
    }

    public void connectServer() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    mSocket = new Socket(ipAddr, PROXY_PORT);
                    in = new BufferedInputStream(mSocket.getInputStream());
                    out = new BufferedOutputStream(mSocket.getOutputStream());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void sendMessage(SVMPProtocol.Request msg) {
        if (isConnected()) {
            //webSocket.sendBinaryMessage(msg.toByteArray());
            // VM is expecting a message delimiter (varint prefix) so write a delimited message instead
            try {
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                msg.writeDelimitedTo(stream);
                Message message = mHandler.obtainMessage();
                message.obj = stream.toByteArray();
                mHandler.sendMessage(message);
//                webSocket.sendBinaryMessage(stream.toByteArray());

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
}
