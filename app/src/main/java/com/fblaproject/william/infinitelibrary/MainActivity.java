package com.fblaproject.william.infinitelibrary;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

/*
* This activity is for the login page
* */

public class MainActivity extends AppCompatActivity {

    //Variable setup
    private Button submitButton;
    private EditText usernameEditText, passwordEditText;
    private DataSnapshot fbSnapshot;
    private SharedPreferences sharedPrefs;
    private ImageView iconTop, loadingScreen;
    private Account user;

    //Runs on start of activity
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Gets shared preferences so stored data (like the username) can be called
        sharedPrefs = getSharedPreferences("sharedPrefs", MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPrefs.edit();


        //Checks if the phone is connected to a network and displays a toast if not
        Utility.isNetworkConnected(MainActivity.this);


        //Element declaration
        submitButton = (Button) findViewById(R.id.buttonSubmit);
        usernameEditText = (EditText) findViewById(R.id.editTextUsername);
        passwordEditText = (EditText) findViewById(R.id.editTextPassword);
        iconTop = (ImageView)findViewById(R.id.imageLogo);
        loadingScreen = (ImageView)findViewById(R.id.loadingScreenImageView);

        //Disables elements while loading screen is showing
        submitButton.setVisibility(View.INVISIBLE);
        submitButton.setEnabled(false);
        usernameEditText.setVisibility(View.INVISIBLE);
        usernameEditText.setEnabled(false);
        passwordEditText.setVisibility(View.INVISIBLE);
        passwordEditText.setEnabled(false);
        iconTop.setVisibility(View.INVISIBLE);


        //Firebase Snapshot code
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference();
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                fbSnapshot = dataSnapshot;
                user = BackendFunctions.getAccount(fbSnapshot,sharedPrefs.getString("username", ""));


                //This if tree decides if a user is already logged in and skips the login page if they are
                if(!sharedPrefs.getString("username", "").equals("") && !sharedPrefs.getString("privateKey", "").equals("No Secret Key!!") && !sharedPrefs.getString("autoLogin", "").equals("false")) {
                    if(user.getPrivateKey().equals(sharedPrefs.getString("privateKey", ""))) {

                        //Starts home activity and closes the loading screen if a user is stored
                        Intent homeActivity = new Intent(getApplicationContext(), HomeActivity.class);
                        startActivity(homeActivity);

                    } else {

                        //Closes loading screen and re-enables the login screen elements if no user is stored
                        submitButton.setVisibility(View.VISIBLE);
                        submitButton.setEnabled(true);
                        usernameEditText.setVisibility(View.VISIBLE);
                        usernameEditText.setEnabled(true);
                        passwordEditText.setVisibility(View.VISIBLE);
                        passwordEditText.setEnabled(true);
                        iconTop.setVisibility(View.VISIBLE);
                        loadingScreen.setVisibility(View.GONE);
                    }
                } else {
                    //Closes loading screen and re-enables the login screen elements if no user is stored
                    submitButton.setVisibility(View.VISIBLE);
                    submitButton.setEnabled(true);
                    usernameEditText.setVisibility(View.VISIBLE);
                    usernameEditText.setEnabled(true);
                    passwordEditText.setVisibility(View.VISIBLE);
                    passwordEditText.setEnabled(true);
                    iconTop.setVisibility(View.VISIBLE);
                    loadingScreen.setVisibility(View.GONE);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });


        //Click listener for the login button
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Retrieve username and password from the edit text fields
                final String username = usernameEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                //Checks for empty fields
                if(username.equals("") || password.equals("")) {

                    //Shows toast if any empty fields
                    Toast toast = Toast.makeText(getApplicationContext(),"Enter all fields.", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.BOTTOM|Gravity.CENTER,0,50);
                    toast.show();

                } else {
                    //Logs in the account

                    //Firebase Authentication code
                    final FirebaseAuth mAuth = FirebaseAuth.getInstance();
                    FirebaseAuth.AuthStateListener mAuthListener;
                    mAuthListener = new FirebaseAuth.AuthStateListener() {
                        @Override
                        public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                        }
                    };
                    mAuth.addAuthStateListener(mAuthListener);

                    //Gets user account
                    user = BackendFunctions.getAccount(fbSnapshot,username);

                    //If user hasn't been deleted
                    if(!user.isDeleted()) {

                        //Removes this phone's token from all other users
                        BackendFunctions.removeTokenFromAccounts(fbSnapshot, FirebaseInstanceId.getInstance().getToken());


                        //Authenticates user account
                        mAuth.signInWithEmailAndPassword(username + "@dummy.com", password)
                                .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {

                                        if (task.isSuccessful()) {

                                            //Saves username and secret key for automatic login later
                                            editor.putString("username", username);
                                            editor.putString("privateKey", user.getPrivateKey());
                                            editor.apply();

                                            //Get device token so firebase can send user's phone notifications
                                            user.addDeviceToken(FirebaseInstanceId.getInstance().getToken());

                                            //Starts home activity
                                            Intent homeActivity = new Intent(getApplicationContext(), HomeActivity.class);
                                            startActivity(homeActivity);

                                        } else {

                                            //Shows toast if login failed
                                            Toast toast = Toast.makeText(getApplicationContext(),"Login failed.", Toast.LENGTH_SHORT);
                                            toast.setGravity(Gravity.BOTTOM|Gravity.CENTER,0,50);
                                            toast.show();

                                        }
                                    }
                                });

                    } else {

                        //Shows toast if user is deleted
                        Toast toast = Toast.makeText(getApplicationContext(),"This user has been deleted.", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.BOTTOM|Gravity.CENTER,0,50);
                        toast.show();
                    }

                }
            }
        });

    }

}
