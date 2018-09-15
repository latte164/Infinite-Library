package com.fblaproject.william.infinitelibrary;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.NetworkOnMainThreadException;
import android.util.Base64;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Contains a bunch of useful functions which perform backend operations
 * Contains all functions which act as an interface between the Account/Book objects and the Firebase database
 */

class BackendFunctions {

    //TAG required for Logging errors or warnings
    private static String TAG = "BACKEND DEBUG";

    //getAccount takes a snapshot of the whole database, while getBook methods just take a snapshot of the Books directory
    //Returns the respective interfaced data (i.e. Account or Book object)
    //Account Getters take the entire directory, Book Getters take the Books directory
    static Account getAccount(DataSnapshot dataSnapshot, String username) {

        //checks for NullPointerException, handles accordingly
        if(dataSnapshot == null) {
            Log.e("ERROR: ", "Data Snapshot parameter is null");
            return null;
        }
        if(dataSnapshot.getChildren() == null) {
            Log.e("ERROR: ", "Data Snapshot has no children");
            return null;
        }

        //sets up the smaller, more specialized snapshots
        DataSnapshot accountSnapshot = dataSnapshot.child("Accounts");
        DataSnapshot bookSnapshot = dataSnapshot.child("Books");

        //default initializes the fields to fill out
        String name = "";
        String accountType = "";
        ArrayList<Book> booksCheckedOut = new ArrayList<Book>();
        ArrayList<Book> watchlist = new ArrayList<Book>();
        ArrayList<Book> reservedBooks = new ArrayList<Book>();
        String sk = "No Private Key!!";
        boolean isDeleted = false;
        ArrayList<String> deviceTokens = new ArrayList<String>();

        //checks for a null snapshot (only occurs when there are no accounts in the database) then cycles through accounts
        if(accountSnapshot.getChildren()!= null)
        for(DataSnapshot ds: accountSnapshot.getChildren()) {

            //Checks validity of each Firebase entry
            //If valid and correct, sets the Account parameters to their datapoints from Firebase
            if(ds.getKey() == null || (ds.getKey() != null && !ds.getKey().equals(username))) continue;
            if(ds.child("Name").getValue() != null) name = ds.child("Name").getValue().toString();
            else continue;
            if(ds.child("Account Type").getValue() != null)
                accountType = ds.child("Account Type").getValue().toString();
            else accountType = "User";
            if(ds.child("Books Taken").getValue() != null) {

                DataSnapshot bookList = ds.child("Books Taken");
                
                for(DataSnapshot book: bookList.getChildren()) {
                    booksCheckedOut.add(getBookFromISBN(bookSnapshot, book.getKey()));
                }
            }
            if(ds.child("Watchlist").getValue() != null) {

                DataSnapshot bookWatchlist = ds.child("Watchlist");
                for(DataSnapshot book: bookWatchlist.getChildren()) {
                    watchlist.add(getBookFromISBN(bookSnapshot, book.getKey()));
                }
            }
            if(ds.child("Reserved Books").getValue() != null) {

                DataSnapshot bookReservedList = ds.child("Reserved Books");
                for(DataSnapshot book: bookReservedList.getChildren()) {
                    reservedBooks.add(getBookFromISBN(bookSnapshot, book.getKey()));
                }
            }
            if(ds.child("Device Tokens") != null && ds.child("Device Tokens").getChildren() != null) {
                DataSnapshot deviceTokensList = ds.child("Device Tokens");
                for(DataSnapshot token: deviceTokensList.getChildren()) {
                    deviceTokens.add(token.getKey());
                }
            }
            if(ds.child("Private Key").getValue()!= null) {
                sk = ds.child("Private Key").getValue().toString();
            }

            if(ds.child("Is Deleted").getValue()!= null) {
                isDeleted = ds.child("Is Deleted").getValue().toString().equals("true");
            }

            //after getting all fields, breaks; only one accoutn will match the username
            break;
        }

        //if the account doesn't exist, name will be the default "" and it returns null
        //if not, returns the Account object associated with the username
        if(!name.equals("")) return new Account(username, name, accountType, booksCheckedOut, watchlist, reservedBooks, sk, deviceTokens, isDeleted);
        else return null;
    }
    static Book getBookFromISBN(DataSnapshot dataSnapshot, String ISBN) {

        //checks for NullPointerException, handles accordingly
        if(dataSnapshot == null) {
            Log.e("ERROR: ", "Data Snapshot parameter is null");
            return null;
        }
        if(dataSnapshot.getChildren() == null) {
            Log.e("ERROR: ", "Data Snapshot has no children");
            return null;
        }

        //Initialize default values for potential null fields
        String title = "Title Not Available";
        String author = "Author Not Available";
        String description = "Description Not Available";
        float rating = -1;
        int ratingCount = -1;
        String deweyIndex = "Dewey Index Not Available";
        String currentOwner = "Not Currently Checked Out";
        String reservedTo = "Not Reserved";
        Bitmap cover = null;

        String publisher = "Publisher Not Available";
        String datePublished = "Publication Date Not Available";
        String publicationLocation = "Publication Location Not Available";

        //get the book
        DataSnapshot ds = dataSnapshot.child(ISBN);

        //Fill in all fields for the book (checking for null fields along the way)
        if(ds.child("Title")!= null && ds.child("Title").getValue()!=null)                              title       = ds.child("Title").getValue().toString();
        if(ds.child("Author")!= null && ds.child("Author").getValue()!=null)                            author      = ds.child("Author").getValue().toString();
        if(ds.child("Description")!= null && ds.child("Description").getValue()!=null)                  description = ds.child("Description").getValue().toString();
        if(ds.child("Dewey Index Number")!= null && ds.child("Dewey Index Number").getValue()!=null)    deweyIndex  = ds.child("Dewey Index Number").getValue().toString();
        if(ds.child("Current Owner")!=null && ds.child("Current Owner").getValue()!=null)               currentOwner= ds.child("Current Owner").getValue().toString();
        if(ds.child("Rating")!=null && ds.child("Rating").getValue()!=null)                             rating      = Float.parseFloat(ds.child("Rating").getValue().toString());
        if(ds.child("Rating Count")!=null && ds.child("Rating Count").getValue()!=null)                 ratingCount = Integer.parseInt(ds.child("Rating Count").getValue().toString());
        if(ds.child("Cover")!=null && ds.child("Cover").getValue()!=null)                               cover       = stringToBitmap(ds.child("Cover").getValue().toString());
        if(ds.child("Reserved To")!=null && ds.child("Reserved To").getValue()!=null)                   reservedTo  = ds.child("Reserved To").getValue().toString();
        if(ds.child("Publisher")!=null && ds.child("Publisher").getValue()!=null)                       publisher  = ds.child("Publisher").getValue().toString();
        if(ds.child("Date Published")!=null && ds.child("Date Published").getValue()!=null)             datePublished  = ds.child("Date Published").getValue().toString();
        if(ds.child("Publication Location")!=null && ds.child("Date Published").getValue()!=null)       publicationLocation  = ds.child("Publication Location").getValue().toString();

        //returns Book obejct desired
        return new Book(ISBN, title, author, description, rating, ratingCount, deweyIndex, currentOwner, cover, reservedTo, publisher, datePublished, publicationLocation);
    }
    static Book getBookFromTitleAndAuthor(DataSnapshot dataSnapshot, String title, String author) {

        //Initialize the book's ISBN
        String ISBN = "";

        //Cycle through all the books in the directory
        for(DataSnapshot ds: dataSnapshot.getChildren()) {
            //If title and author match the desired book, get that book's ISBN
            if(ds.child("Title") != null && ds.child("Author") != null &&
                    ds.child("Title").getValue().toString().equals(title) &&
                    ds.child("Author").getValue().toString().equals(author)) {
                ISBN = ds.getKey();
                break;
            }
        }

        //Return the book needed using it's ISBN
        return getBookFromISBN(dataSnapshot, ISBN);
    }


    //search functions, each which overload and filter down to the ultimate one on the bottom

    static List<Book> searchBooks(DataSnapshot dataSnapshot, float minRating, float maxRating) {
        List<Book> searchResults = new ArrayList<Book>();

        for(DataSnapshot ds: dataSnapshot.getChildren()) {
            if(ds != null && ds.child("Rating") != null && ds.child("Rating").getValue() != null) {
                float rating = Float.parseFloat(ds.child("Rating").getValue().toString());
                if(minRating < rating && rating < maxRating) {
                    searchResults.add(getBookFromISBN(dataSnapshot, ds.getKey()));
                }
            }
        }

        return searchResults;
    }

    //basic search across Title, Author, ISBN, and Dewey Index Number
    static List<Book> searchBooks(DataSnapshot dataSnapshot, String searchWord) {
        return searchBooks(dataSnapshot, searchWord, false);
    }
    static List<Book> searchBooks(DataSnapshot dataSnapshot, String searchWord, boolean isExclusive) {
        List<String> dataTypes = new ArrayList<String>();
        List<String> searchWords = new ArrayList<String>();

        dataTypes.add("Title");
        searchWords.add(searchWord);

        dataTypes.add("Author");
        searchWords.add(searchWord);

        dataTypes.add("ISBN");
        searchWords.add(searchWord);

        dataTypes.add("Dewey Index Number");
        searchWords.add(searchWord);

        return searchBooks(dataSnapshot, searchWords, dataTypes, isExclusive);
    }

    //for one search word in one dataType
    static List<Book> searchBooks(DataSnapshot dataSnapshot, String searchWord, String dataType) {
        List<String> searchWords = new ArrayList<String>();
        List<String> dataTypes = new ArrayList<String>();

        searchWords.add(searchWord);
        dataTypes.add(dataType);
        return searchBooks(dataSnapshot, searchWords, dataTypes, false);
    }
    static List<Book> searchBooks(DataSnapshot dataSnapshot, String searchWord, String dataType, boolean isInverted) {
        List<String> searchWords = new ArrayList<String>();
        List<String> dataTypes = new ArrayList<String>();

        searchWords.add(searchWord);
        dataTypes.add(dataType);
        return searchBooks(dataSnapshot, searchWords, dataTypes, false, isInverted);
    }

    //for one search word across many or all data types
    static List<Book> searchBooks(DataSnapshot dataSnapshot, String searchWord, List<String> dataTypes) {
        List<String> searchWords = new ArrayList<String>();

        for(String i: dataTypes) {
            searchWords.add(searchWord);
        }
        return searchBooks(dataSnapshot, searchWords, dataTypes, false, false);
    }

    //ultimate search functions
    static List<Book> searchBooks(DataSnapshot dataSnapshot, List<String> searchWords, List<String> dataTypes, boolean isExclusive) {
        return searchBooks(dataSnapshot, searchWords, dataTypes, isExclusive, false);
    }

    static List<Book> searchBooks(DataSnapshot dataSnapshot, List<String> searchWords, List<String> dataTypes, boolean isExclusive, boolean isInverted) {

        //checks for NullPointerException, handles accordingly
        if(dataSnapshot == null) {
            Log.e("ERROR: ", "Data Snapshot parameter is null");
            return null;
        }
        if(dataSnapshot.getChildren() == null) {
            Log.e("ERROR: ", "Data Snapshot has no children");
            return null;
        }

        //initializes an array (not arrayList) as ArrayLists are slow to add to repeatedly for large data pools
        List<Book> searchResults = new ArrayList<Book>();

        //cycles through all the books
        for(DataSnapshot ds : dataSnapshot.getChildren()) {
            //checks how many of the data fields match the search parameters
            int matches = 0;
            for(int i = 0; i < dataTypes.size(); i++) {
                //adds a match if it matches and isn't inverted or if it doesn't match and is inverted
                if(isInverted) {
                    if (ds.child(dataTypes.get(i)) != null && ds.child(dataTypes.get(i)).getValue() != null && !ds.child(dataTypes.get(i)).toString().toLowerCase().contains(searchWords.get(i).toLowerCase())) {
                        matches++;
                    }
                } else {
                    if (ds.child(dataTypes.get(i)) != null && ds.child(dataTypes.get(i)).getValue() != null && ds.child(dataTypes.get(i)).toString().toLowerCase().contains(searchWords.get(i).toLowerCase())) {
                        matches++;
                    }
                }
            }
            //checks if the match count is enough for the exclusivity of the search (i.e. if the search was OR or AND)
            if((isExclusive && matches == dataTypes.size()) || (!isExclusive && matches > 0)) {
                searchResults.add(getBookFromISBN(dataSnapshot, ds.getKey()));
            }
        }

        //converts the array to the desired List and returns it
        return searchResults;
    }

    //searches through the accounts for any that contain the name string passed
    static List<Account> searchAccounts(DataSnapshot dataSnapshot, String name) {

        //checks for NullPointerExceptions, handles accordingly
        if(dataSnapshot == null) {
            Log.e("ERROR: ", "Data Snapshot parameter is null");
            return null;
        }
        if(dataSnapshot.getChildren() == null) {
            Log.e("ERROR: ", "Data Snapshot has no children");
            return null;
        }

        //initializes an array (not arrayList), as ArrayLists are slow to add to repeatedly
        Account[] searchResultsArray = new Account[100];
        int index = 0;

        //cycles through all the accounts
        for(DataSnapshot ds: dataSnapshot.getChildren()) {
            //checks if the account name contains the search parameter
            if(ds.child("Name").getValue().toString().toLowerCase().contains(name.toLowerCase())) {
                //expands the array by 100 if necessary
                if(index == searchResultsArray.length) {
                    Account[] tempArray = searchResultsArray;
                    searchResultsArray = new Account[tempArray.length + 100];
                    System.arraycopy(tempArray, 0, searchResultsArray, 0, tempArray.length);
                }
                //adds the account to the array
                searchResultsArray[index] = getAccount(dataSnapshot, ds.getKey());
                index++;
            }
        }

        //cuts off the uninitialized end of the array
        Account[] tempArray = searchResultsArray;
        searchResultsArray = new Account[index];
        System.arraycopy(tempArray, 0, searchResultsArray, 0, index);

        //converts the array to the desired List and returns it
        return Arrays.asList(searchResultsArray);
    }

    //Updates the Firebase entry for a user account or book
    static void updateAccount(Account a) {

        //Set up Firebase Reference to use for updating
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("Accounts");

        //Account is not natively serializable because of the ArrayLists
        //Map hierarchy is simply a serializable version of the Account

        Map<String, Object> nameMap = new HashMap<String, Object>();
        nameMap.put("Name", a.getName());

        Map<String, Object> accountTypeMap = new HashMap<String, Object>();
        nameMap.put("Account Type", a.getAccountType());

        Map<String, Object> skMap = new HashMap<String, Object>();
        nameMap.put("Private Key", a.getPrivateKey());

        Map<String, Object> isDeletedMap= new HashMap<String, Object>();
        isDeletedMap.put("Is Deleted", a.isDeleted());

        //To map the lists, the listMaps are maps from strings to other maps for the list items
        //Book -> Maps Book Attribute Names (Title, Author, etc.) to Attributes (Moby Dick, Herman Melville, etc.)

        //Create booksCheckedOut in serializable form
        //Books Checked Out Map -> Maps Book ISBNs to Book Maps
        Map<String, Object> booksCheckedOutMap = new HashMap<String, Object>();
        List<Book> booksCheckedOut = a.getBooksCheckedOut();
        for(Book b: booksCheckedOut) {
            booksCheckedOutMap.put(b.getISBN(), "<-- ISBN");
        }

        //Create watchlist in serializable form
        //Watchlist Map -> Maps Book ISBNs to Book Maps
        Map<String, Object> watchlistMap = new HashMap<String, Object>();
        List<Book> watchlist = a.getWatchlist();
        for(Book b: watchlist) {
            watchlistMap.put(b.getISBN(), "<-- ISBN");
        }

        //Create reservedBooks in serializable form
        //Reserved Books Map -> Maps Book ISBNs to Book Maps
        Map<String, Object> reservedBooksMap = new HashMap<String, Object>();
        List<Book> reservedBooks = a.getReservedBooks();
        for(Book b: reservedBooks) {
            reservedBooksMap.put(b.getISBN(), "<-- ISBN");
        }

        //Create reservedBooks in serializable form
        //Reserved Books Map -> Maps Book ISBNs to Book Maps
        Map<String, Object> deviceTokensMap = new HashMap<String, Object>();
        List<String> deviceTokens = a.getDeviceTokens();
        for(String token: deviceTokens) {
            deviceTokensMap.put(token, "<-- Token");
        }

        //fills out all the child fields
        ref.child(a.getUsername()).updateChildren(nameMap);
        ref.child(a.getUsername()).updateChildren(accountTypeMap);
        ref.child(a.getUsername()).updateChildren(skMap);
        ref.child(a.getUsername()).updateChildren(isDeletedMap);
        ref.child(a.getUsername()).child("Device Tokens").setValue(deviceTokensMap);
        ref.child(a.getUsername()).child("Books Taken").setValue(booksCheckedOutMap);
        ref.child(a.getUsername()).child("Watchlist").setValue(watchlistMap);
        ref.child(a.getUsername()).child("Reserved Books").setValue(reservedBooksMap);

    }
    static void updateBook(Book b) {

        //Set up Firebase Reference to use for updating
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("Books");

        //fills out the Map for the book
        Map<String, Object> bookMap = new HashMap<String, Object>();
        bookMap.put("Title", b.getTitle());
        bookMap.put("Author", b.getAuthor());
        bookMap.put("Description", b.getDescription());
        bookMap.put("Rating", b.getRating());
        bookMap.put("Rating Count", b.getRatingCount());
        bookMap.put("Dewey Index Number", b.getDeweyIndexNumber());
        bookMap.put("Current Owner", b.getCurrentOwner());
        bookMap.put("Cover", bitmapToString(b.getCover()));
        bookMap.put("Reserved To", b.getReserver());
        bookMap.put("Publisher", b.getPublisher());
        bookMap.put("Date Published", b.getDatePublished());
        bookMap.put("Publication Location", b.getPublicationLocation());

        //writes the map to the database
        ref.child(b.getISBN()).setValue(bookMap);
    }

    //a variety of overloaded ways to add an account to the database
    //they utilize updateAccount, as it writes over old datapoints, which adds a new point if there wasn't one there already
    static void addAccount(Account a) {
        addAccount(a.getUsername(), a.getName(), a.getAccountType(), a.getBooksCheckedOut(), a.getWatchlist(), a.getReservedBooks(), a.getPrivateKey());
    }
    static void addAccount(String username, String name, String accountType) {
        List<Book> booksCheckedOut = new ArrayList<Book>();
        List<Book> watchlist = new ArrayList<Book>();
        List<Book> reservedBooks = new ArrayList<Book>();
        addAccount(username, name, accountType, booksCheckedOut, watchlist, reservedBooks);
    }
    static void addAccount(String username, String name, String accountType, List<Book> booksCheckedOut, List<Book> watchlist, List<Book> reservedBooks) {

        //Set up Firebase Reference to use for updating
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("Accounts");

        Random r = new Random();
        String sk = "";
        for(int i = 0; i < 32; i++) {
            int index = r.nextInt(36);
            if(index < 10) {
                sk += Integer.toString(index);
            } else {
                sk += (char)(index - 10 + 'a');
            }
        }

        addAccount(username, name, accountType, booksCheckedOut, watchlist, reservedBooks, sk);
    }
    static void addAccount(String username, String name, String accountType, List<Book> booksCheckedOut, List<Book> watchlist, List<Book> reservedBooks, String sk) {
        addAccount(username, name, accountType, booksCheckedOut, watchlist, reservedBooks, sk, new ArrayList<String>(), false);
    }
    static void addAccount(String username, String name, String accountType, List<Book> booksCheckedOut, List<Book> watchlist, List<Book> reservedBooks, String sk, List<String> deviceTokens, boolean isDeleted) {
        Account newAccount = new Account(username, name, accountType, booksCheckedOut, watchlist, reservedBooks, sk, deviceTokens, isDeleted);

        updateAccount(newAccount);
    }
    static void removeAccount(DataSnapshot dataSnapshot, String username) {
        //sets the isDeleted flag true ONLY IF the account is already created
        //this way you cannot remove a non-existent account
        for(DataSnapshot ds: dataSnapshot.getChildren()) {
            if(ds.getKey().equals(username)) {
                FirebaseDatabase.getInstance().getReference("Accounts/"+username+"/Is Deleted").setValue(true);
            }
        }
    }

    //add/remove book functions
    //first uses the second but feeds default values for non-initial fields: rating, rating count, cover image, and reserver
    static void addBook(String title, String author, String description, String ISBN, String deweyIndexNumber, String coverURL, String publisher, String datePublished, String publicationLocation) {
        Bitmap coverBitmap = URLtoBitmap(coverURL);
        Book b = new Book(ISBN, title, author, description, -1, -1, deweyIndexNumber, "Not Currently Checked Out", coverBitmap, "Not Reserved", publisher, datePublished, publicationLocation);

        updateBook(b);
    }
    static void addBook(String title, String author, String description, float rating, int ratingCount, String ISBN, String deweyIndexNumber, String currentOwner, Bitmap cover, String reservedTo, String publisher, String datePublished, String publicationLocation) {
        //simply creates the book object
        Book b = new Book(ISBN, title, author, description, rating, ratingCount, deweyIndexNumber, currentOwner, cover, reservedTo, publisher, datePublished, publicationLocation);
        //utilizes updateBook, as it writes the point, meaning if it doesn't exist it will create it
        updateBook(b);
    }
    static void removeBook(String ISBN) {
        //removes the book with the given ISBN
        FirebaseDatabase.getInstance().getReference("Books/"+ISBN).removeValue();
    }

    //removes any given token from all accounts
    static void removeTokenFromAccounts(DataSnapshot dataSnapshot, String token) {
        //cycles through accounts in the database
        for(DataSnapshot account: dataSnapshot.child("Accounts").getChildren()) {
            //just gets the account
            Account a = getAccount(dataSnapshot, account.getKey());
            if(a == null) {
                Log.e(TAG, "ERROR: Account in database is not gettable. THIS SHOULD NOT HAPPEN! CONTACT ZACK!");
            }
            //checks if the account has the device in question, and removes it if so
            else if(a.getDeviceTokens().contains(token)) {
                a.removeDeviceToken(token);
            }
        }
    }

    //conversion functions for Cover Bitmap/Base64 String/Image URL
    static String bitmapToString(Bitmap bmp) {
        //returns the default for a null bitmap
        if(bmp == null) {
            return "Cover Image Not Available";
        }

        //converts the bitmap into a byteArray
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream .toByteArray();

        //encodes the byteArray in base64 and returns it as a string
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }
    static Bitmap stringToBitmap(String str) {
        //decodes the string into a byteArray then a bitmap and returns it
        byte[] byteArray = Base64.decode(str, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
    }
    static Bitmap URLtoBitmap(String src) throws NetworkOnMainThreadException{
        try {
            //connects to the url, gets the image, and returns it as a bitmap
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            return BitmapFactory.decodeStream(input);
        } catch (IOException e) {
            //catches any exceptions that might rise
            Log.e(TAG, e.getMessage());
            return null;
        }
    }

}
