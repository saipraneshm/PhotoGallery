package com.bignerdranch.android.photogallery.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.bignerdranch.android.photogallery.service.PollService;
import com.bignerdranch.android.photogallery.utility.QueryPreferences;

/**
 * Created by sai pranesh on 04-Sep-16.
 */
public class StartupReceiver extends BroadcastReceiver {

    private static final String TAG = "StartupReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "Received broadcast intent: " + intent.getAction());

        boolean isOn = QueryPreferences.getIsAlarmOn(context);
        PollService.setServiceAlarm(context,isOn);

    }
}
