package com.example.arcibald160.callblocker.tools;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.arcibald160.callblocker.R;

public class SharedPreferencesHelper {

    SharedPreferences sharedPreferences;
    Context mContext;

    public SharedPreferencesHelper(Context context) {
        mContext = context;
        sharedPreferences = context.getSharedPreferences(context.getString(R.string.app_shared_preferences), Context.MODE_PRIVATE);
    }

    public SharedPreferences getSharedPreferences() {
        return sharedPreferences;
    }

    public void setBlockAllState(Boolean isBlockAllActivated) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(mContext.getString(R.string.blockall), isBlockAllActivated);
        editor.commit();
    }

    public boolean isBlockAllActivated() {
        return sharedPreferences.getBoolean(mContext.getString(R.string.blockall), false);
    }
}
