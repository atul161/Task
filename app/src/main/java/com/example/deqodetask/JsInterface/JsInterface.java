package com.example.deqodetask.JsInterface;

import android.content.Context;
import android.util.Log;
import android.webkit.JavascriptInterface;

import androidx.appcompat.app.AppCompatActivity;

import com.example.deqodetask.MainActivity;
import com.example.deqodetask.Model.ProgressModel;
import com.example.deqodetask.adapter.JsViewAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

//JsInterface is a Bridge which is called by the Javascript
public class JsInterface {
    private Context context;
    private JsViewAdapter  recyclerViewAdapter;
    private ArrayList<ProgressModel> progressModel;

    public JsInterface(Context context , JsViewAdapter recyclerViewAdapter , ArrayList<ProgressModel> progressModel){
        this.context = context;
        this.recyclerViewAdapter = recyclerViewAdapter;
        this.progressModel = progressModel;
    }
    //This method will be called by javascript
    @JavascriptInterface
    public void postMessage(String msg) throws JSONException {
        JSONObject jsonObject = null;
        try {

            jsonObject = new JSONObject(msg);

        } catch (Throwable t) {
            Log.e("My App", "Could not parse malformed JSON");
        }

        boolean isProgress = false;
        Iterator keys = jsonObject.keys();
        while (keys.hasNext()){
            String currentDynamicKey = (String)keys.next();
             if (currentDynamicKey.equals("progress")){
                 isProgress = true;
                 break;
             }
        }

        System.out.println(msg);


        if (isProgress){
            ProgressModel pr =  new ProgressModel(jsonObject.get("id").toString(),jsonObject.get("message").toString(), Integer.parseInt(jsonObject.get("progress").toString()));
            progressModel.set(Integer.parseInt(jsonObject.get("id").toString()) , pr);

        }else {
            ProgressModel pq = progressModel.get(Integer.parseInt(jsonObject.get("id").toString()));
            ProgressModel pr =  new ProgressModel(jsonObject.get("id").toString(),jsonObject.get("message").toString(), pq.getProgress());
            progressModel.set(Integer.parseInt(jsonObject.get("id").toString()) , pr);
        }

    }

}
