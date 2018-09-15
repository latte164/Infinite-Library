package com.fblaproject.william.infinitelibrary;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.Switch;

/*
* This activity is for the settings page. It mostly contains switch and click listeners.
* */

public class SettingsActivity extends AppCompatActivity {

    //Variable setup
    private Switch loginSwitch;
    private SharedPreferences sharedPrefs;

    //Runs on start of activity
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        //Creates back button in top left of screen
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Gets shared preferences so stored data (like the username) can be called
        sharedPrefs = getSharedPreferences("sharedPrefs", MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPrefs.edit();

        //Checks if the phone is connected to a network and displays a toast if not
        Utility.isNetworkConnected(SettingsActivity.this);

        //Element declaration
        loginSwitch = (Switch)findViewById(R.id.settingsLoginSwitch);


        //Checks for the user's settings choices and sets the elements to those preferences
        if(sharedPrefs.getString("autoLogin", "").equals("false")) loginSwitch.setChecked(false);


        //Click listener for the automatic login switch
        //If false, the user will have to login everytime they open the app
        loginSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked) {
                    //Switch on

                    editor.putString("autoLogin", "true");
                } else {
                    //Switch off

                    editor.putString("autoLogin", "false");
                }

                editor.apply();
            }
        });
    }


    //Called when back button is pressed
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:

                //Starts home activity and starts the sliding animation
                Intent mainActivity = new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(mainActivity);
                overridePendingTransition(R.anim.left_slide_in, R.anim.left_slide_out);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
