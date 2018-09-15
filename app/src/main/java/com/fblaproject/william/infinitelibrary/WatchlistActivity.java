package com.fblaproject.william.infinitelibrary;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.List;

/*
* This activity is for the watch list page where one can view the books in the watchlist, go to the books page, and remove it from the watchlist.
* */

public class WatchlistActivity extends AppCompatActivity {

    //Variable setup
    private ListView listView;
    private ReturnBookAdapter bookAdapter;
    private Account user;
    private SharedPreferences sharedPrefs;
    private DataSnapshot fbSnapshot;
    private String[] bookTitles, bookAuthors;
    private List<Book> watchlistBooks;

    //Runs on start of activity
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watchlist);

        //Creates back button in top left of screen
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Gets shared preferences so stored data (like the username) can be called
        sharedPrefs = getSharedPreferences("sharedPrefs", MODE_PRIVATE);

        //Checks if the phone is connected to a network and displays a toast if not
        Utility.isNetworkConnected(WatchlistActivity.this);

        //Setup for the list view and adapter / provider
        listView = (ListView) findViewById(R.id.watchlistListView);
        bookAdapter = new ReturnBookAdapter(getApplicationContext(), R.layout.return_row_layout);
        listView.setAdapter(bookAdapter);

        //Firebase Snapshot code
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference();
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                fbSnapshot = dataSnapshot;
                user = BackendFunctions.getAccount(fbSnapshot, sharedPrefs.getString("username", ""));

                //Set data for adapter and show books in list view
                setAdapterData();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });


        //Click listener for list view objects
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                //Create dialog popup to ask the user whether they want to view the book or remove the book
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(WatchlistActivity.this);
                dialogBuilder.setCancelable(true);
                dialogBuilder.setTitle("Watchlist");
                dialogBuilder.setMessage("Would you like to remove or view " + user.getWatchlist().get(position).getTitle() + "? Tap off this box to close it.");
                dialogBuilder.setNegativeButton("Remove", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //If remove selected

                        //Remove book from watchlist, reset adapter, close dialog box.
                        user.removeFromWatchlist(watchlistBooks.get(position));
                        setAdapterData();
                        dialog.cancel();

                    }
                });
                dialogBuilder.setPositiveButton("View", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //If view selected

                        //Starts book activity and starts the sliding animation
                        Intent bookActiviy = new Intent(getApplicationContext(), BookActivity.class);
                        bookActiviy.putExtra("bookID", watchlistBooks.get(position).getISBN());
                        bookActiviy.putExtra("returnTo", "watchlist");
                        startActivity(bookActiviy);
                        overridePendingTransition(R.anim.right_slide_in, R.anim.right_slide_out);

                    }
                });
                dialogBuilder.show();
            }
        });

    }


    //Called when setting or updating the list view books
    private void setAdapterData(){

        //Get list size (numberOfBooks) and add all the book objects to a list (watchlistBooks)
        int numberOfBooks = user.getWatchlist().size();
        watchlistBooks = user.getWatchlist();

        //Set up adapter variables
        bookTitles = new String[numberOfBooks];
        bookAuthors = new String[numberOfBooks];

        //Assign data to the above adapter variables
        for(int i = 0; i < user.getWatchlist().size(); i++) {

            //If not checked out or reserved, then add an "(Available)" tag
            if(!user.getWatchlist().get(i).isCheckedOut() && !user.getWatchlist().get(i).isReserved()) {
                //Available

                bookTitles[i] = watchlistBooks.get(i).getTitle() + " (Aval.)";
            } else {
                //Available

                bookTitles[i] = watchlistBooks.get(i).getTitle();
            }

            bookAuthors[i] = watchlistBooks.get(i).getAuthor();
        }

        //Clear adapter data
        bookAdapter.wipe();

        //Add data to the adapter
        for(int i = 0; i < numberOfBooks; i++) {
            ReturnBookDataProvider dataProvider = new ReturnBookDataProvider(bookTitles[i], bookAuthors[i]);
            bookAdapter.add(dataProvider);
        }

        //Set list view from adapter
        bookAdapter.notifyDataSetChanged();

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
