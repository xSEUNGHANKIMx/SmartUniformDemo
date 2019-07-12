package com.smartuniform;

public class SmartUniformStatics {

    final public static String SERVER_IP = "66.27.90.117";
    final public static String SERVER_URL = "http://" + SERVER_IP + "/";

    final public static int RESULT_CAPTURE = 1001;
    final public static int RESULT_CAPTURE_RESULT = 1002;

    final public static String EXTRA_RESULT_URI = "extra_result_uri";

    private SmartUniformStatics() {
    }

    private static volatile com.smartuniform.SmartUniformStatics sInstance;

    public static com.smartuniform.SmartUniformStatics GetInstance() {

        if  (sInstance == null) {
            synchronized (com.smartuniform.SmartUniformApp.class) {
                if (sInstance == null) {
                    sInstance = new com.smartuniform.SmartUniformStatics();
                }
            }
        }

        return sInstance;
    }
}