package com.bignerdranch.android.photogallery.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

import com.bignerdranch.android.photogallery.activity.BaseActivity.SingleFragmentActivity;
import com.bignerdranch.android.photogallery.fragment.PhotoGalleryFragment;

public class PhotoGalleryActivity extends SingleFragmentActivity {


    public static Intent newIntent(Context context){
        return new Intent(context,PhotoGalleryActivity.class);
    }

    @Override
    protected Fragment createFragment() {
        return PhotoGalleryFragment.newInstance();
    }

}
