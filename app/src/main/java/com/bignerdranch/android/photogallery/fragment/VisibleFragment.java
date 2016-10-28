package com.bignerdranch.android.photogallery.fragment;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.Toast;

import com.bignerdranch.android.photogallery.service.PollService;

/**
 * Created by sai pranesh on 06-Sep-16.
 */
public class VisibleFragment extends Fragment {

    private static final String TAG = "VisibleFragment";

    private BroadcastReceiver mShowNotifications = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            /*Toast.makeText(getActivity(),
                    "Got a broadcast:" + intent.getAction(),
                    Toast.LENGTH_LONG)
                    .show();*/
            Log.i(TAG,"cancelling notification");
            setResultCode(Activity.RESULT_CANCELED);
        }
    };

    @Override
    public void onStart() {
        super.onStart();
        IntentFilter intentFilter = new IntentFilter(PollService.ACTION_SHOW_NOTIFICATION);
        getActivity().registerReceiver(mShowNotifications,intentFilter,PollService.PERM_PRIVATE,null);
    }

    @Override
    public void onStop() {
        super.onStop();
        getActivity().unregisterReceiver(mShowNotifications);
    }
}
