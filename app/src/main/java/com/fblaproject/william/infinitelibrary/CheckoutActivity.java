package com.fblaproject.william.infinitelibrary;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ListView;

public class CheckoutActivity extends AppCompatActivity {

    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        //back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        //get number of books
        int numberOfBooks = 0;

        //listview initialization
        listView = (ListView) findViewById(R.id.listViewCheckout);

        //set variables for adapter
        int[] bookImages = new int[numberOfBooks];
        String[] bookTitles = new String[numberOfBooks];
        String[] bookAuthors = new String[numberOfBooks];

    }

    //called when back button is pressed
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                Intent mainActivity = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(mainActivity);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
