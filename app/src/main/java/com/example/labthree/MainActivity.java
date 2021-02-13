package com.example.labthree;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.Permission;
import java.util.Random;

public class MainActivity extends AppCompatActivity {


    private Button openMenuBtn, downloadBtn, newBtn;
    private WebView webView;
    private WebSettings webSettings;
    DownloadFile download;
    private ProgressBar progressBar;
    ProgressDialog mProgressDialog;
    int progress_bar_type;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (getIntent().hasExtra("click_action")) {

            Intent intent = new Intent(this, Notification.class);
            startActivity(intent);
            finish();

        }
        openMenuBtn = (Button) findViewById(R.id.openmenu_btn);
        downloadBtn = (Button) findViewById(R.id.download_btn);
        newBtn = (Button) findViewById(R.id.new_btn);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setMax(10);

        newBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Notification.class);
                startActivity(intent);
            }
        });

        openMenuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setContentView(R.layout.web_layout);
                webView = (WebView) findViewById(R.id.web_holder);

                webView.setWebViewClient(new WebViewClient());
                webView.loadUrl("https://drive.google.com/file/d/1Q21KMK0iHqKMsLkI3cvrXql6LzgHAJLh/view?export?format=pdf");
                webSettings = webView.getSettings();
                webView.getSettings().setSupportMultipleWindows(true);
                webSettings.setJavaScriptEnabled(true);
                webSettings.setSupportZoom(true);

            }
        });


        int permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }

        download = new DownloadFile();
        downloadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //so that it does crash if user presses the button more than once
                //as async tasks can only be executed once
                syncTasks();


            }
        });

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Downloading, please wait...");
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setMax(100);
        mProgressDialog.setProgress(0);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setCancelable(true);

       /* mProgressDialog = new ProgressDialog(MainActivity.this);
        mProgressDialog.setMessage("Downloading...");
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);


        */
        progress_bar_type = 0;
    }
    private void syncTasks() {
        try {
            if (download.getStatus() != AsyncTask.Status.RUNNING){   // check if asyncTasks is running
                download.cancel(true); // asyncTasks not running => cancel it
                download = new DownloadFile(); // reset task
                download.execute("https://imgur.com/a/lPn8W7N"); // execute new task (the same task)
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("MainActivity_TSK", "Error: "+e.toString());
        }
    }



    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();

        } else {
            super.onBackPressed();
        }
    }




    class DownloadFile extends AsyncTask<String, String, String> {
        /**
         * Before starting background thread
         * Show Progress Bar Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog.show();

        }

        /**
         * Downloading file in background thread
         *
         */
        @Override
        protected String doInBackground(String...murl) {

            try {

                URLConnection connection =
                        new URL("https://imgur.com/CEfOrWp.png").openConnection();
                InputStream response = connection.getInputStream();

                Bitmap bitmap = BitmapFactory.decodeStream(response);
                    String filename = "boba.png";
                    File sd = Environment.getExternalStorageDirectory();
                    File dest = new File(sd.getAbsolutePath(), filename);
                    mProgressDialog.incrementProgressBy(100);

                try {
                        FileOutputStream out = new FileOutputStream(dest);
                        bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);

                        out.flush();
                        out.close();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }
        /**
         * Updating progress bar
         * */
        protected void onProgressUpdate(String... progress)
        {
            mProgressDialog.setProgress(Integer.parseInt(progress[0]));

        }


        /**
         * After completing background task
         * Dismiss the progress dialog
         * **/
        @Override
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after the file was downloaded
            mProgressDialog.dismiss();
            Toast.makeText(getApplicationContext(),"boba.png" +" downloaded.", Toast.LENGTH_LONG).show();
        }

    }
}