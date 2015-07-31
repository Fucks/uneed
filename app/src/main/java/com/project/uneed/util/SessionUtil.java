package com.project.uneed.util;

import android.util.Log;

import com.project.uneed.model.User;

/**
 * Created by wellington.fucks on 28/07/15.
 */
public class SessionUtil {

    public static User currentUser;

    public static void printLog(String log){
        Log.d("com.project.uneed.Logger", log);
    }
}
