package edu.wing.yytang.common;

/**
 * Created by yytang on 2/10/17.
 */

public class ConnectionInfo implements Constants {

    private int connectionID;
    private String description;
    private String username;
    private String host;
    private int port;
    private int encryptionType;
    private int authType;
    private String certificateAlias;
    private int appCount;

    // constructor
    public ConnectionInfo(int connectionID, String description, String username, String host, int port,
                          int encryptionType, int authType, String certificateAlias, int appCount) {
        this.connectionID = connectionID;
        this.description = description;
        this.username = username;
        this.host = host;
        this.port = port;
        this.encryptionType = encryptionType;
        this.authType = authType;
        this.certificateAlias = certificateAlias;
        this.appCount = appCount;
    }

    // getters
    public int getConnectionID() {
        return connectionID;
    }

    public String getDescription() {
        return description;
    }

    public String getUsername() {
        return username;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public int getEncryptionType() {
        return encryptionType;
    }

    public int getAuthType() {
        return authType;
    }

    public String getCertificateAlias() {
        return certificateAlias;
    }

    public int getAppCount() {
        return appCount;
    }

    // used to describe each ConnectionInfo in ConnectionList activity
    public String lineOneText() {
        return description;
    }

    public String lineTwoText() {
//        boolean certAuthType = (authType & CertificateModule.AUTH_MODULE_ID) == CertificateModule.AUTH_MODULE_ID;
        String text = username;
        String authDesc = null;
//        if (certAuthType && certificateAlias.length() > 0)
//            text = certificateAlias;
//        String authDesc = AuthRegistry.getAuthType(authType).getDescription();
        return String.format("%s, %s@%s:%d", authDesc, text, host, port);
    }

    public String buttonText() {
        return String.format("%d Apps", appCount);
    }

}
