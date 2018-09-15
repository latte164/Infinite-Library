package com.fblaproject.william.infinitelibrary;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

/*
* This activity displays the library map
* */

public class MapActivity extends AppCompatActivity {

    //Runs on start of activity
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        //Creates back button in top left of screen
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Checks if the phone is connected to a network and displays a toast if not
        Utility.isNetworkConnected(MapActivity.this);

    }


    //Called when back button is pressed
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:

                //Starts home activity and starts the sliding animation
                Intent homeActivity = new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(homeActivity);
                overridePendingTransition(R.anim.left_slide_in, R.anim.left_slide_out);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
