package com.fblaproject.william.infinitelibrary;

import android.graphics.Bitmap;

public class BookDataProvider {

    private Bitmap bookImages;
    //private int bookImages;
    private String bookTitles, bookAuthors;


    public BookDataProvider(Bitmap bookImages, String bookTitles, String bookAuthors) {
    //public BookDataProvider(int bookImages, String bookTitles, String bookAuthors) {

        this.setBookAuthors(bookAuthors);
        this.setBookImages(bookImages);
        this.setBookTitles(bookTitles);

    }


    public Bitmap getBookImages() {
    //public int getBookImages() {
        return bookImages;
    }

    //public void setBookImages(int bookImages) {
    public void setBookImages(Bitmap bookImages) {
        this.bookImages = bookImages;
    }

    public String getBookTitles() {
        return bookTitles;
    }

    public void setBookTitles(String bookTitles) {
        this.bookTitles = bookTitles;
    }

    public String getBookAuthors() {
        return bookAuthors;
    }

    public void setBookAuthors(String bookAuthors) {
        this.bookAuthors = bookAuthors;
    }
}
