package com.star.photogallery;


import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.ArrayList;

public class PollService extends IntentService {

    private static final String TAG = "PollService";

    public PollService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        ConnectivityManager connectivityManager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        boolean isNetworkAvailable = connectivityManager.getBackgroundDataSetting() &&
                connectivityManager.getActiveNetworkInfo() != null;

        if (!isNetworkAvailable) {
            return;
        }

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String query = prefs.getString(FlickrFetchr.PREF_SEARCH_QUERY, null);
        String lastResultId = prefs.getString(FlickrFetchr.PREF_LAST_RESULT_ID, null);

        ArrayList<GalleryItem> items;

        if (query != null) {
            items = new FlickrFetchr().search(query, 1);
        } else {
            items = new FlickrFetchr().fetchItems(1);
        }

        if (items.size() == 0) {
            return;
        }

        String resultId = items.get(0).getId();

        if (!resultId.equals(lastResultId)) {
            Log.i(TAG, "Got a new result: " + resultId);
        } else {
            Log.i(TAG, "Got an old result: " + resultId);
        }

        prefs.edit().putString(FlickrFetchr.PREF_LAST_RESULT_ID, resultId).commit();

    }
}
