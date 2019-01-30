package com.gen.genqu3;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SaveSharedPreference {

    static final String Name = "nameKey";
    static final String Email = "emailKey";
    static final String Id = "idKey";
    static final String Pass = "passKey";
    static final String fName = "fnameKey";
    static final String lName = "lnameKey";
    static final String Num = "numKey";
    static final String Mes = "mesKey";
    static final String Tranid = "tranidKey";
    static final String Mes2 = "mes2Key";

    static SharedPreferences getSharedPreferences(Context ctx){
        return PreferenceManager.getDefaultSharedPreferences(ctx);
    }

    public static void setUserName(Context ctx, String userName){
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(Name, userName);
        editor.commit();
    }

    public static void setfName(Context ctx, String firstName){
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(fName, firstName);
        editor.commit();
    }

    public static void setlName(Context ctx, String lastName){
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(lName, lastName);
        editor.commit();
    }

    public static void setMes(Context ctx, String mes){
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(Mes, mes);
        editor.commit();
    }

    public static void setTranId(Context ctx, String tranid){
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(Tranid, tranid);
        editor.commit();
    }

    public static void setMes2(Context ctx, String mes2){
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(Mes2, mes2);
        editor.commit();
    }

    public static void setNum(Context ctx, String number){
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(Num, number);
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
    public static String getUserfName(Context ctx){
        return getSharedPreferences(ctx).getString(fName,"");
    }
    public static String getUserlName(Context ctx){
        return getSharedPreferences(ctx).getString(lName,"");
    }
    public static String getUserNum(Context ctx){
        return getSharedPreferences(ctx).getString(Num,"");
    }
    public static String getMes(Context ctx){
        return getSharedPreferences(ctx).getString(Mes,"");
    }
    public static String getTranId(Context ctx){
        return getSharedPreferences(ctx).getString(Tranid,"");
    }
    public static String getMes2(Context ctx){
        return getSharedPreferences(ctx).getString(Mes2,"");
    }

}
