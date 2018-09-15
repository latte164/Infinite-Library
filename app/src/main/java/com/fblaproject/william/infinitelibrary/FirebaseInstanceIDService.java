package com.fblaproject.william.infinitelibrary;

import android.content.SharedPreferences;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import java.util.HashMap;
import java.util.Map;


/**
 * Recieves New Tokens and updates them in the database for the user
 */

public class FirebaseInstanceIDService extends FirebaseInstanceIdService {

    //Runs when a new token is received
    public void onTokenRefresh() {

        SharedPreferences sharedPrefs = getSharedPreferences("sharedPrefs", MODE_PRIVATE);

        //Get and setup the token for database storage
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Map<String, Object> tokenMap = new HashMap<String, Object>();
        tokenMap.put(refreshedToken, "<-- Token");

        //Get firebase reference and send the token to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("Accounts/" + sharedPrefs.getString("username", "") + "/Device Tokens");
        ref.setValue(tokenMap);


    }

}
