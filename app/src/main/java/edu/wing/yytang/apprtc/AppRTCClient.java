package edu.wing.yytang.apprtc;

import android.os.Binder;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import edu.wing.yytang.common.ConnectionInfo;
import edu.wing.yytang.common.Constants;
import edu.wing.yytang.common.StateMachine;
import edu.wing.yytang.protocol.SVMPProtocol;
import edu.wing.yytang.services.SessionService;
import edu.wing.yytang.tvconsole.TouchScreenActivity;

import static java.lang.System.out;

/**
 * Created by yytang on 2/10/17.
 */

public class AppRTCClient extends Binder implements Constants {
    private static final String TAG = AppRTCClient.class.getName();

    // service and activity objects
    private StateMachine machine;
    private SessionService service = null;
    private TouchScreenActivity activity = null;
    private Socket mSocket = null;
    private InputStream in = null;
    private OutputStream out = null;

    // common variables
    private ConnectionInfo connectionInfo;
    private boolean init = false; // switched to 'true' when activity first binds
    private boolean proxying = false; // switched to 'true' upon state machine change

    // STEP 0: NEW -> STARTED
    public AppRTCClient(SessionService service, StateMachine machine, ConnectionInfo connectionInfo) {
        this.service = service;
        this.machine = machine;
        machine.addObserver(service);
        this.connectionInfo = connectionInfo;

        machine.setState(StateMachine.STATE.STARTED, 0);
    }

    // called from activity
    public void connectToHost(final TouchScreenActivity activity, final String hostIP, final int hostPort) {
        this.activity = activity;
        machine.addObserver(activity);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    mSocket = new Socket(hostIP, hostPort);
                    in = mSocket.getInputStream();
                    out = mSocket.getOutputStream();
                    proxying = true;
                    onServerResponse();
                    activity.onOpen();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

//        if (machine.getState() == StateMachine.STATE.RUNNING) {
//            activity.onOpen();
//        }
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

    public void disconnect() {
        proxying = false;

        try {
            in.close();
            out.close();
            mSocket.close();
        } catch (Exception e) {
            // Don't care
        } finally {
            in = null;
            out = null;
            mSocket = null;
        }
    }

    public synchronized void sendMessage(SVMPProtocol.Request msg) {
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

    public boolean isConnected() {
        return proxying == true && mSocket != null && mSocket.isConnected() && !mSocket.isClosed();
    }

    public boolean isBound() {
        return this.activity != null;
    }

    private void onResponseRUNNING(final SVMPProtocol.Response data) {
        if (isBound()) {
            activity.runOnUiThread(new Runnable() {
                public void run() {
                    activity.onMessage(data);
                }
            });
        }
    }
}
