package com.bignerdranch.android.photogallery.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.bignerdranch.android.photogallery.activity.BaseActivity.SingleFragmentActivity;
import com.bignerdranch.android.photogallery.fragment.PhotoPageFragment;

public class PhotoPageActivity extends SingleFragmentActivity{


    public static Intent newIntent(Context context, Uri uri){
        Intent intent = new Intent(context,PhotoPageActivity.class);
        intent.setData(uri);
        return intent;
    }

    @Override
    protected Fragment createFragment() {
        return PhotoPageFragment.newInstance(getIntent().getData());
    }
}
