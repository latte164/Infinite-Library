package com.fblaproject.william.infinitelibrary;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

/*
* This activity is the landing page when a user logs in.
* */

public class HomeActivity extends AppCompatActivity {

    //Variable setup
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private Button btnCheckout, btnReturn, btnWatchlist, btnSettings;
    private TextView txtWelcome;
    private Account userAccount;
    private DataSnapshot fbSnapshot;
    private SharedPreferences sharedPrefs;
    private String name;

    //Runs on start of activity
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //Gets shared preferences so stored data (like the username) can be called
        sharedPrefs = getSharedPreferences("sharedPrefs", MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPrefs.edit();


        //Checks if the phone is connected to a network and displays a toast if not
        Utility.isNetworkConnected(HomeActivity.this);


        //Sets up menu button in the top left
        mDrawerLayout = (DrawerLayout) findViewById(R.id.homeLayout);
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);
        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        //Element declaration
        btnCheckout = (Button) findViewById(R.id.buttonCheckout);
        btnReturn = (Button) findViewById(R.id.buttonReturn);
        btnWatchlist = (Button) findViewById(R.id.buttonWatchlist);
        btnSettings = (Button) findViewById(R.id.buttonSettings);


        //Navigates to new activity when an item in the menu is clicked
        final NavigationView navigationView = (NavigationView)findViewById(R.id.navigationViewHome);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                switch(menuItem.getItemId()){
                    case R.id.nav_logOut:

                        //Deletes the user from the stored data
                        editor.putString("username", "");
                        editor.putString("privateKey", "");
                        editor.apply();

                        //Remove device token so firebase doesn't send user's notifications
                        userAccount.removeDeviceToken(FirebaseInstanceId.getInstance().getToken());

                        //Starts the login activity
                        Intent mainActivity = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(mainActivity);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        break;

                    case R.id.nav_aboutUs:

                        //Starts the about activity
                        Intent aboutUsActivity = new Intent(getApplicationContext(), AboutActivity.class);
                        startActivity(aboutUsActivity);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        break;

                    case R.id.nav_map:

                        //Starts the map activity
                        Intent mapActivity = new Intent(getApplicationContext(), MapActivity.class);
                        startActivity(mapActivity);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        break;

                    case R.id.nav_reportBug:

                        //Starts an email client on the phone to send the bug report
                        Intent email = new Intent(Intent.ACTION_SEND);
                        email.putExtra(Intent.EXTRA_EMAIL, new String[]{"latte164studios@gmail.com"});
                        email.putExtra(Intent.EXTRA_SUBJECT, "INFINITE_LIBRARY-BUG_REPORT");
                        email.putExtra(Intent.EXTRA_TEXT, "Please do not change the subject line at all. " +
                                "You can remove all the default body text and replace it with an explanation of the problem you encountered. " +
                                "Thank you from the entire VFL Studios team.");
                        email.setType("message/rfc822");
                        startActivity(Intent.createChooser(email, "Please choose an email client."));
                        break;

                    case R.id.nav_adminPage:

                        //Starts the administration page
                        Intent adminActivity = new Intent(getApplicationContext(), AdministratorActivity.class);
                        startActivity(adminActivity);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        break;

                }
                return true;
            }
        });

        //Firebase Snapshot code
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference();
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                fbSnapshot = dataSnapshot;
                userAccount = BackendFunctions.getAccount(fbSnapshot, sharedPrefs.getString("username", ""));

                //Setup for the name at the top of the page
                txtWelcome = (TextView) findViewById(R.id.txtViewName);
                if(txtWelcome.getText().equals("Hello")) {
                    name = userAccount.getName();
                    txtWelcome.setText("Hello " + name);
                }

                //Removes access to admin settings if user is not an admin
                if(!userAccount.getAccountType().equals("Administrator")) navigationView.getMenu().removeItem(R.id.nav_adminPage);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });


        //Click listener for the checkout button
        btnCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Starts checkout activity and starts the sliding animation
                Intent checkoutActivity = new Intent(getApplicationContext(), SearchActivity.class);
                startActivity(checkoutActivity);
                overridePendingTransition(R.anim.right_slide_in, R.anim.right_slide_out);

            }
        });

        //Click listener for the return button
        btnReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Starts return activity and starts the sliding animation
                Intent returnActivity = new Intent(getApplicationContext(), ReturnActivity.class);
                startActivity(returnActivity);
                overridePendingTransition(R.anim.right_slide_in, R.anim.right_slide_out);

            }
        });

        //Click listener for the watchlist button
        btnWatchlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Starts watchlist activity and starts the sliding animation
                Intent watchlistActivity = new Intent(getApplicationContext(), WatchlistActivity.class);
                startActivity(watchlistActivity);
                overridePendingTransition(R.anim.right_slide_in, R.anim.right_slide_out);

            }
        });

        //Click listener for the settings button
        btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Starts settings activity and starts the sliding animation
                Intent settingsActivity = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(settingsActivity);
                overridePendingTransition(R.anim.right_slide_in, R.anim.right_slide_out);

            }
        });
    }

    //Called when a navigation menu item is selected
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(mToggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
