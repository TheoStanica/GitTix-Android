package com.example.gittixapp;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManagement {
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String SHARED_PREF_NAME = "session";
    String SESSION_KEY = "session_user";

    public SessionManagement(Context context){
        sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void saveSession(String session){
        // save user session whenever user is logged in
        editor.putString(SESSION_KEY, session).commit();
    }

    public String getSession(){
        // return user whose session is saved
        return sharedPreferences.getString(SESSION_KEY, "none");
    }

    public void removeSession(){
        editor.putString(SESSION_KEY, "none").commit();
    }
}
