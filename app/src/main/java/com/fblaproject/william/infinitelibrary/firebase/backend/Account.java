package com.fblaproject.william.infinitelibrary.firebase.backend;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class Account {

    //Fields needed to represent a user account
    private String username;
    private String name;
    private List<Book> booksCheckedOut;
    private List<Book> watchlist;
    private String accountType;

    //Constructors. Some are explicit, others are for implicit instantiation.
    Account(String username, String name, String accountType, ArrayList<Book> booksCheckedOut, ArrayList<Book> watchlist) {
        this(username, name, accountType);
        this.booksCheckedOut = booksCheckedOut;
        this.watchlist = watchlist;
    }
    Account(String username, String name, String accountType) {
        this.username = username;
        this.name = name;
        this.accountType = accountType;
        this.booksCheckedOut = new ArrayList<Book>();
        this.watchlist = new ArrayList<Book>();
    }

    //Getter functions for the class
    String getUsername() {
        return username;
    }
    public String getName() {
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

    void addToWatchlist(Book b) {
        //Updates account instance to reflect change
        this.watchlist.add(b);

        //Updates backend for the user account
        BackendFunctions.updateAccount(this);
    }
    void removeFromWatchlist(Book b) {

        //Handles case where the book is not checked out by this account
        if(!this.watchlist.contains(b)) {
            Log.e("ERROR", "Account "+this.username+" does not currently have "+b.getTitle()+" by "+b.getAuthor()+"checked out.");
            return;
        }

        //Updates account instance to reflect the change
        this.watchlist.remove(b);

        //Updates backend for the user account
        BackendFunctions.updateAccount(this);
    }
    void removeFromWatchlist(String bookID) {
        for(Book b: watchlist) {
            if(b.getID().equals(bookID)) {
                removeFromWatchlist(b);
            }
        }
    }

    void checkOutBook(Book b) {
        //Updates the user and book instances to reflect the change
        this.booksCheckedOut.add(b);
        b.checkOut(this.username);

        //Updates the backend for the User account
        BackendFunctions.updateAccount(this);
    }
    void returnBook(Book b) {
        //Handles case where the book is not checked out by this account
        if(!this.booksCheckedOut.contains(b)) {
            Log.e("ERROR", "Account "+this.username+" does not currently have "+b.getTitle()+" by "+b.getAuthor()+"checked out.");
            return;
        }

        //Updates the book and account instances to reflect the change
        this.booksCheckedOut.remove(b);
        b.checkIn();

        //Updates the backend for the user account
        BackendFunctions.updateAccount(this);
    }
    void returnBook(String bookID) {
        for(Book b: this.booksCheckedOut) {
            if(b.getID().equals(bookID)) {
                returnBook(b);
            }
        }
    }

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
