package com.fblaproject.william.infinitelibrary;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import java.util.concurrent.TimeUnit;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.evernote.android.job.JobManager;
import com.evernote.android.job.JobRequest;
import com.facebook.CallbackManager;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;

/*
* This activity is for the book page where one can checkout, add to watchlist, or add to reserve list.
* It sets text/image fields, gets ratings, and has listeners for the buttons and social media integration.
* */

public class BookActivity extends AppCompatActivity {

    //Variable setup
    private DataSnapshot fbSnapshot;
    private String bookID;
    private TextView titleTextView, authorTextView, isbnTextView, deweyTextView, ratingTextView, descriptionTextView, subtitleTextView;
    private ImageView coverImageView;
    private Button checkoutButton, reserveButton, watchlistButton, citationButton;
    private ImageButton tweetButton, facebookButton;
    private RatingBar ratingBar;
    private SharedPreferences sharedPrefs;
    private SharedPreferences.Editor editor;
    private Book bookObj;
    private Account user;
    private CallbackManager callbackManager;
    private ShareLinkContent content;

    //Runs on start of activity
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppEventsLogger.activateApp(BookActivity.this); //facebook setup
        setContentView(R.layout.activity_book);

        //Setup for Evernote Android-Job for creating the overdue book notifications
        JobManager.create(BookActivity.this).addJobCreator(new AndroidJobCreator());

        //Gets shared preferences so stored data (like the username) can be called
        sharedPrefs = getSharedPreferences("sharedPrefs", MODE_PRIVATE);
        editor = sharedPrefs.edit();

        //Creates back button in top left of screen
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Checks if the phone is connected to a network and displays a toast if not
        Utility.isNetworkConnected(BookActivity.this);


        //Element declaration
        checkoutButton = (Button)findViewById(R.id.buttonBookCheckout);
        reserveButton = (Button)findViewById(R.id.buttonBookReserve);
        watchlistButton = (Button)findViewById(R.id.buttonBookWatchlist);
        ratingBar = (RatingBar)findViewById(R.id.ratingBarBook);
        tweetButton = (ImageButton)findViewById(R.id.tweetButtonBooks);
        facebookButton = (ImageButton)findViewById(R.id.facebookButtonBooks);
        citationButton = (Button)findViewById(R.id.buttonBookCitation);

        //Disables buttons and sets text as loading until the Firebase Snapshot is received
        checkoutButton.setEnabled(false);
        reserveButton.setEnabled(false);
        watchlistButton.setEnabled(false);
        checkoutButton.setText("Loading...");
        reserveButton.setText("Loading...");
        watchlistButton.setText("Loading...");

        //Firebase Snapshot code
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference();
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                fbSnapshot = dataSnapshot;

                //Setup for Book Object, User, and Activity Elements once Snapshot is received
                user = BackendFunctions.getAccount(fbSnapshot,sharedPrefs.getString("username", ""));
                setBookFieldData();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //Click listener for the checkout button
        checkoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Checks if user has too many books checked out
                if(user.getAccountType().equals("Administrator") || user.getBooksCheckedOut().size() < 2) {

                    //Checks out book to user
                    user.checkOutBook(bookObj);

                    //Disables buttons on checkout
                    checkoutButton.setText("Checked Out");
                    checkoutButton.setEnabled(false);
                    reserveButton.setEnabled(false);
                    watchlistButton.setEnabled(false);

                    //Unreserves the book if it was reserved by the user
                    for(int i = 0; i < user.getReservedBooks().size(); i++) {
                        if(user.getReservedBooks().get(i).equals(bookObj)) {
                            user.unreserveBook(bookObj);
                        }
                    }

                    //Calls function to set the overdue notification
                    setNotification();

                } else {

                    //Tell the user they have reached their checkout limit
                    final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(BookActivity.this);
                    dialogBuilder.setCancelable(false);
                    dialogBuilder.setTitle("Limit Reached");
                    dialogBuilder.setMessage("One can only have 2 books checked out at the same time.");
                    dialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        }
                    });
                    dialogBuilder.show();

                }
            }
        });

        //Click listener for the reserve button
        reserveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Reserves book to user
                user.reserveBook(bookObj);

                //Disables buttons on reserve
                reserveButton.setText("Reserved");
                reserveButton.setEnabled(false);
                watchlistButton.setEnabled(false);
            }
        });

        //Click listener for the watchlist button
        watchlistButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Adds book to user's watchlist
                user.addToWatchlist(bookObj);

                //Disables buttons on watchlist
                watchlistButton.setText("Watchlisted");
                watchlistButton.setEnabled(false);
            }
        });

        //Click listener for the Twitter share button
        tweetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //If tree decides what message to tweet depending on the rating
                if(ratingBar.getRating() <= 2.5 && ratingBar.getRating() > 0) {

                    //Twitter syntax - runs if rated badly
                    TweetComposer.Builder builder = new TweetComposer.Builder(BookActivity.this)
                            .text("I just rated " + bookObj.getTitle() + " a " + String.valueOf(ratingBar.getRating()) + "/5. I would steer clear!");
                    builder.show();

                } else if (ratingBar.getRating() > 0){

                    //Twitter syntax - runs if rated well
                    TweetComposer.Builder builder = new TweetComposer.Builder(BookActivity.this)
                            .text("I just rated " + bookObj.getTitle() + " a " + String.valueOf(ratingBar.getRating()) + "/5. I would recommend giving it a read!");
                    builder.show();

                } else {

                    //Twitter syntax - runs if no rating
                    TweetComposer.Builder builder = new TweetComposer.Builder(BookActivity.this)
                            .text("You should give " + bookObj.getTitle() + " a read!");
                    builder.show();
                }
            }
        });

        //Click listener for the Facebook share button
        facebookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //If tree decides what message to share depending on the rating
                if(ratingBar.getRating() <= 2.5 && ratingBar.getRating() > 0) {

                    //Facebook syntax - runs if rated badly
                    content = new ShareLinkContent.Builder()
                            .setQuote("I just rated " + bookObj.getTitle() + " a " + String.valueOf(ratingBar.getRating()) + "/5. I would steer clear!")
                            .build();

                } else if(ratingBar.getRating() > 0) {

                    //Facebook syntax - runs if rated well
                    content = new ShareLinkContent.Builder()
                            .setQuote("I just rated " + bookObj.getTitle() + " a " + String.valueOf(ratingBar.getRating()) + "/5. I would recommend giving it a read!")
                            .build();

                } else {

                    //Facebook syntax - runs if no rating
                    content = new ShareLinkContent.Builder()
                            .setQuote("You should give " + bookObj.getTitle() + " a read!")
                            .build();
                }

                //More facebook syntax
                callbackManager = CallbackManager.Factory.create();
                ShareDialog shareDialog = new ShareDialog(BookActivity.this);
                shareDialog.show(content);
            }
        });

        //Click listener for the citation button
        citationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Create string for citation
                String author;
                String[] authorSplit = bookObj.getAuthor().split(" ");

                if(authorSplit.length > 2) {

                    author = authorSplit[authorSplit.length-1];

                    for(int i = 0; i < (authorSplit.length - 1); i++) {
                        author += authorSplit[i];
                    }
                } else {
                    author = bookObj.getAuthor();
                }

                String citation;
                citation = author + ". <i>" + bookObj.getTitle() + "</i>. " + bookObj.getPublicationLocation() + ": " + bookObj.getPublisher() + ", " + bookObj.getDatePublished() + ". " + "Print.";

                //Add the citation to the phone clipboard so the user can paste it later
                ClipboardManager clipboardManager = (ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText("MLA7 Book Citation", citation);
                clipboardManager.setPrimaryClip(clipData);

                //Toast to tell the user that the citation has been added to their clipboard
                Toast toast = Toast.makeText(getApplicationContext(),"Citation Copied", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.BOTTOM|Gravity.CENTER,0,50);
                toast.show();


            }
        });

    }


    //More facebook sharing syntax & runs after message is shared
    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }


    //Called when Firebase Snapshot is received & sets page Elements
    private void setBookFieldData() {

        //Element and Book Object initialization
        bookID = getIntent().getStringExtra("bookID");
        bookObj = BackendFunctions.getBookFromISBN(fbSnapshot.child("Books"),bookID);
        titleTextView = (TextView)findViewById(R.id.textViewBookTitle);
        authorTextView = (TextView)findViewById(R.id.textViewBookAuthor);
        isbnTextView = (TextView)findViewById(R.id.textViewBookISBN);
        deweyTextView = (TextView)findViewById(R.id.textViewBookDewey);
        ratingTextView = (TextView)findViewById(R.id.textViewBookRating);
        descriptionTextView = (TextView)findViewById(R.id.textViewBookDescription);
        coverImageView = (ImageView)findViewById(R.id.imageViewBookCover);
        subtitleTextView = (TextView)findViewById(R.id.textViewBookSubtitle);

        //Sets page element text & enables/disables buttons based on state of the book
        if(!bookObj.getTitle().isEmpty())  {
            if(bookObj.getTitle().contains(";:")) {
                String[] titles = bookObj.getTitle().split(";:");

                titleTextView.setText(titles[0]);
                subtitleTextView.setText(titles[1]);
            } else {
                titleTextView.setText(bookObj.getTitle());
            }
        }
        if(!bookObj.getAuthor().isEmpty()) authorTextView.setText(bookObj.getAuthor());
        if(!bookObj.getISBN().isEmpty()) isbnTextView.append(bookObj.getISBN());
        if(!bookObj.getDeweyIndexNumber().isEmpty()) deweyTextView.append(bookObj.getDeweyIndexNumber());
        if(bookObj.getRating() < 0) {
            ratingTextView.setText("No Ratings");
        } else {
            ratingTextView.append(String.valueOf(bookObj.getRating()) + "/5.0");
        }
        if(!bookObj.getDescription().isEmpty()) descriptionTextView.append(Html.fromHtml(bookObj.getDescription()));
        coverImageView.setImageBitmap(bookObj.getCover());
        checkoutButton.setText("Checkout");
        checkoutButton.setEnabled(true);
        reserveButton.setText("Reserve");
        reserveButton.setEnabled(true);
        watchlistButton.setText("Watchlist");
        watchlistButton.setEnabled(true);

        if(bookObj.isCheckedOut()) {
            checkoutButton.setText("Checked Out");
            checkoutButton.setBackgroundColor(Color.parseColor("#636363"));
            checkoutButton.setEnabled(false);

            reserveButton.setText("Checked Out");
            reserveButton.setBackgroundColor(Color.parseColor("#636363"));
            reserveButton.setEnabled(false);

        } else if(bookObj.isReserved()) {

            if(!bookObj.isCheckedOut() && !bookObj.getReserver().equals(user.getName())) {
                checkoutButton.setText("Reserved");
                checkoutButton.setBackgroundColor(Color.parseColor("#636363"));
                checkoutButton.setEnabled(false);
            }

            reserveButton.setText("Reserved");
            reserveButton.setBackgroundColor(Color.parseColor("#636363"));
            reserveButton.setEnabled(false);

        }

        for(int i = 0; i < user.getWatchlist().size(); i++) {

            if(user.getWatchlist().get(i).getTitle().equals(bookObj.getTitle())) {
                watchlistButton.setText("Watchlisted");
                watchlistButton.setEnabled(false);
            }
        }

    }


    //Called when the book is checked out to set up overdue notification
    private void setNotification() {

        //Creates new job using Evernote Android-Job to display overdue book notifications
        int jobID = new JobRequest.Builder(OverdueBookJob.JOB_TAG)
                .setExecutionWindow(TimeUnit.DAYS.toMillis(12), TimeUnit.DAYS.toMillis(14))
                .build()
                .schedule();

        //Add the jobID to the shared preferences so it can be canceled later if necessary
        editor.putString(bookObj.getISBN() + "-job", String.valueOf(jobID));
        editor.apply();

    }


    //Called when back button is pressed
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:

                //Gets shared preferences to call stored data (like username)
                SharedPreferences sharedPrefs = getSharedPreferences("sharedPrefs", MODE_PRIVATE);
                final SharedPreferences.Editor editor = sharedPrefs.edit();

                //Ratings bar Element initialization
                ratingBar = (RatingBar)findViewById(R.id.ratingBarBook);

                //If rating has been selected and the user has not set a rating before
                if(ratingBar.getRating() > 0 && (sharedPrefs.getString("userRating" + bookObj.getISBN(),"").equals("false") || sharedPrefs.getString("userRating" + bookObj.getISBN(),"").isEmpty())) {

                    //Sets new ratings
                    bookObj.addRating(ratingBar.getRating());

                    //Adds a shared preference so the user can't rate again
                    editor.putString("userRating" + bookObj.getISBN(), "true");
                    editor.apply();

                }


                //Decides which function to return to
                if(getIntent().hasExtra("returnTo") && getIntent().getStringExtra("returnTo").equals("search")) {

                    //Starts search activity and starts the sliding animation
                    Intent searchActivity = new Intent(getApplicationContext(), SearchActivity.class);
                    searchActivity.putExtra("searchTerm", getIntent().getStringExtra("searchTerm"));
                    startActivity(searchActivity);
                    overridePendingTransition(R.anim.left_slide_in, R.anim.left_slide_out);
                    return true;

                } else if(getIntent().hasExtra("returnTo") && getIntent().getStringExtra("returnTo").equals("watchlist")) {

                    //Starts watchlist activity and starts the sliding animation
                    Intent watchlistActivity = new Intent(getApplicationContext(), WatchlistActivity.class);
                    startActivity(watchlistActivity);
                    overridePendingTransition(R.anim.left_slide_in, R.anim.left_slide_out);
                    return true;

                } else {

                    //Starts home activity and starts the sliding animation
                    Intent homeActivity = new Intent(getApplicationContext(), HomeActivity.class);
                    startActivity(homeActivity);
                    overridePendingTransition(R.anim.left_slide_in, R.anim.left_slide_out);
                    return true;
                }
        }
        return super.onOptionsItemSelected(item);
    }
}
