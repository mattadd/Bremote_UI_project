package com.example.mariamaddai.bremote_ui;

import android.content.SharedPreferences;
import android.content.Context;


/**
 * Created by mariam addai on 01-Apr-18.
 */

public class Session {
    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    Context ctx;

    public Session(Context ctx){
        this.ctx=ctx;
        prefs=ctx.getSharedPreferences("b-remote", Context.MODE_PRIVATE);
        editor=prefs.edit();
    }

    public void setLoggedin(boolean loggedin){
        editor.putBoolean("loggedInmode",loggedin);
        editor.commit();
    }
    public boolean loggedIn(){
        return prefs.getBoolean("loggedInmode", false);
    }
}

