package com.fblaproject.william.infinitelibrary;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is a search adapter. It holds and passes the data for the DataProvider.
 */

public class SearchBookAdapter extends ArrayAdapter{

    //Variable declarations
    List bookList = new ArrayList<>();
    static class DataHandler {
        ImageView cover;
        TextView title, author;
    }

    //Setup for the adapter
    public SearchBookAdapter(@NonNull Context context, @LayoutRes int resource) {
        super(context, resource);
    }

    //Adds data to the adapter
    @Override
    public void add(@Nullable Object object) {
        super.add(object);
        bookList.add(object);
    }

    //Clears all data in the adapter
    public void wipe(){
        super.clear();
        bookList.clear();
    }

    //Returns number of books in list
    @Override
    public int getCount() {
        return this.bookList.size();
    }

    //Returns item at a position
    @Nullable
    @Override
    public Object getItem(int position) {
        return this.bookList.get(position);
    }

    //Sends data to handler
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        //Variable declaration
        View row;
        DataHandler dataHandler;

        row = convertView;

        //Checks if row is null
        if(convertView == null) {
            //If null, setup layout and data handler

            LayoutInflater inflater = (LayoutInflater)this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.search_row_layout, parent, false);

            dataHandler = new DataHandler();
            dataHandler.cover = (ImageView) row.findViewById(R.id.cover);
            dataHandler.title = (TextView) row.findViewById(R.id.firstLine);
            dataHandler.author = (TextView) row.findViewById(R.id.secondLine);

            row.setTag(dataHandler);
        } else {
            //If not null, get tag for dataHandler

            dataHandler = (DataHandler) row.getTag();
        }

        //Set data for the handler
        SearchBookDataProvider dataProvider = (SearchBookDataProvider) this.getItem(position);
        dataHandler.cover.setImageBitmap(dataProvider.getBookImages());
        dataHandler.title.setText(dataProvider.getBookTitles());
        dataHandler.author.setText(dataProvider.getBookAuthors());

        return row;
    }
}
