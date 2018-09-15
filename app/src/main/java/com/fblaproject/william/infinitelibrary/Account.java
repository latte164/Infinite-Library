package com.fblaproject.william.infinitelibrary;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ZackPC on 10/9/2017.
 *
 * Object used to represent user accounts
 * Acts as an interface for the Frontend Developers and the Firebase data
 */

class Account {

    //Fields needed to represent a user account
    private String username;
    private String name;
    private List<Book> booksCheckedOut;
    private List<Book> watchlist;
    private List<Book> reservedBooks;
    private String accountType;
    private boolean isDeleted;

    //a list of device tokens registered to the user
    //used for device push notifications
    private List<String> deviceTokens;

    //needed for encrypting sharedPrefs stuff needed for staying logged in
    private String sk;

    //Constructors. Some are explicit, others are for implicit instantiation.
    Account(String username, String name, String accountType, List<Book> booksCheckedOut, List<Book> watchlist, List<Book> reservedBooks, String sk, List<String> deviceTokens, boolean isDeleted) {
        this.username = username;
        this.name = name;
        this.accountType = accountType;
        this.booksCheckedOut = booksCheckedOut;
        this.watchlist = watchlist;
        this.reservedBooks = reservedBooks;
        this.sk = sk;
        this.deviceTokens = deviceTokens;
        this.isDeleted = isDeleted;
    }
    Account(String username, String name, String accountType, List<Book> booksCheckedOut, List<Book> watchlist, List<Book> reservedBooks, String sk) {
        this(username, name, accountType, booksCheckedOut, watchlist, reservedBooks, sk, new ArrayList<String>(), false);
    }
    Account(String username, String name, String accountType, List<Book> booksCheckedOut, List<Book> watchlist, List<Book> reservedBooks) {
        this(username, name, accountType, booksCheckedOut, watchlist, reservedBooks, "No Secret Key!!");
    }
    Account(String username, String name, String accountType, List<Book> booksCheckedOut, List<Book> watchlist) {
        this(username, name, accountType, booksCheckedOut, watchlist, new ArrayList<Book>());
    }
    Account(String username, String name, String accountType) {
        this(username, name, accountType, new ArrayList<Book>(), new ArrayList<Book>());
    }

    //Getter functions for the class
    String getUsername() {
        return username;
    }
    String getName() {
        return this.name;
    }
    String getAccountType() {
        return this.accountType;
    }
    List<Book> getBooksCheckedOut() {
        return this.booksCheckedOut;
    }
    List<Book> getWatchlist() {
        return this.watchlist;
    }
    List<Book> getReservedBooks() {
        return this.reservedBooks;
    }
    String getPrivateKey() {
        return this.sk;
    }
    List<String> getDeviceTokens() {
        return this.deviceTokens;
    }
    boolean isDeleted() {
        return this.isDeleted;
    }

    //adding and removing device tokens
    void addDeviceToken(String token) {
        //adds the token
        this.deviceTokens.add(token);
        //updates database object
        BackendFunctions.updateAccount(this);
    }
    void removeDeviceToken(String token) {
        //removes the token if it is there
        if(this.deviceTokens.contains(token)) {
            this.deviceTokens.remove(token);
        }
        //updates the database object
        BackendFunctions.updateAccount(this);
    }

    //watchlist functions
    void addToWatchlist(Book b) {

        //instantiates the list if necessary
        if(this.watchlist == null) {
            this.watchlist = new ArrayList<Book>();
        }

        //checks for a book duplicate
        for(Book b2: watchlist) {
            if(b2.getISBN().equals(b.getISBN())) {
                Log.e("ERROR: ", "Account "+this.username+" already has this book on their watchlist!");
                return;
            }
        }

        //Updates account instance to reflect change
        this.watchlist.add(b);

        //Updates backend for the user account
        BackendFunctions.updateAccount(this);
    }
    void removeFromWatchlist(Book b) {
        removeFromWatchlist(b.getISBN());
    }
    void removeFromWatchlist(String bookISBN) {

        //compiles a list of ISBNs
        List<String> bookISBNs = new ArrayList<String>();
        for(Book b: this.watchlist) {
            bookISBNs.add(b.getISBN());
        }
        //checks if the book is actually on their watchlist
        if(!bookISBNs.contains(bookISBN)) {
            Log.e("ERROR: ", "Account " + this.username + " does not currently have a book with ISBN " + bookISBN + " on their watchlist.");
            return;
        }

        //removes book from watchlist
        for(Book b: this.watchlist) {
            if(b.getISBN().equals(bookISBN)) {
                this.watchlist.remove(b);
                break;
            }
        }

        //updates database object
        BackendFunctions.updateAccount(this);
    }

    //check out and return functions
    void checkOutBook(Book b) {

        //instantiates the list if necessary
        if(this.booksCheckedOut == null) {
            this.booksCheckedOut = new ArrayList<Book>();
        }

        //checks for a book duplicate
        for(Book b2: booksCheckedOut) {
            if(b2.getISBN().equals(b.getISBN())) {
                Log.e("ERROR: ", "Account "+this.username+" already has this book checked out!");
                return;
            }
        }
        //makes sure the book isn't already checked out
        if(b.isCheckedOut()) {
            Log.e("ERROR: ", "Account "+b.getCurrentOwner()+" has this book checked out!");
            return;
        }

        //Updates the user and book instances to reflect the change
        this.booksCheckedOut.add(b);
        b.checkOut(this.username);

        //Updates the backend for the User account
        BackendFunctions.updateAccount(this);
    }
    void returnBook(Book b) {
        returnBook(b.getISBN());
    }
    void returnBook(String bookISBN) {

        //compiles a list of ISBNs
        List<String> bookISBNs = new ArrayList<String>();
        for(Book b: this.booksCheckedOut) {
            bookISBNs.add(b.getISBN());
        }
        //checks if the book is actually checked out
        if(!bookISBNs.contains(bookISBN)) {
            Log.e("ERROR: ", "Account " + this.username + " does not currently have a book with ISBN " + bookISBN + " checked out.");
            return;
        }

        //removes book from booksCheckedOut
        for(Book b: this.booksCheckedOut) {
            if(b.getISBN().equals(bookISBN)) {
                this.booksCheckedOut.remove(b);
                b.checkIn();
                break;
            }
        }

        //updates database object
        BackendFunctions.updateAccount(this);
    }

    //reserve and unreserve book functions, which also unreserve the book object
    void reserveBook(Book b) {

        //instantiates the list if necessary
        if(this.reservedBooks == null) {
            this.reservedBooks = new ArrayList<Book>();
        }

        //checks for a book duplicate
        for(Book b2: reservedBooks) {
            if(b2.getISBN().equals(b.getISBN())) {
                Log.e("ERROR: ", "Account "+this.username+" already has this book in their watchlist!");
                return;
            }
        }
        //checks if the book is already reserved by someone else
        if(b.isReserved()) {
            Log.e("ERROR: ", "Account "+b.getReserver()+" has this book in their watchlist!");
            return;
        }

        //reserves the book
        this.reservedBooks.add(b);
        b.reserve(this.username);

        //updates the database object
        BackendFunctions.updateAccount(this);
    }
    void unreserveBook(Book b) {
        unreserveBook(b.getISBN());
    }
    void unreserveBook(String bookISBN) {

        //compiles a list of ISBNs
        List<String> bookISBNs = new ArrayList<String>();
        for(Book b: this.reservedBooks) {
            bookISBNs.add(b.getISBN());
        }
        //checks whether the book is actually reserved or not
        if(!bookISBNs.contains(bookISBN)) {
            Log.e("ERROR: ", "Account " + this.username + " does not currently have a book with ISBN " + bookISBN + " reserved.");
            return;
        }

        //removes book from reservedBooks and calls the book's unreserve function
        for(Book b: this.reservedBooks) {
            if(b.getISBN().equals(bookISBN)) {
                this.reservedBooks.remove(b);
                this.booksCheckedOut.remove(b);
                b.unreserve();
                BackendFunctions.updateBook(b);
                break;
            }
        }

        //update database objects
        BackendFunctions.updateAccount(this);
    }

    //redefined toString for this object for debugging purposes
    //displays the object in a file-like fashion
    public String toString() {
        String output = "";
        output += this.name + "\n";

        output += "|---Account Type: " + this.accountType + "\n";
        if(this.booksCheckedOut != null && this.booksCheckedOut.size() > 0) {
            output += "|---Books Checked Out\n";
            for(int i = 0; i < booksCheckedOut.size(); i++) {
                Book b = booksCheckedOut.get(i);
                if(i == booksCheckedOut.size() - 1) {
                    output += b.toString("|   `---");
                } else {
                    output += b.toString("|   |---");
                }
            }
        }
        if(this.watchlist != null && this.watchlist.size() > 0) {
            output += "`---Books on Watchlist\n";
            for(int i = 0; i < watchlist.size(); i++) {
                Book b = watchlist.get(i);
                if(i == watchlist.size() - 1) {
                    output += b.toString("    `---");
                } else {
                    output += b.toString("|   |---");
                }
            }
        }

        return output;
    }
}
