package edu.wing.yytang.common;

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
}
