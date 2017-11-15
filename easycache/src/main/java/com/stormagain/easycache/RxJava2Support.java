package com.stormagain.easycache;

/**
 * Created by 37X21=777 on 17/11/2.
 */

public class RxJava2Support {


    private static int rxJava2Status = -1;

    public static boolean hasRxJava2() {
        if (rxJava2Status == -1) {
            try {
                Class c = Class.forName("io.reactivex.Observable");
                if (c != null) {
                    rxJava2Status = 1;
                } else {
                    rxJava2Status = 0;
                }
            } catch (ClassNotFoundException e) {
                //ignore
                e.printStackTrace();
            }
        }

        return rxJava2Status == 1;
    }

}
