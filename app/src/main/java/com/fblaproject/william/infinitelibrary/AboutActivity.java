package com.fblaproject.william.infinitelibrary;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.MenuItem;
import android.widget.TextView;

/*
* This activity is for the about page. The page is mostly just text, so there's not much here.
* */

public class AboutActivity extends AppCompatActivity {

    //Runs on start of activity
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Default setup code
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        //Creates back button in top left of screen
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Checks if the phone is connected to a network and displays a toast if not
        Utility.isNetworkConnected(AboutActivity.this);

        TextView licensing = (TextView)findViewById(R.id.txtViewLicensingText);
        licensing.setText(Html.fromHtml("MIT License<br />" +
                "<br />" +
                "Copyright (c)  2017 Will Thomas and Zachary Hercher<br />" +
                "<br />" +
                "Permission is hereby granted, free of charge, to any person obtaining a copy" +
                "of this software and associated documentation files (the \"Software\"), to deal" +
                "in the Software without restriction, including without limitation the rights" +
                "to use, copy, modify, merge, publish, distribute, sublicense, and/or sell" +
                "copies of the Software, and to permit persons to whom the Software is" +
                "furnished to do so, subject to the following conditions:<br />" +
                "<br />" +
                "The above copyright notice and this permission notice shall be included in all" +
                "copies or substantial portions of the Software.<br />" +
                "<br />" +
                "THE SOFTWARE IS PROVIDED \"AS IS\", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR" +
                "IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY," +
                "FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE" +
                "AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER" +
                "LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM," +
                "OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE" +
                "SOFTWARE.<br />" +
                "<br />" +
                "<br />" +
                "Infinite Library uses Firebase Realtime Database and Firebase Cloud Functions, " +
                "both of which are produced as Firebase services by Google, Inc. They are both" +
                "licensed under the Creative Commons Attribution 3.0 License, as described in" +
                "their terms in the following link." +
                "&emsp;https://firebase.google.com/terms/<br />" +
                "<br />" +
                "Infinite Library also uses images from Material.io, which is produced and maintained" +
                "by Google, Inc. It is licensed under the Apache License, version 2.0, a copy of which" +
                "can be found at the first link below. Material.io's licensing statement can be found at" +
                "the second link below.<br />" +
                "&emsp;https://www.apache.org/licenses/LICENSE-2.0.html<br />" +
                "&emsp;https://material.io/guidelines/#introduction-principles<br />" +
                "<br />" +
                "Infinite Library uses the Evernote SDK for Job Scheduling, which is copyrighted and" +
                "licensed by Evernote Corporation; the license can be found at the link below.<br />" +
                "&emsp;https://github.com/evernote/evernote-sdk-android/blob/master/README.md<br />" +
                "<br />" +
                "Infinite Library uses the Facebook API, the terms of which can be found at the link below.<br />" +
                "&emsp;https://developers.facebook.com/policy<br />" +
                "<br />" +
                "Infinite Library uses the Twitter API, the terms of which can be found at the link below.<br />" +
                "&emsp;https://developer.twitter.com/en/developer-terms/agreement-and-policy<br />" +
                "<br />" +
                "Infinite Library uses images and information from Amazon, gathered using the Amazon" +
                "AWS API. The terms of this content's use can be found at the link below.<br />" +
                "&emsp;https://affiliate-program.amazon.com/help/operating/policies<br />" +
                "<br />" +
                "The development of Infinite Library involved the use of the Splinter library for python," +
                "which is copyrighted and licensed by splinter authors. The terms of the usage of this" +
                "can be found at the link below.<br />" +
                "&emsp;https://github.com/cobrateam/splinter/blob/master/LICENSE<br />" +
                "<br />" +
                "Much of Infinite Library was developed in Android Studio. The terms of use of Android" +
                "Studio can be found at the link below.<br />" +
                "&emsp;https://developer.android.com/studio/terms.html<br />" +
                "<br />" +
                "Many of the custom assets used in Infinite Library were developed using Paint.net, a free" +
                "image editing software produced by Rick Brewster and contributers. The terms of use and " +
                "licensing of Paint.net can be found at the link below.<br />" +
                "&emsp;https://www.getpaint.net/license.html"));

    }

    //Called when back button is pressed
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:

                //Starts home activity and starts the sliding animation
                Intent homeActivity = new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(homeActivity);
                overridePendingTransition(R.anim.left_slide_in, R.anim.left_slide_out);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
