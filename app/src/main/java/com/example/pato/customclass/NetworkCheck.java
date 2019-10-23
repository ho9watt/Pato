package com.example.pato.customclass;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkCheck {

    public static boolean isNetworkCheck(Context context){
        if(context != null){
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

            if(networkInfo != null && connectivityManager != null) {
                if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                    if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnectedOrConnecting() && connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isAvailable()) {
                        return true;
                    }else{
                        return false;
                    }
                }else if(networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                    if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnectedOrConnecting() && connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isAvailable()) {
                        return true;
                    }else{
                        return false;
                    }
                }else if(networkInfo.getType() == ConnectivityManager.TYPE_WIMAX) {
                    if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIMAX).isConnectedOrConnecting() && connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIMAX).isAvailable()) {
                        return true;
                    }else{
                        return false;
                    }
                }else{
                    return false;
                }
            }else{
                return false;
            }
        }else{
            return false;
        }
    }
}
