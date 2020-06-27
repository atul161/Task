package com.example.deqodetask.Utils;


import android.app.DownloadManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Random;

public class Helper {
    //Url to doenload a js file
    public static final String  MAIN_URL = "https://www.dropbox.com/s/p5mbfwcthfztcfw/test-task.js?dl=1";
    //Name of the js file
    public static final String  JS_FILE = "js-task.js";
    public static final String OPERATION = "Operation:";
    public static final String MESSAGE = "Message:";
    //Bridge is the injected interface to the html
    public static final String BRIDGE = "Bridge";
    //JsCode will be updated when ever we the download complete
    public static String JsCode = null;
    public static final int TASK = 20;
    //Reading the javascript file
    //Reading from the downloaded javascript file
    //and replacing WebKit to Bridge bcz according to documentation
    //there must me a single length of the Interface
    public static  String readFromFile(String path) throws IOException {
        File file = new File(path,"/js-task.js");
        int length = (int) file.length();
        byte[] bytes = new byte[length];

        FileInputStream in = new FileInputStream(file);
        try {
            in.read(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            in.close();
        }
        return  new String(bytes).replaceAll("window.webkit.messageHandlers.operation", BRIDGE);
    }


    public boolean isFileExisit(String path){
        File file = new File(path,"/js-task.js");
        if(file.exists()){
            return true;
        }
        return false;
    }

    //After Reading from the downloaded file
    //We will Inject the string into the html
    //So we can get a full html file
    //And Load into the web view easily
    public static String injectJsToHtml(String jsCode){

        //Create a new html
        return "<!DOCTYPE html>\n" +
                "<html>\n" +
                "  <body>\n" +
                "        <script>\n" +
                jsCode+"\n"+
                "        </script>\n" +
                " </body>\n" +
                "</html>";
    }


    //This will Download the file from the given url
    //It will also show the notification above
    public static void startDownloading(Context context){
        String url =  Helper.MAIN_URL.trim();
        //create download request
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
        //allow types of network to download files
        request.setTitle("Downloading Javascript file");
        request.setDescription("This file is neccesary to complete the task.");
        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, ""+ Helper.JS_FILE);
        //get Download service and enque file
        DownloadManager downloadManager = (DownloadManager)context.getSystemService(Context.DOWNLOAD_SERVICE);
        downloadManager.enqueue(request);
    }

// ICMP A ping Server which is too fast to check internet connection
    public boolean isOnline() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int     exitValue = ipProcess.waitFor();
            return (exitValue == 0);
        }
        catch (IOException e)          { e.printStackTrace(); }
        catch (InterruptedException e) { e.printStackTrace(); }

        return false;
    }

    public  String getSaltString() {
        String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < 18) { // length of the random string.
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        String saltStr = salt.toString();
        return saltStr;

    }

    public boolean haveNetworkConnection(Context context) {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }
}
