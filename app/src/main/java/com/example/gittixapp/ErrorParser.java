package com.example.gittixapp;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ErrorParser {
    Context context;
    String stringToParse;
    Activity activity;

    public ErrorParser(Activity activity, Context context, String stringToParse) {
        this.context = context;
        this.stringToParse = stringToParse;
        this.activity = activity;
    }

    public void displayErrors(){
        JSONObject resultJSON = null;
        try {
            // parsing the errors
            resultJSON = new JSONObject(stringToParse);
            JSONArray errors = resultJSON.getJSONArray("errors");
            for(int i = 0; i < errors.length(); i++){
                JSONObject oneError = errors.getJSONObject(i);
                String message = oneError.getString("message");

                // show error in Toast Message
                new Thread(){
                    public void run() {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }.start();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
