package com.fblaproject.william.infinitelibrary;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/*
* This activity is for the book search function. It takes a search string and returns the matching books in a list view.
* */

public class SearchActivity extends AppCompatActivity {

    //Variable setup
    private ListView listView;
    private EditText searchTermEditText;
    private DataSnapshot fbSnapshot;
    private List<Book> searchResults;
    ReturnBookAdapter bookAdapter;
    private boolean waitForData;

    //Runs on start of activity
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        //Creates back button in top left of screen
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Checks if the phone is connected to a network and displays a toast if not
        Utility.isNetworkConnected(SearchActivity.this);


        //Initialize variables
        searchTermEditText = (EditText)findViewById(R.id.editTextSearch);
        searchResults = new ArrayList<Book>();

        //List view and adapter setup
        listView = (ListView) findViewById(R.id.listViewCheckout);
        bookAdapter = new ReturnBookAdapter(getApplicationContext(), R.layout.return_row_layout);
        listView.setAdapter(bookAdapter);

        //Prevents user from searching before the Firebase Snapshot has been received
        waitForData = true;

        //Firebase Snapshot code
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("Books");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                fbSnapshot = dataSnapshot;
                waitForData = false;

                //Gets previous search term if there was one
                //This is used if, for example, the user types in "Green" and clicks a book.
                //When the user goes back, "Green" will be in the search edit text.
                Intent currentIntent = getIntent();
                if(currentIntent.hasExtra("searchTerm")) searchTermEditText.setText(currentIntent.getStringExtra("searchTerm"));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        //This list is what books fields to search for the term in
        final List<String> searchTypesList = Arrays.asList("Title", "Author", "ISBN");

        //Change listener for the search edit text (search bar)
        searchTermEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                //Checks to se if the Firebase Snapshot was received
                if(!waitForData) {

                    //Get the books from the search
                    searchResults = BackendFunctions.searchBooks(fbSnapshot, searchTermEditText.getText().toString(), searchTypesList);

                    //Clear adapter data
                    bookAdapter.wipe();

                    for (int i = 0; i < searchResults.size(); i++) {

                        //Assign data to the adapter variables
                        Bitmap cover = searchResults.get(i).getCover();
                        String title = searchResults.get(i).getTitle();
                        String author = searchResults.get(i).getAuthor();

                        //Add data to the adapter and set the list
                        ReturnBookDataProvider dataProvider = new ReturnBookDataProvider(title, author);
                        bookAdapter.add(dataProvider);
                    }

                    bookAdapter.notifyDataSetChanged();

                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //Click listener for list view objects
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //Opens the BookActivity for the selected book and stores necessary data for the book and back button
                Intent bookActiviy = new Intent(getApplicationContext(), BookActivity.class);
                bookActiviy.putExtra("bookID", searchResults.get((int)id).getISBN());
                bookActiviy.putExtra("searchTerm", searchTermEditText.getText().toString());
                bookActiviy.putExtra("returnTo", "search");
                startActivity(bookActiviy);
                overridePendingTransition(R.anim.right_slide_in, R.anim.right_slide_out);

            }
        });


        //Open/Close keyboard - closes/opens depending on what view is in focus (opens if edit text, closes if list view).
        searchTermEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {

                if (view == searchTermEditText) {
                    if (hasFocus) {
                        ((InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInput(searchTermEditText, InputMethodManager.SHOW_FORCED);
                    } else {
                        ((InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(searchTermEditText.getWindowToken(), 0);
                    }
                }

            }
        });
        //Open/Close Keyboard #2 - Closes on list view scroll
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                ((InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(searchTermEditText.getWindowToken(), 0);
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
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