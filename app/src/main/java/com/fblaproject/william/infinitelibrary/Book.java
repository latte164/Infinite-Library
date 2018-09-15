package com.fblaproject.william.infinitelibrary;

import android.graphics.Bitmap;

/**
 * Created by ZackPC on 10/9/2017.
 *
 * Object used to represent books
 * Acts as an interface for the Frontend Developers and the Firebase data
 */

class Book {

    //fields needed to represent a book
    private String title;
    private String author;
    private String description;
    private float rating;
    private int ratingCount;
    private String ISBN;
    private String deweyIndexNumber;
    private String currentOwner;
    private Bitmap cover;
    private String reservedTo;

    private String publisher;
    private String datePublished;
    private String publicationLocation;

    //Constructor
    Book(String ISBN, String title, String author, String description, float rating, int ratingCount, String deweyIndexNumber, String currentOwner, Bitmap cover, String reservedTo, String publisher, String datePublished, String publicationLocation) {
        this.title = title;
        this.author = author;
        this.description = description;
        this.rating = rating;
        this.ratingCount = ratingCount;
        this.ISBN = ISBN;
        this.deweyIndexNumber = deweyIndexNumber;
        this.currentOwner = currentOwner;
        this.cover = cover;
        this.reservedTo = reservedTo;
        this.publisher = publisher;
        this.datePublished = datePublished;
        this.publicationLocation = publicationLocation;
    }

    //Getters
    String getTitle() {
        return this.title;
    }
    String getAuthor() {
        return this.author;
    }
    String getDescription() {
        return this.description;
    }
    float getRating() {
        return this.rating;
    }
    int getRatingCount() {
        return this.ratingCount;
    }
    String getISBN() {
        return this.ISBN;
    }
    String getDeweyIndexNumber() {
        if(this.deweyIndexNumber == null) {
            return "Dewey Index Not Available";
        } else {
            return this.deweyIndexNumber;
        }
    }
    String getCurrentOwner() {
        return this.currentOwner;
    }
    Bitmap getCover() {
        return this.cover;
    }
    String getReserver() {
        return this.reservedTo;
    }
    String getPublisher() {
        return publisher;
    }
    String getPublisherForCitation() {
        if(this.publisher.equals("Publisher Not Available")) {
            return "n.p.";
        } else {
            return this.publisher;
        }
    }
    String getDatePublished() {
        return datePublished;
    }
    String getDatePublishedForCitation() {
        if(this.datePublished.equals("Date Published Not Available")) {
            return "n.d.";
        } else {
            return this.datePublished;
        }
    }
    String getPublicationLocation() {
        return publicationLocation;
    }
    String getPublicationLocationForCitation() {
        if(this.publicationLocation.equals("Publication Location Not Available")) {
            return "N.p.";
        } else {
            return this.publicationLocation;
        }
    }
    boolean isCheckedOut() {
        return !this.currentOwner.equals("Not Currently Checked Out");
    }
    boolean isReserved() {
        return !this.reservedTo.equals("Not Reserved");
    }


    //Setters
    void setDescription(String description) {
        this.description = description;
        BackendFunctions.updateBook(this);
    }
    void setRating(float rating) {
        this.rating = rating;
        BackendFunctions.updateBook(this);
    }
    void setRatingCount(int ratingCount) {
        this.ratingCount = ratingCount;
        BackendFunctions.updateBook(this);
    }
    void setDeweyIndexNumber(String deweyIndexNumber) {
        this.deweyIndexNumber = deweyIndexNumber;
        BackendFunctions.updateBook(this);
    }
    void setCover(Bitmap cover) {
        this.cover = cover;
        BackendFunctions.updateBook(this);
    }

    void setDatePublished(String datePublished) {
        this.datePublished = datePublished;
    }
    void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    //used to check the book in or out
    void checkOut(String currentOwner) {
        this.currentOwner = currentOwner;
        BackendFunctions.updateBook(this);
    }
    void checkIn() {
        this.currentOwner = "Not Currently Checked Out";
        BackendFunctions.updateBook(this);
    }

    //reserve or unreserve the book
    void reserve(String username) {
        this.reservedTo = username;
        BackendFunctions.updateBook(this);
    }
    void unreserve() {
        this.reservedTo = "Not Reserved";
        BackendFunctions.updateBook(this);
    }

    //changes the rating and rating count to simulate the addition of one new rating
    void addRating(float value) {
        int actualCount = this.ratingCount == -1? 0: this.ratingCount;
        this.rating = ((this.rating == -1? 0: this.rating) * actualCount + value)/(actualCount+1);
        this.ratingCount = actualCount + 1;
        BackendFunctions.updateBook(this);
    }

    //I redefined toString for this object to aid in debugging
    //Displays the book in a file-like fashion
    //Spacer is used to display the book as a sub-object (i.e. in the list of an Account)
    public String toString() {
        return toString("");
    }
    public String toString(String spacer) {
        String output = "";

        output += spacer + this.title + "\n";

        if(spacer.equals("|   |---")) {
            spacer = "|       ";
        }
        if(spacer.equals("    `---")) {
            spacer = "        ";
        }
        if(spacer.equals("|   `---")) {
            spacer = "|       ";
        }

        output += spacer + "|---Author: " + this.author + "\n";
        output += spacer + "|---Description: " + this.description + "\n";
        output += spacer + "|---Rating: " + this.rating + "\n";
        output += spacer + "|---ISBN: " + this.ISBN + "\n";
        output += spacer + "|---Dewey Index: " + this.deweyIndexNumber + "\n";

        if(currentOwner != null) {
            output += spacer + "|---Current Owner: " + this.currentOwner + "\n";
        }

        output += "`---Cover Image (Base64 String): " + BackendFunctions.bitmapToString(this.cover);

        return output;
    }

}
