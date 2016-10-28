package com.bignerdranch.android.photogallery.service;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.bignerdranch.android.photogallery.R;
import com.bignerdranch.android.photogallery.activity.PhotoGalleryActivity;
import com.bignerdranch.android.photogallery.model.GalleryItem;
import com.bignerdranch.android.photogallery.utility.FlickrFetchr;
import com.bignerdranch.android.photogallery.utility.QueryPreferences;

import java.util.List;

/**
 * Created by sai pranesh on 01-Sep-16.
 */
public class PollService extends IntentService {

    private static final String TAG = "PollService";

    public static final String ACTION_SHOW_NOTIFICATION = "com.bignerdranch.android.photogallery.service.PollService.SHOW_NOTIFICATION";

    public  static final String PERM_PRIVATE = "com.bignerdranch.android.photogallery.PRIVATE";

    public static final String REQUEST_CODE = "REQUEST_CODE";

    public static final String NOTIFICATION = "NOTIFICATION";

    private static final long POll_INTERVALS = 1000 * 60;

    public static Intent newIntent(Context context){
        return new Intent(context,PollService.class);
    }

    public PollService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        if(!isNetworkAvailableAndConnected()){
            return;
        }

        String query = QueryPreferences.getStoredQuery(this);
        String lastResultId = QueryPreferences.getLastResultId(this);
        List<GalleryItem> items;

        if(query == null){
            items = new FlickrFetchr().fetchRecentPhotos();
        }else{
            items = new FlickrFetchr().searchPhotos(query);
        }
        String resultId = items.get(0).getId();

        if(resultId.equals(lastResultId)){
            Log.i(TAG,"Got an old result: " + resultId);
        }else{
            Log.i(TAG,"Got a new result: " + resultId);
            Resources resources = getResources();
            Intent i = PhotoGalleryActivity.newIntent(this);
            PendingIntent pi = PendingIntent
                    .getActivity(this,0,i,0);

            Notification notification = new NotificationCompat
                                        .Builder(this)
                                        .setTicker(resources.getString(R.string.new_pictures_title))
                                        .setSmallIcon(android.R.drawable.ic_menu_report_image)
                                        .setContentTitle(resources.getString(R.string.new_pictures_title))
                                        .setContentText(resources.getString(R.string.new_pictures_text))
                                        .setContentIntent(pi)
                                        .setAutoCancel(true)
                                        .build();

            /*NotificationManagerCompat notificationManagerCompat =
                        NotificationManagerCompat.from(this);
            notificationManagerCompat.notify(0, notification);*/

            //sendBroadcast((new Intent(ACTION_SHOW_NOTIFICATION)),PERM_PRIVATE);
            showBackgroundNotification(0,notification);

        }
        QueryPreferences.setLastResultId(this,resultId);
    }

    private void showBackgroundNotification(int requestCode, Notification notification){
            Intent i = new Intent(ACTION_SHOW_NOTIFICATION);
            i.putExtra(REQUEST_CODE,requestCode);
            i.putExtra(NOTIFICATION,notification);
            sendOrderedBroadcast(i,PERM_PRIVATE,null,null, Activity.RESULT_OK,null,null);
    }

    private Boolean isNetworkAvailableAndConnected(){
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        boolean isNetworkAvailable = cm.getActiveNetworkInfo().isAvailable();
        boolean isNetworkConnected = isNetworkAvailable &&
                        cm.getActiveNetworkInfo().isConnected();

        return isNetworkConnected;
    }

    public static void setServiceAlarm(Context context, boolean isOn){
       /* if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){}*/
        Intent i = PollService.newIntent(context);
        PendingIntent pi = PendingIntent.getService(context,0,i,0);

        AlarmManager alarmManager = (AlarmManager) context
                            .getSystemService(Context.ALARM_SERVICE);
        if(isOn){
            alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME,
                    SystemClock.elapsedRealtime(),
                    POll_INTERVALS,pi);
        }
        else{
            alarmManager.cancel(pi);
            pi.cancel();
        }
        QueryPreferences.setIsAlarmOn(context,isOn);
    }

    public static boolean isServiceAlarmOn(Context context){
        Intent i = PollService.newIntent(context);
        PendingIntent pi = PendingIntent.getService(context,0,i
                ,PendingIntent.FLAG_NO_CREATE);
        return pi != null;
    }
}
