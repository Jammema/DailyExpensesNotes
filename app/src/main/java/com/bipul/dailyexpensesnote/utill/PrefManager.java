package com.bipul.dailyexpensesnote.utill;

import android.content.Context;
import android.content.SharedPreferences;

public class PrefManager {
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context _context;
    int PRIVATE_MODE = 0;

    public PrefManager(Context context) {

        this._context = context;
        pref = _context.getSharedPreferences(SharedPref.SHARED_PREFERENCE_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }
    public boolean isFirstTimeLaunch() {

        return pref.getBoolean(SharedPref.IS_FIRST_TIME_LAUNCH, true);
    }
    public void setFirstTimeLaunch(boolean isFirstTime) {

        editor.putBoolean(SharedPref.IS_FIRST_TIME_LAUNCH, isFirstTime);
        editor.commit();
    }
}
