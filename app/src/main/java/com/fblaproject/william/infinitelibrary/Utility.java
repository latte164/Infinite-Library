package com.fblaproject.william.infinitelibrary;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.Gravity;
import android.widget.Toast;
import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Various utility functions that can be used around the app
 */

public class Utility {
    
    public static boolean isNetworkConnected(Context context) {
        //Checks if the device is connected or connecting to a network
        //If not, displays toast

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if(!(networkInfo != null && networkInfo.isConnectedOrConnecting())) {
            Toast toast = Toast.makeText(getApplicationContext(),"Please check your internet connection.", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.BOTTOM|Gravity.CENTER,0,50);
            toast.show();
        }

        return (networkInfo != null && networkInfo.isConnectedOrConnecting());
        
    }
    
}
