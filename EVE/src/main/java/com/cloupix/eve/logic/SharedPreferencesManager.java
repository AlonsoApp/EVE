package com.cloupix.eve.logic;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by AlonsoApp on 16/11/13.
 */
public class SharedPreferencesManager
{
    public static String PREF_ACCOUNT_USER_EMAIL = "pref_account_user_email";
    public static String PREF_ACCOUNT_USER_FULL_NAME = "pref_account_user_full_name";
    public static String PREF_ACCOUNT_USER_ID = "pref_account_id_user";
    public static String PREF_ACCOUNT_USER_PROFILE_IMAGE_ID = "pref_account_id_user_profile_image_id";

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public SharedPreferencesManager(Context context) {
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public String getAccountUserName()
    {
        return sharedPreferences.getString(PREF_ACCOUNT_USER_EMAIL, "");
    }

    public String getUserFullName(){
        return sharedPreferences.getString(PREF_ACCOUNT_USER_FULL_NAME, "");
    }

    public long getAccountUserId(){
        return sharedPreferences.getLong(PREF_ACCOUNT_USER_ID, -1L);
    }

    public long getUserProfileImageId(){
        return sharedPreferences.getLong(PREF_ACCOUNT_USER_PROFILE_IMAGE_ID, -1L);
    }

    public void setAccountUserFullName(String userName)
    {
        if(editor==null)
            this.editor = sharedPreferences.edit();
        editor.putString(PREF_ACCOUNT_USER_FULL_NAME, userName);
        editor.commit();
    }

    public void setAccountUserName(String userName)
    {
        if(editor==null)
            this.editor = sharedPreferences.edit();
        editor.putString(PREF_ACCOUNT_USER_EMAIL, userName);
        editor.commit();
    }

    public void setAccountUserId(long idUser)
    {
        if(editor==null)
            this.editor = sharedPreferences.edit();
        editor.putLong(PREF_ACCOUNT_USER_ID, idUser);
        editor.commit();
    }

    public void setAccountUserProfileImageId(long idProfileImage)
    {
        if(editor==null)
            this.editor = sharedPreferences.edit();
        editor.putLong(PREF_ACCOUNT_USER_PROFILE_IMAGE_ID, idProfileImage);
        editor.commit();
    }

}
