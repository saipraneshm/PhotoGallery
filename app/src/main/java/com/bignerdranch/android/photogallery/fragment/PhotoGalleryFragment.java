package com.bignerdranch.android.photogallery.fragment;


import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;

import com.bignerdranch.android.photogallery.activity.PhotoPageActivity;
import com.bignerdranch.android.photogallery.utility.QueryPreferences;
import com.bignerdranch.android.photogallery.R;
import com.bignerdranch.android.photogallery.model.GalleryItem;
import com.bignerdranch.android.photogallery.service.PollService;
import com.bignerdranch.android.photogallery.utility.FlickrFetchr;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class PhotoGalleryFragment extends VisibleFragment {


    RecyclerView mRecyclerView;
    List<GalleryItem> mGalleryItems= new ArrayList<>();
    private static final String TAG = "PhotoGalleryFragment";
    public PhotoGalleryFragment() {
        // Required empty public constructor
    }


    public static Fragment newInstance(){
        return new PhotoGalleryFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);
        getActivity().startService(PollService.newIntent(getActivity()));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       View view = inflater.inflate(R.layout.fragment_photo_gallery,
                                container,false);
        mRecyclerView = (RecyclerView)view.findViewById(R.id.fragment_photo_gallery_recycler_view);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(),3));
        updateUI();
        return view;
    }

    public class FetchItemsTask extends AsyncTask<Void,Void,List<GalleryItem>>{

        String mQuery;

        FetchItemsTask(String query){
            mQuery = query;
        }

        @Override
        protected List<GalleryItem> doInBackground(Void... voids) {

                if(mQuery == null){
                    return new FlickrFetchr().fetchRecentPhotos();
                }else{
                    return new FlickrFetchr().searchPhotos(mQuery);
                }
        }

        @Override
        protected void onPostExecute(List<GalleryItem> galleryItems) {
            super.onPostExecute(galleryItems);
            mGalleryItems = galleryItems;
            setUpAdapter();
        }
    }

    public class GalleryItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        //TextView mCaption;
        ImageView mPhotoView;
        GalleryItem mGalleryItem;

        public GalleryItemHolder(View itemView) {
            super(itemView);
            //mCaption = (TextView) itemView;
            mPhotoView = (ImageView) itemView
                    .findViewById(R.id.fragment_photo_gallery_image_view);
            mPhotoView.setOnClickListener(this);
        }

        /*void bindGalleryItem(GalleryItem item){
            mCaption.setText(item.getCaption());
        }*/
        void bindGalleryItem(GalleryItem item){
            mGalleryItem = item;
            Picasso.with(getActivity())
                    .load(item.getUrl())
                    .placeholder(android.R.drawable.ic_menu_camera)
                    .into(mPhotoView);
        }


        @Override
        public void onClick(View view) {
           /* Intent intent = new Intent(Intent.ACTION_VIEW,mGalleryItem.getPhotoPageUri());*/
            Intent intent = PhotoPageActivity.newIntent(getActivity(),mGalleryItem.getPhotoPageUri());
            startActivity(intent);
        }
    }

    public class GalleryItemAdapter extends RecyclerView.Adapter<GalleryItemHolder>{

        List<GalleryItem> mGalleryItems;

        public GalleryItemAdapter(List<GalleryItem> galleryItems) {
            mGalleryItems = galleryItems;
        }

        @Override
        public GalleryItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
           LayoutInflater inflater = LayoutInflater.from(getActivity());
            View view = inflater.inflate(R.layout.photo_gallery_item,parent,false);
            return new GalleryItemHolder(view);
        }

        @Override
        public void onBindViewHolder(GalleryItemHolder holder, int position) {
            GalleryItem item = mGalleryItems.get(position);
            holder.bindGalleryItem(item);
        }

        @Override
        public int getItemCount() {
            return mGalleryItems.size();
        }
    }

    void setUpAdapter(){
        if(isAdded()){
            mRecyclerView.setAdapter(new GalleryItemAdapter(mGalleryItems));
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_photo_gallery,menu);

        MenuItem menuItem = menu.findItem(R.id.fragment_photo_search_view);
        final SearchView searchView = (SearchView) menuItem.getActionView();

        MenuItem togglePollItem = menu.findItem(R.id.menu_item_toggle_polling);

        if(PollService.isServiceAlarmOn(getActivity())){
            togglePollItem.setTitle(getString(R.string.stop_polling));
        }else{
            togglePollItem.setTitle(getString(R.string.start_polling));
        }

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.i(TAG, query + " has been submitted");
                InputMethodManager inputMethodManager =
                        (InputMethodManager) getContext()
                                .getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(
                        getActivity().getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
                QueryPreferences.setStoredQuery(getActivity(),query);
                updateUI();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.i(TAG, newText + " is being altered");
                return false;
            }
        });

        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String query = QueryPreferences.getStoredQuery(getActivity());
                searchView.setQuery(query,false);
            }
        });
    }

    void updateUI(){
        String query = QueryPreferences.getStoredQuery(getActivity());
        new FetchItemsTask(query).execute();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.menu_item_clear:
                QueryPreferences.setStoredQuery(getActivity(),null);
                updateUI();
                return true;
            case R.id.menu_item_toggle_polling:
                boolean shouldStartAlarm = !PollService.isServiceAlarmOn(getActivity());
                PollService.setServiceAlarm(getActivity(),shouldStartAlarm);
                getActivity().invalidateOptionsMenu();
                return true;
            default:
                return true;

        }
    }
}
