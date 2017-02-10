package edu.wing.yytang.common;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import edu.wing.yytang.protocol.SVMPProtocol;

/**
 * Created by yytang on 1/30/17.
 */

public class Utility {
    public static SVMPProtocol.Request toRequest_RotationInfo(int rotation) {
        // create a RotationInfo Builder
        SVMPProtocol.RotationInfo.Builder riBuilder = SVMPProtocol.RotationInfo.newBuilder()
                // set required variables
                .setRotation(rotation);


        // pack RotationInfo into Request wrapper
        SVMPProtocol.Request.Builder rBuilder = SVMPProtocol.Request.newBuilder()
                .setType(SVMPProtocol.Request.RequestType.ROTATION_INFO)
                .setRotationInfo(riBuilder);

        // build the Request
        return rBuilder.build();
    }

    public static String getPrefString(Context context, int keyId, int defaultValueId) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String key = context.getString(keyId);
        String defaultValue = context.getString(defaultValueId);

        return sharedPreferences.getString(key, defaultValue);
    }

    public static int getPrefInt(Context context, int keyId, int defaultValueId) {
        String prefString = getPrefString(context, keyId, defaultValueId);

        int value = 0;
        try {
            value = Integer.parseInt(prefString);
        } catch( Exception e ) { /* don't care */ }

        return value;
    }

    public static boolean getPrefBool(Context context, int keyId, int defaultValueId) {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String key = context.getString(keyId);
        boolean defaultValue = false;
        try {
            defaultValue = Boolean.parseBoolean(context.getString(defaultValueId));
        } catch( Exception e ) { /* don't care */ }

        return sharedPreferences.getBoolean(key, defaultValue);
    }
}
