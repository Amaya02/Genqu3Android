package com.gen.genqu3;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SaveSharedPreference {

    static final String Name = "nameKey";
    static final String Email = "emailKey";
    static final String Id = "idKey";
    static final String Pass = "passKey";

    static SharedPreferences getSharedPreferences(Context ctx){
        return PreferenceManager.getDefaultSharedPreferences(ctx);
    }

    public static void setUserName(Context ctx, String userName){
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(Name, userName);
        editor.commit();
    }

    public static void setUserEmail(Context ctx, String userEmail){
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(Email, userEmail);
        editor.commit();
    }

    public static void setUserId(Context ctx, String userId){
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(Id, userId);
        editor.commit();
    }

    public static void setUserPass(Context ctx, String userPass){
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(Pass, userPass);
        editor.commit();
    }

    public static String getUserName(Context ctx){
        return getSharedPreferences(ctx).getString(Name,"");
    }

    public static String getUserEmail(Context ctx){
        return getSharedPreferences(ctx).getString(Email,"");
    }
    public static String getUserPass(Context ctx){
        return getSharedPreferences(ctx).getString(Pass,"");
    }
    public static String getUserId(Context ctx){
        return getSharedPreferences(ctx).getString(Id,"");
    }

}
