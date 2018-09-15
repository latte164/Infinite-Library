package com.fblaproject.william.infinitelibrary;

import android.graphics.Bitmap;

/**
 * This class is a search data provider. It receives data from the adapter and formats it for the list view.
 */

public class SearchBookDataProvider {

    //Initialize variables
    private Bitmap bookImages;
    private String bookTitles, bookAuthors;

    //Setup for the data provider
    public SearchBookDataProvider(Bitmap bookImages, String bookTitles, String bookAuthors) {

        this.setBookAuthors(bookAuthors);
        this.setBookImages(bookImages);
        this.setBookTitles(bookTitles);
    }

    //Returns book images bitmap
    public Bitmap getBookImages() { return bookImages; }

    //Sets book images bitmap
    public void setBookImages(Bitmap bookImages) {
        this.bookImages = bookImages;
    }

    //Returns book titles string
    public String getBookTitles() {
        return bookTitles;
    }

    //Sets book titles string
    public void setBookTitles(String bookTitles) {
        this.bookTitles = bookTitles;
    }

    //Returns book authors string
    public String getBookAuthors() {
        return bookAuthors;
    }

    //Sets book authors string
    public void setBookAuthors(String bookAuthors) {
        this.bookAuthors = bookAuthors;
    }
}
