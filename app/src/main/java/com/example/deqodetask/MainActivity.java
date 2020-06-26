package com.example.deqodetask;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.crowdfire.cfalertdialog.CFAlertDialog;
import com.example.deqodetask.JsInterface.JsInterface;
import com.example.deqodetask.Model.ProgressModel;
import com.example.deqodetask.Utils.Helper;
import com.example.deqodetask.adapter.JsViewAdapter;
import com.google.android.material.snackbar.Snackbar;



import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;


public class MainActivity extends AppCompatActivity {


    //let us Assume there will be 50 progress Task
    private int count = 100;
    // Defining Permission codes.
    // We can give any value
    // but unique for each permission.
    private    RecyclerView rvVertical;
    private   final int PERMISSIONS_MULTIPLE_REQUEST = 123;
    public ArrayList<ProgressModel> progressModel;
    boolean isPermissionAvail = false;
    String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
    Helper helper = new Helper();
    private  JsViewAdapter recyclerViewAdapter;
    boolean isPer = false;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        progressBar = findViewById(R.id.progressBar_cyclic);
        //Checking the permission
        //Below Marshmallow we dont need to check
        //Above marsh mallow need runtime permission
        //When the download will be complete this broadcast will be called from
        //Download Manager
        checkAndroidVersion();
        //If file does not exisit
         if (!helper.isFileExisit(path)){
             if(!helper.haveNetworkConnection(getApplicationContext())){
                 CFAlertDialog.Builder builder = new CFAlertDialog.Builder(this)
                         .setDialogStyle(CFAlertDialog.CFAlertStyle.ALERT)
                         .setTitle("Js Download , Need Internet!")
                         .setMessage("First time when you will install our app we need to download js file")
                         .addButton("QUIT", -1, -1, CFAlertDialog.CFAlertActionStyle.NEGATIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (dialog, which) -> {
                             dialog.dismiss();
                             finish();
                         }).addButton("Retry", -1 ,-1 , CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (dialog, which) -> {
                            if (helper.haveNetworkConnection(getApplicationContext())){
                                dialog.dismiss();
                            }
                         });
                 builder.setCancelable(false);
                 builder.show();
             }
         }
        rvVertical = findViewById(R.id.rvVertical);
        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
                    //After  Downloading start read
                    try {
                        Helper.JsCode = Helper.readFromFile(path);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    progressModel = new ArrayList<>();
                    for (int i = 0; i < count; i++) {
                        progressModel.add(new ProgressModel(Integer.toString(i), "Error", 0));
                    }
                  recyclerViewAdapter = new JsViewAdapter(MainActivity.this, progressModel);
                    rvVertical.setAdapter(recyclerViewAdapter);
                    progressBar.setVisibility(View.GONE);
                    //setup web view
                    //here you can modify no of progress you want
                    setUpWebView(count , progressModel);
                }
            }

        };
        registerReceiver(broadcastReceiver, new IntentFilter(
                DownloadManager.ACTION_DOWNLOAD_COMPLETE));

        if (isPermissionAvail){
            progressModel = new ArrayList<>();
            for (int i = 0; i < count; i++) {
                progressModel.add(new ProgressModel(Integer.toString(i), "Error", 0));
            }
          recyclerViewAdapter = new JsViewAdapter(MainActivity.this, progressModel);
            rvVertical.setAdapter(recyclerViewAdapter);
            progressBar.setVisibility(View.GONE);
            setUpWebView(count , progressModel);

        }

    }

    Handler handler = new Handler();
    Runnable runnable;
    int delay = 1*1000; //Delay for 15 seconds.  One second = 1000 milliseconds.
    @Override
    protected void onResume() {
        //start handler as activity become visible
            handler.postDelayed( runnable = new Runnable() {
                public void run() {
                    //do something
                    if ( recyclerViewAdapter != null){
                        recyclerViewAdapter.notifyDataSetChanged();
                    }
                    handler.postDelayed(runnable, delay);
                }
            }, delay);

        super.onResume();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }


    // If onPause() is not included the threads will double up when you
// reload the activity

    @Override
    protected void onPause() {
        if (isPer){
            handler.removeCallbacks(runnable); //stop handler when activity not visible
        }
        super.onPause();
    }



    @RequiresApi(api = Build.VERSION_CODES.M)
    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) + ContextCompat
                .checkSelfPermission(getApplicationContext(),
                        Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale
                    (MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) ||
                    ActivityCompat.shouldShowRequestPermissionRationale
                            (MainActivity.this, Manifest.permission.READ_PHONE_STATE)) {

                Snackbar.make(MainActivity.this.findViewById(android.R.id.content),
                        "Please Grant All Permissions !",
                        Snackbar.LENGTH_INDEFINITE).setAction("ENABLE",
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                requestPermissions(
                                        new String[]{Manifest.permission
                                                .WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE},
                                        PERMISSIONS_MULTIPLE_REQUEST);
                            }
                        }).show();
            } else {
                requestPermissions(
                        new String[]{Manifest.permission
                                .WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE},
                        PERMISSIONS_MULTIPLE_REQUEST);
            }
        } else {
            //If permission is Already Granted
            //Read the content of js file
            File file = new File(path,"/js-task.js");
            try {
                Helper.JsCode = Helper.readFromFile(path);
                isPermissionAvail = true;
                isPer = true;
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    private void checkAndroidVersion() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkPermission();

        }
    }




    // This function is called when user accept or decline the permission.
// Request Code is used to check which permission called this function.
// This request code is provided when user is prompt for permission.

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case PERMISSIONS_MULTIPLE_REQUEST:
                if (grantResults.length > 0) {
                    boolean readPhonePermission = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    boolean writeExternalFile = grantResults[0] == PackageManager.PERMISSION_GRANTED;

                    if(readPhonePermission && writeExternalFile)
                    {
                        //If all permission Granted
                        //Delete the file if exist and download new one js file
                        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
                        File file = new File(path,"/js-task.js");
                        file.delete();
                        //Now download new one file
                        Helper.startDownloading(MainActivity.this);
                        isPer =  true;

                    } else {
                        Snackbar.make(MainActivity.this.findViewById(android.R.id.content),
                                "Please Grant Permissions!",
                                Snackbar.LENGTH_INDEFINITE).setAction("ENABLE",
                                new View.OnClickListener() {
                                    @RequiresApi(api = Build.VERSION_CODES.M)
                                    @Override
                                    public void onClick(View v) {
                                        MainActivity.this.requestPermissions(
                                                new String[]{Manifest.permission
                                                        .WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE},
                                                PERMISSIONS_MULTIPLE_REQUEST);
                                    }
                                }).show();
                    }
                }
                break;
        }
    }

    public void setUpWebView(int nProgress , ArrayList<ProgressModel> progressModel){
        WebView webView = new WebView(getApplicationContext());
        webView.setWebViewClient(new WebViewClient());
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.setWebChromeClient(new WebChromeClient());
        webView.addJavascriptInterface(new JsInterface(getApplicationContext() ,  recyclerViewAdapter ,progressModel ),Helper.BRIDGE);
        webView.loadData(Helper.injectJsToHtml(Helper.JsCode), "text/html", null);
        webView.setWebViewClient(new WebViewClient(){
            public void onPageFinished(WebView view, String weburl){

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                    for (int i = 0 ; i < nProgress ; i ++ ){
                        webView.evaluateJavascript("startOperation("+Integer.toString(i)+");", null);
                    }
                }
            }
        });

    }




}