package com.bignerdranch.android.photogallery.broadcast;

import android.app.Activity;
import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.bignerdranch.android.photogallery.service.PollService;

/**
 * Created by sai pranesh on 06-Sep-16.
 */
public class NotificationReceiver extends BroadcastReceiver {

    private static final String TAG = "NotificationReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.i(TAG,"received result: " + getResultCode());
        if( getResultCode() != Activity.RESULT_OK){
            return;
        }

        int requestCode = intent.getIntExtra(PollService.REQUEST_CODE,0);
        Notification notification = intent
                                        .getParcelableExtra(PollService.NOTIFICATION);

        NotificationManagerCompat notificationManager =
                NotificationManagerCompat.from(context);
        notificationManager.notify(requestCode,notification);

    }
}
