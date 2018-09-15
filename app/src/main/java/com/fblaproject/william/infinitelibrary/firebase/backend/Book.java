package com.fblaproject.william.infinitelibrary.firebase.backend;

class Book {

    //fields needed to represent a book
    private String ID;
    private String title;
    private String author;
    private String description;
    private int rating;
    private int ratingCount;
    private String ISBN;
    private String deweyIndexNumber;
    private String currentOwner;

    //Constructors. SOme are explicit, others use default values for instantiation
    Book(String ID, String title, String author, String description, int rating, int ratingCount, String ISBN, String deweyIndexNumber, String currentOwner) {
        this(ID, title, author, description, rating, ratingCount, ISBN, deweyIndexNumber);
        this.currentOwner = currentOwner;
    }
    Book(String ID, String title, String author, String description, int rating, int ratingCount, String ISBN, String deweyIndexNumber) {
        this(ID, title, author, description, rating, ratingCount, ISBN);
        this.deweyIndexNumber = deweyIndexNumber;
        this.currentOwner = "Not Currently Checked Out";
    }
    Book(String ID, String title, String author, String description, int rating, int ratingCount, String ISBN) {
        this(ID, title, author, description, ISBN);
        this.rating = rating;
        this.ratingCount = ratingCount;
        this.deweyIndexNumber = "Dewey Index Not Available";
        this.currentOwner = "Not Currently Checked Out";
    }
    Book(String ID, String title, String author, String description, String ISBN) {
        this(ID, title, author, description);
        this.ISBN = ISBN;
        this.deweyIndexNumber = "Dewey Index Not Available";
        this.currentOwner = "Not Currently Checked Out";
        this.rating = -1;
        this.ratingCount = -1;
    }
    Book(String ID, String title, String author, String description) {
        this(ID, title, author);
        this.description = description;
        this.ISBN = "ISBN Not Available";
        this.deweyIndexNumber = "Dewey Index Not Available";
        this.currentOwner = "Not Currently Checked Out";
        this.rating = -1;
        this.ratingCount = -1;
    }
    Book(String ID, String title, String author) {
        this.ID = ID;
        this.title = title;
        this.author = author;
        this.description = "Description Not Available";
        this.ISBN = "ISBN Not Available";
        this.deweyIndexNumber = "Dewey Index Not Available";
        this.currentOwner = "Not Currently Checked Out";
        this.rating = -1;
        this.ratingCount = -1;
    }

    //Getters and setters
    String getID() {
        return this.ID;
    }
    String getTitle() {
        return this.title;
    }
    String getAuthor() {
        return author;
    }
    String getDescription() {
        return description;
    }
    int getRating() {
        return this.rating;
    }
    int getRatingCount() {
        return this.ratingCount;
    }
    String getISBN() {
        return this.ISBN;
    }
    String getDeweyIndexNumber() {
        return deweyIndexNumber;
    }
    String getCurrentOwner() {
        return currentOwner;
    }

    void setDescription(String description) {
        this.description = description;
        BackendFunctions.updateBook(this);
    }
    void setRating(int rating) {
        this.rating = rating;
    }
    void setRatingCount(int ratingCount) {
        this.ratingCount = ratingCount;
    }
    void setISBN(String ISBN) {
        this.ISBN = ISBN;
        BackendFunctions.updateBook(this);
    }
    void setDeweyIndexNumber(String deweyIndexNumber) {
        this.deweyIndexNumber = deweyIndexNumber;
        BackendFunctions.updateBook(this);
    }

    void checkOut(String currentOwner) {
        this.currentOwner = currentOwner;
        BackendFunctions.updateBook(this);
    }
    void checkIn() {
        this.currentOwner = "Not Currently Checked Out";
        BackendFunctions.updateBook(this);
    }


    boolean isCheckedOut() {
        return this.currentOwner.equals("Not Currently Checked Out");
    }


    public String toString() {
        return toString("");
    }
    public String toString(String spacer) {
        String output = "";

        output += spacer + this.title + "\n";

        if(spacer == "|   |---") {
            spacer = "|       ";
        }
        if(spacer == "    `---") {
            spacer = "        ";
        }
        if(spacer == "|   `---") {
            spacer = "|       ";
        }

        output += spacer + "|---Author: " + this.author + "\n";
        output += spacer + "|---Description: " + this.description + "\n";
        output += spacer + "|---Rating: " + this.rating + "\n";
        output += spacer + "|---ISBN: " + this.ISBN + "\n";

        if(currentOwner == null) {
            output += spacer + "`---Dewey Index: " + this.deweyIndexNumber + "\n";
        } else {
            output += spacer + "|---Dewey Index: " + this.deweyIndexNumber + "\n";
            output += spacer + "`---Current Owner: " + currentOwner;
        }

        return output;
    }

}
