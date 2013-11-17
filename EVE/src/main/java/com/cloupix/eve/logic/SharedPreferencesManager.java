package com.cloupix.eve.logic;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by AlonsoUSA on 16/11/13.
 */
public class SharedPreferencesManager
{
    public static String PREF_ACCOUNT_USER_NAME = "pref_account_user_name";

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public SharedPreferencesManager(Context context) {
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public String getAccountUserName()
    {
        return sharedPreferences.getString(PREF_ACCOUNT_USER_NAME, "");
    }

    public void setAccountUserName(String userName)
    {
        if(editor==null)
            this.editor = sharedPreferences.edit();
        editor.putString(PREF_ACCOUNT_USER_NAME, userName);
        editor.commit();
    }
}
