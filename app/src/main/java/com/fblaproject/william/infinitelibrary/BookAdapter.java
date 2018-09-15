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

public class BookAdapter extends ArrayAdapter{

    List bookList = new ArrayList<>();

    static class DataHandler {

        ImageView cover;
        TextView title, author;

    }

    public BookAdapter(@NonNull Context context, @LayoutRes int resource) {
        super(context, resource);
    }

    @Override
    public void add(@Nullable Object object) {
        super.add(object);
        bookList.add(object);
    }

    public void wipe(){
        super.clear();
        bookList.clear();
    }

    @Override
    public int getCount() {
        return this.bookList.size();
    }

    @Nullable
    @Override
    public Object getItem(int position) {
        return this.bookList.get(position);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View row;
        DataHandler dataHandler;

        row = convertView;

        if(convertView == null) {

            LayoutInflater inflater = (LayoutInflater)this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.search_row_layout, parent, false);

            dataHandler = new DataHandler();
            dataHandler.cover = (ImageView) row.findViewById(R.id.cover);
            dataHandler.title = (TextView) row.findViewById(R.id.firstLine);
            dataHandler.author = (TextView) row.findViewById(R.id.secondLine);

            row.setTag(dataHandler);
        } else {
            dataHandler = (DataHandler) row.getTag();
        }
        BookDataProvider dataProvider = (BookDataProvider) this.getItem(position);
        dataHandler.cover.setImageBitmap(dataProvider.getBookImages());
        //dataHandler.cover.setImageAlpha(dataProvider.getBookImages());
        dataHandler.title.setText(dataProvider.getBookTitles());
        dataHandler.author.setText(dataProvider.getBookAuthors());

        return row;
    }
}
