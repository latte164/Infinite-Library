package com.fblaproject.william.infinitelibrary;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
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

import static android.support.constraint.R.id.parent;
import static com.facebook.FacebookSdk.getApplicationContext;

/*
* Activity for the administrator page to add/remove books or users
* */

public class AdministratorActivity extends AppCompatActivity {

    private Button addBook, removeBook, addUser, removeUser, addUserConfirm, addBookConfirm, addUserCancel, addBookCancel;
    private DataSnapshot fbSnapshot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_administrator);

        //Creates back button in top left of screen
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Checks if the phone is connected to a network and displays a toast if not
        Utility.isNetworkConnected(AdministratorActivity.this);

        //Element declaration
        addBook = (Button)findViewById(R.id.btnAddBook);
        removeBook = (Button)findViewById(R.id.btnRemoveBook);
        addUser = (Button)findViewById(R.id.btnAddUser);
        removeUser = (Button)findViewById(R.id.btnRemoveUser);
        addUserConfirm = (Button)findViewById(R.id.btnAddUserConfirm);
        addUserCancel = (Button)findViewById(R.id.btnAddUserCancel);
        addBookConfirm = (Button)findViewById(R.id.btnAddBookConfirm);
        addBookCancel = (Button)findViewById(R.id.btnAddBookCancel);


        //Firebase Snapshot code
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference();
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                fbSnapshot = dataSnapshot;

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });


        //Click listener for the add book button
        addBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Create animation and listener to make previous layout invisible
                Animation animation = AnimationUtils.loadAnimation(AdministratorActivity.this, R.anim.slide_up);
                animation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        findViewById(R.id.mainAdminLayout).setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });

                //Get view and start animation
                View item = findViewById(R.id.addBookAdminLayout);
                item.setVisibility(View.VISIBLE);
                item.startAnimation(animation);

            }
        });


        //Click listener for the add book confirm button
        addBookConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Element setup
                EditText editTextTitle, editTextAuthor, editTextDescription, editTextISBN, editTextDewey, editTextPublisher, editTextDate, editTextPlace, editTextCover;
                String title, author, description, isbn, dewey, publisher, date, place, cover;

                //Element declaration
                editTextTitle = (EditText)findViewById(R.id.edtTextAddBookTitle);
                editTextAuthor = (EditText)findViewById(R.id.edtTextAddBookAuthor);
                editTextDescription = (EditText)findViewById(R.id.edtTextAddBookDescription);
                editTextISBN = (EditText)findViewById(R.id.edtTextAddBookISBN);
                editTextDewey = (EditText)findViewById(R.id.edtTextAddBookDewey);
                editTextPublisher = (EditText)findViewById(R.id.edtTextAddBookPublisher);
                editTextDate = (EditText)findViewById(R.id.edtTextAddBookDate);
                editTextPlace = (EditText)findViewById(R.id.edtTextAddBookLocation);
                editTextCover = (EditText)findViewById(R.id.edtTextAddBookCover);

                //Get text from elements
                title = editTextTitle.getText().toString();
                author = editTextAuthor.getText().toString();
                description = editTextDescription.getText().toString();
                isbn = editTextISBN.getText().toString();
                dewey = editTextDewey.getText().toString();
                publisher = editTextPublisher.getText().toString();
                date = editTextDate.getText().toString();
                place = editTextPlace.getText().toString();
                cover = editTextCover.getText().toString();

                //Check for empty fields
                if(publisher.equals("")){
                    publisher = "N.p.";
                }
                if(date.equals("")){
                    date = "n.d.";
                }
                if(place.equals("")){
                    place = "n.p.";
                }
                if(title.equals("") || author.equals("") || description.equals("") || isbn.equals("") || dewey.equals("") || cover.equals("")){

                    //Create toast to tell user to fill out all fields
                    Toast toast = Toast.makeText(getApplicationContext(),"Please fill in all fields.", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.BOTTOM|Gravity.CENTER,0,50);
                    toast.show();

                } else {

                    BackendFunctions.addBook(title, author, description, isbn, dewey, cover, publisher, date, place);

                    //Close the add book view
                    Animation animation = AnimationUtils.loadAnimation(AdministratorActivity.this, R.anim.slide_down);
                    animation.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                            findViewById(R.id.mainAdminLayout).setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            findViewById(R.id.addBookAdminLayout).setVisibility(View.GONE);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                    View item = findViewById(R.id.addBookAdminLayout);
                    item.startAnimation(animation);

                }

            }
        });

        //Click listener for the add book cancel button
        addBookCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Create animation and listener to make previous layout invisible
                Animation animation = AnimationUtils.loadAnimation(AdministratorActivity.this, R.anim.slide_down);
                animation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        findViewById(R.id.mainAdminLayout).setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        findViewById(R.id.addBookAdminLayout).setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });

                //Get view and start animation
                View item = findViewById(R.id.addBookAdminLayout);
                item.startAnimation(animation);

            }
        });

        //Click listener for the remove book button
        removeBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Edit text for removing the book
                final EditText editText = new EditText(getApplicationContext());
                editText.setBackgroundColor(Color.GRAY);
                editText.setHint("ISBN");
                editText.setHintTextColor(Color.WHITE);
                editText.setPadding(48,0,48,0);

                //Create dialog popup to get the ISBN of the book to remove
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(AdministratorActivity.this);
                dialogBuilder.setCancelable(false);
                dialogBuilder.setTitle("Remove a Book");
                dialogBuilder.setMessage("Enter the book's ISBN");
                dialogBuilder.setView(editText);
                dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //If cancel selected
                        dialog.cancel();

                    }
                });
                dialogBuilder.setPositiveButton("Remove", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //If remove selected

                        //Get ISBN and remove the book
                        String isbn = editText.getText().toString().replace("-", "");
                        BackendFunctions.removeBook(isbn);
                    }
                });
                dialogBuilder.show();

            }
        });

        //Click listener for the add user button
        addUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Create animation and listener to make previous layout invisible
                Animation animation = AnimationUtils.loadAnimation(AdministratorActivity.this, R.anim.slide_up);
                animation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        findViewById(R.id.mainAdminLayout).setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });

                //Get view and start animation
                View item = findViewById(R.id.addUserAdminLayout);
                item.setVisibility(View.VISIBLE);
                item.startAnimation(animation);

                //Populate the account type spinner
                Spinner spin = (Spinner)findViewById(R.id.spinnerAddUserAccountType);
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(AdministratorActivity.this, android.R.layout.simple_spinner_item, new String[] {"Standard", "Administrator"});
                spin.setAdapter(adapter);

            }


        });

        //Click listener for the remove user button
        removeUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Edit text for removing the user
                final EditText editText = new EditText(getApplicationContext());
                editText.setBackgroundColor(Color.GRAY);
                editText.setHint("Username");
                editText.setHintTextColor(Color.WHITE);
                editText.setPadding(48,0,48,0);

                //Create dialog popup to get the username to remove
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(AdministratorActivity.this);
                dialogBuilder.setCancelable(false);
                dialogBuilder.setTitle("Remove a User");
                dialogBuilder.setMessage("Enter the username");
                dialogBuilder.setView(editText);
                dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //If cancel selected
                        dialog.cancel();

                    }
                });
                dialogBuilder.setPositiveButton("Remove", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //If remove selected

                        //Get username and remove the user
                        String username = editText.toString();
                        BackendFunctions.removeAccount(fbSnapshot.child("Accounts"), username);
                    }
                });
                dialogBuilder.show();

            }
        });

        //Click listener for the add user confirmation button
        addUserConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Element setup
                EditText editTextUsername, editTextName, editTextPassword;
                Spinner spinnerAccountType;
                final String username, name, password, accountType;

                //Element declaration
                editTextUsername = (EditText)findViewById(R.id.edtTextAddUserUsername);
                editTextName = (EditText)findViewById(R.id.edtTextAddUserUsersName);
                editTextPassword = (EditText)findViewById(R.id.edtTextAddUserPassword);
                spinnerAccountType = (Spinner)findViewById(R.id.spinnerAddUserAccountType);

                //Get text from elements
                username = editTextUsername.getText().toString();
                name = editTextName.getText().toString();
                password = editTextPassword.getText().toString();
                accountType = spinnerAccountType.getSelectedItem().toString();

                //Checks if all fields are filled out
                if(username.equals("") || name.equals("") || password.equals("") || accountType.equals("")) {

                    //Create toast to tell user to fill out all fields
                    Toast toast = Toast.makeText(getApplicationContext(),"Please fill in all fields.", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.BOTTOM|Gravity.CENTER,0,50);
                    toast.show();

                } else {
                    //Create account

                    //Firebase Authentication code
                    final FirebaseAuth mAuth = FirebaseAuth.getInstance();
                    FirebaseAuth.AuthStateListener mAuthListener;
                    mAuthListener = new FirebaseAuth.AuthStateListener() {
                        @Override
                        public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                        }
                    };
                    mAuth.addAuthStateListener(mAuthListener);

                    //Add account to the Firebase authentication database
                    mAuth.createUserWithEmailAndPassword(username + "@dummy.com", password)
                            .addOnCompleteListener(AdministratorActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if(task.isSuccessful()) {

                                //Add account to the database
                                BackendFunctions.addAccount(username,name,accountType);

                                //Close the add user view
                                Animation animation = AnimationUtils.loadAnimation(AdministratorActivity.this, R.anim.slide_down);
                                animation.setAnimationListener(new Animation.AnimationListener() {
                                    @Override
                                    public void onAnimationStart(Animation animation) {
                                        findViewById(R.id.mainAdminLayout).setVisibility(View.VISIBLE);
                                    }

                                    @Override
                                    public void onAnimationEnd(Animation animation) {
                                        findViewById(R.id.addUserAdminLayout).setVisibility(View.GONE);
                                    }

                                    @Override
                                    public void onAnimationRepeat(Animation animation) {

                                    }
                                });
                                View item = findViewById(R.id.addUserAdminLayout);
                                item.startAnimation(animation);

                            } else {

                                //Create toast to tell user the creation failed
                                Toast toast = Toast.makeText(getApplicationContext(),"Error creating account.", Toast.LENGTH_LONG);
                                toast.setGravity(Gravity.BOTTOM|Gravity.CENTER,0,50);
                                toast.show();

                            }

                        }
                    });

                }

            }
        });

        //Click listener for the add user cancel button
        addUserCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Create animation and listener to make previous layout invisible
                Animation animation = AnimationUtils.loadAnimation(AdministratorActivity.this, R.anim.slide_down);
                animation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        findViewById(R.id.mainAdminLayout).setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        findViewById(R.id.addUserAdminLayout).setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });

                //Get view and start animation
                View item = findViewById(R.id.addUserAdminLayout);
                item.startAnimation(animation);

            }
        });

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
