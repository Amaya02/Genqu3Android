package com.gen.genqu3;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class CheckNetwork {
    private static final String TAG = CheckNetwork.class.getSimpleName();

    public static boolean isAvail(Context context){
        NetworkInfo info = (NetworkInfo) ((ConnectivityManager)
        context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();

        if(info == null){
            Log.d(TAG, "No internet Connection");
            return false;
        }
        else{
            if(info.isConnected()){
                Log.d(TAG, "internet connection available..");
                return true;
            }
            else{
                Log.d(TAG, "internet connection");
                return false;
            }
        }
    }
}
