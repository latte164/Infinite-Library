package com.fblaproject.william.infinitelibrary;

/**
 * This class is a return data provider. It receives data from the adapter and formats it for the list view.
 */

public class ReturnBookDataProvider {

    //Initialize variables
    private String bookTitles, bookAuthors;

    //Setup for the data provider
    public ReturnBookDataProvider(String bookTitles, String bookAuthors) {

        this.setBookAuthors(bookAuthors);
        this.setBookTitles(bookTitles);
    }

    //Returns book titles string
    public String getBookTitles() {
        return bookTitles;
    }

    //Sets book titles string
    public void setBookTitles(String bookTitles) {
        this.bookTitles = bookTitles;
    }

    //Return book authors string
    public String getBookAuthors() {
        return bookAuthors;
    }

    //Sets book authors string
    public void setBookAuthors(String bookAuthors) {
        this.bookAuthors = bookAuthors;
    }
}
