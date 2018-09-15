package com.fblaproject.william.infinitelibrary.firebase.backend;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BackendFunctions {

    //Takes in a data snapshot of the relevant directory and identifying information
    //Returns the respective interfaced data (i.e. Account or Book object)
    //Account Getters take the Accounts directory, Book Getters take the Books directory
    public static Account getAccount(DataSnapshot dataSnapshot, String username) {

        String name = "";
        String accountType = "";
        ArrayList<Book> booksCheckedOut = new ArrayList<Book>();
        ArrayList<Book> watchlist = new ArrayList<Book>();
        for(DataSnapshot ds: dataSnapshot.getChildren()) {

            //Checks validity of each Firebase entry
            //If valid and correct, sets the Account parameters to their datapoints from Firebase
            if(!ds.getKey().equals(username)) continue;
            if(ds.child("Name").getValue() != null) name = ds.child("Name").getValue().toString();
            else continue;
            if(ds.child("Account Type").getValue() != null)
                accountType = ds.child("Account Type").getValue().toString();
            else accountType = "User";
            if(ds.child("Books Taken").getValue() != null) {

                DataSnapshot bookList = ds.child("Books Taken");
                for(DataSnapshot book: ds.child("Books Taken").getChildren()) {

                    String ID = book.getKey();

                    String title;
                    if(book.child("Title").getValue() != null) {
                        title = book.child("Title").getValue().toString();
                    } else {
                        title = "Book Title Not Available";
                    }

                    String author;
                    if(book.child("Author").getValue() != null) {
                        author = book.child("Author").getValue().toString();
                    } else {
                        author = "Book Author Not Available";
                    }

                    String description;
                    if(book.child("Description").getValue() != null) {
                        description = book.child("Description").getValue().toString();
                    } else {
                        description = "Book Description Not Available";
                    }

                    int rating;
                    if(book.child("Rating").getValue() != null) {
                        rating = Integer.parseInt(book.child("Rating").getValue().toString());
                    } else {
                        rating = -1;
                    }

                    int ratingCount;
                    if(book.child("Rating Count").getValue() != null) {
                        ratingCount = Integer.parseInt(book.child("Rating Count").getValue().toString());
                    } else {
                        ratingCount = -1;
                    }

                    String ISBN;
                    if(book.child("ISBN").getValue() != null) {
                        ISBN = book.child("ISBN").getValue().toString();
                    } else {
                        ISBN = "Book ISBN Not Available";
                    }

                    String deweyDecimalNumber;
                    if(book.child("deweyDecimalNumber").getValue() != null) {
                        deweyDecimalNumber = book.child("deweyDecimalNumber").getValue().toString();
                    } else {
                        deweyDecimalNumber = "Dewey Index Not Available";
                    }

                    booksCheckedOut.add(new Book(ID, title, author, description, rating, ratingCount, ISBN, deweyDecimalNumber));
                }
            }
            if(ds.child("Watchlist").getValue() != null) {

                DataSnapshot bookWatchlist = ds.child("Watchlist");
                for(DataSnapshot book: bookWatchlist.getChildren()) {

                    String ID = book.getKey();

                    String title;
                    if(book.child("Title").getValue() != null) {
                        title = book.child("Title").getValue().toString();
                    } else {
                        title = "Book Title Not Available";
                    }

                    String author;
                    if(book.child("Author").getValue() != null) {
                        author = book.child("Author").getValue().toString();
                    } else {
                        author = "Book Author Not Available";
                    }

                    String description;
                    if(book.child("Description").getValue() != null) {
                        description = book.child("Description").getValue().toString();
                    } else {
                        description = "Book Description Not Available";
                    }

                    int rating;
                    if(book.child("Rating").getValue() != null) {
                        rating = Integer.parseInt(book.child("Rating").getValue().toString());
                    } else {
                        rating = -1;
                    }

                    int ratingCount;
                    if(book.child("Rating Count").getValue() != null) {
                        ratingCount = Integer.parseInt(book.child("Rating Count").getValue().toString());
                    } else {
                        ratingCount = -1;
                    }

                    String ISBN;
                    if(book.child("ISBN").getValue() != null) {
                        ISBN = book.child("ISBN").getValue().toString();
                    } else {
                        ISBN = "Book ISBN Not Available";
                    }

                    String deweyDecimalNumber;
                    if(book.child("deweyDecimalNumber").getValue() != null) {
                        deweyDecimalNumber = book.child("deweyDecimalNumber").getValue().toString();
                    } else {
                        deweyDecimalNumber = "Dewey Index Not Available";
                    }

                    watchlist.add(new Book(ID, title, author, description, rating, ratingCount, ISBN, deweyDecimalNumber));
                }
            }

            break;
        }
        if(!name.equals("")) return new Account(username, name, accountType, booksCheckedOut, watchlist);
        else return null;
    }
    static Book getBookFromID(DataSnapshot dataSnapshot, String ID) {

        //Initialize default values for potential null fields
        String title = "Title Not Available";
        String author = "Author Not Available";
        String description = "Description Not Available";
        int rating = -1;
        int ratingCount = -1;
        String ISBN = "ISBN Not Available";
        String deweyIndex = "Dewey Index Not Available";
        String currentOwner = "Not Currently Checked Out";

        //Loop through the books in the directory
        for(DataSnapshot ds: dataSnapshot.getChildren()) {
            //Check if book has the desired ID
            if(!ds.getKey().equals(ID)) continue;

            //Fill in all fields for the book
            if(ds.child("Title")!= null && ds.child("Title").getValue()!=null)                              title       = ds.child("Title").getValue().toString();
            if(ds.child("Author")!= null && ds.child("Author").getValue()!=null)                            author      = ds.child("Author").getValue().toString();
            if(ds.child("Description")!= null && ds.child("Description").getValue()!=null)                  description = ds.child("Description").getValue().toString();
            if(ds.child("ISBN")!= null && ds.child("ISBN").getValue()!=null)                                ISBN        = ds.child("ISBN").getValue().toString();
            if(ds.child("Dewey Index Number")!= null && ds.child("Dewey Index Number").getValue()!=null)    deweyIndex  = ds.child("Dewey Index Number").getValue().toString();
            if(ds.child("Current Owner")!=null && ds.child("Current Owner").getValue()!=null)               currentOwner= ds.child("Current Owner").getValue().toString();
            if(ds.child("Rating")!=null && ds.child("Rating").getValue()!=null)                             rating      = Integer.parseInt(ds.child("Rating").getValue().toString());
            if(ds.child("Rating Count")!=null && ds.child("Rating Count").getValue()!=null)                 ratingCount = Integer.parseInt(ds.child("Rating Count").getValue().toString());

            break;
        }

        return new Book(ID, title, author, description, rating, ratingCount, ISBN, deweyIndex, currentOwner);
    }
    static Book getBookFromTitleAndAuthor(DataSnapshot dataSnapshot, String title, String author) {

        //Initialize the book's ID
        String ID = "";

        //Cycle through all the books in the directory
        for(DataSnapshot ds: dataSnapshot.getChildren()) {
            //If title and author match the desired book, get that book's ID
            if(ds.child("Title") != null && ds.child("Author") != null &&
                    ds.child("Title").getValue().toString().equals(title) &&
                    ds.child("Author").getValue().toString().equals(author)) {
                ID = ds.getKey();
                break;
            }
        }

        //Return the book needed using it's ID
        return getBookFromID(dataSnapshot, ID);
    }

    //search function
    static List<Book> searchBooks(DataSnapshot dataSnapshot, String searchWord, String dataType) {
        List<Book> searchResults = new ArrayList<Book>();

        for(DataSnapshot ds: dataSnapshot.getChildren()) {
            //If desired datapoint contains the searched string, add it to the search results
            if(ds.child(dataType) != null && ds.child(dataType).getValue() != null && ds.child(dataType).getValue().toString().toLowerCase().contains(searchWord.toLowerCase())) {
                searchResults.add(getBookFromID(dataSnapshot, ds.getKey()));
            }
        }

        return searchResults;
    }

    //Updates the Firebase entry for a user account or book
    static void updateAccount(Account a) {

        //Set up Firebase Reference to use for updating
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("Accounts");

        //Account is not natively serializable because of the ArrayLists
        //Map heirarchy is simply a serializable version of the Account

        Map<String, Object> nameMap = new HashMap<String, Object>();
        nameMap.put("Name", a.getName());

        //Create the booksCheckedOut in serializable form
        //Books Checked Out Map -> Maps Book IDs to Book Maps
        //Book -> Maps Book Attribute Names (Title, Author, etc.) to Attributes (Moby Dick, Herman Melville, etc.)
        Map<String, Map<String, Object>> booksCheckedOutMap = new HashMap<String, Map<String, Object>>();
        List<Book> booksCheckedOut = a.getBooksCheckedOut();
        for(Book b: booksCheckedOut) {

            Map<String, Object> bookMap = new HashMap<String, Object>();
            bookMap.put("Title", b.getTitle());
            bookMap.put("Author", b.getAuthor());
            bookMap.put("Description", b.getDescription());
            bookMap.put("ISBN", b.getISBN());
            bookMap.put("Dewey Index Number", b.getDeweyIndexNumber());
            bookMap.put("Current Owner", b.getCurrentOwner());
            bookMap.put("Rating", b.getRating());
            bookMap.put("Rating Count", b.getRatingCount());

            booksCheckedOutMap.put(b.getID(), bookMap);
        }

        //Create the watchlist in serializable form
        //Watchlist Map -> Maps Book IDs to Book Maps
        //Book Map -> Maps Book Attribute Names (Title, Author, etc.) to Attributes (Moby Dick, Herman Melville, etc.)
        Map<String, Map<String, Object>> watchlistMap = new HashMap<String, Map<String, Object>>();
        List<Book> watchlist = a.getWatchlist();
        for(Book b: watchlist) {

            Map<String, Object> bookMap = new HashMap<String, Object>();
            bookMap.put("Title", b.getTitle());
            bookMap.put("Author", b.getAuthor());
            bookMap.put("Description", b.getDescription());
            bookMap.put("ISBN", b.getISBN());
            bookMap.put("Dewey Index Number", b.getDeweyIndexNumber());
            bookMap.put("Current Owner", b.getCurrentOwner());
            bookMap.put("Rating", b.getRating());
            bookMap.put("Rating Count", b.getRatingCount());

            watchlistMap.put(b.getID(), bookMap);
        }

        ref.child(a.getUsername()).updateChildren(nameMap);
        ref.child(a.getUsername()).child("Books Taken").setValue(booksCheckedOutMap);
        ref.child(a.getUsername()).child("Watchlist").setValue(watchlistMap);
    }
    static void updateBook(Book b) {

        //Set up Firebase Reference to use for updating
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("Books");

        Map<String, Object> bookMap = new HashMap<String, Object>();
        bookMap.put("Title", b.getTitle());
        bookMap.put("Author", b.getAuthor());
        bookMap.put("Description", b.getDescription());
        bookMap.put("Rating", b.getRating());
        bookMap.put("Rating Count", b.getRatingCount());
        bookMap.put("ISBN", b.getISBN());
        bookMap.put("Dewey Index Number", b.getDeweyIndexNumber());
        bookMap.put("Current Owner", b.getCurrentOwner());

        ref.child(b.getID()).setValue(bookMap);
    }

}
