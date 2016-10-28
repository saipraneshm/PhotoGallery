package com.bignerdranch.android.photogallery.utility;

import android.net.Uri;
import android.util.Log;

import com.bignerdranch.android.photogallery.model.GalleryItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sai pranesh on 31-Aug-16.
 */
public class FlickrFetchr {

    private static final String TAG = "FlickrFetchr";
    private static final String API_KEY = "9beb7c172f11e9f3eda3cd69dbfd7705";
    private static final String FETCH_RECENTS_METHOD = "flickr.photos.getRecent";
    private static final String SEARCH_METHOD = "flickr.photos.search";
    private static final Uri ENDPOINT =  Uri
            .parse("https://api.flickr.com/services/rest/")
            .buildUpon()
            .appendQueryParameter("api_key",API_KEY)
            .appendQueryParameter("nojsoncallback","1")
            .appendQueryParameter("extras","url_s")
            .appendQueryParameter("format","json")
            .build();

    public  byte[] getUrlBytes(String urlSpec) throws IOException {

        URL url = new URL(urlSpec);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        try{
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream in = connection.getInputStream();

            if(connection.getResponseCode() != HttpURLConnection.HTTP_OK){
                throw new IOException(connection.getResponseMessage() + " for url:" +
                            urlSpec);
            }

            int bytesRead = 0;
            byte[] buffer = new byte[1024];
            while((bytesRead = in.read(buffer)) > 0){
                out.write(buffer,0,bytesRead);
            }
            out.close();
            return out.toByteArray();
        }finally {
            connection.disconnect();
        }

    }

    public String getUrlString(String url) throws IOException {
        return new String(getUrlBytes(url));
    }

    private  List<GalleryItem> downloadGalleryItem(String url) {
        List<GalleryItem> galleryItems = new ArrayList<>();
        try {
            String jsonString = getUrlString(url);
            JSONObject jsonObject = new JSONObject(jsonString);

            parseItems(galleryItems,jsonObject);
            //Log.i(TAG,jsonString);
        } catch (JSONException e) {
            e.printStackTrace();
        }catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG,"unable to load ", e );
        }
        return galleryItems;
    }

    public String urlBuilder(String method,String query){
        Uri.Builder uriBuilder = ENDPOINT
                    .buildUpon()
                    .appendQueryParameter("method",method);
        if(method.equals(SEARCH_METHOD)){
            uriBuilder
                    .appendQueryParameter("text",query);
        }

        return uriBuilder.build().toString();
    }

    public List<GalleryItem> fetchRecentPhotos(){
        String url = urlBuilder(FETCH_RECENTS_METHOD,null);
        return downloadGalleryItem(url);
    }

    public List<GalleryItem> searchPhotos(String query){
        String url = urlBuilder(SEARCH_METHOD,query);
        return downloadGalleryItem(url);
    }

    public void parseItems(List<GalleryItem> galleryItems,
                                        JSONObject jsonBody) throws JSONException {

        JSONObject photosJSONObject = jsonBody.getJSONObject("photos");
        JSONArray photosJSONArray = photosJSONObject.getJSONArray("photo");
        for (int i = 0; i < photosJSONArray.length(); i ++){
            JSONObject photoJSONObject = photosJSONArray.getJSONObject(i);
            GalleryItem item = new GalleryItem();
            String id = photoJSONObject.getString("id");
            item.setId(id);
            String title = photoJSONObject.getString("title");
            item.setCaption(title);

            if(!photoJSONObject.has("url_s")){
                continue;
            }

            item.setUrl(photoJSONObject.getString("url_s"));
            item.setOwner(photoJSONObject.getString("owner"));
            galleryItems.add(item);
        }
    }
}
