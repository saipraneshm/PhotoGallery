package com.bignerdranch.android.photogallery.model;

import android.net.Uri;

/**
 * Created by sai pranesh on 31-Aug-16.
 */
public class GalleryItem {

    private String mId;
    private String mUrl;
    private String mCaption;
    private String mOwner;

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getCaption() {
        return mCaption;
    }

    public void setCaption(String caption) {
        mCaption = caption;
    }

    public String getUrl() {
        return mUrl;
    }

    public void setUrl(String url) {
        mUrl = url;
    }

    public String getOwner() {
        return mOwner;
    }

    public void setOwner(String owner) {
        mOwner = owner;
    }

    public Uri getPhotoPageUri(){
        return  Uri.parse("http://www.flickr.com/photos/")
                        .buildUpon()
                        .appendPath(mOwner)
                        .appendPath(mId)
                        .build();
    }
    @Override
    public String toString() {
        return getCaption();
    }

}
