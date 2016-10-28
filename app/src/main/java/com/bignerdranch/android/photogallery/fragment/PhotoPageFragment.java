package com.bignerdranch.android.photogallery.fragment;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.bignerdranch.android.photogallery.R;

/**
 * Created by sai pranesh on 06-Sep-16.
 */
public class PhotoPageFragment extends VisibleFragment{


    private static final String ARG_URI = "arg_uri";
    private WebView mWebView;
    private ProgressBar mProgressBar;

    public static Fragment newInstance(Uri uri){
        Bundle args = new Bundle();
        args.putParcelable(ARG_URI,uri);

        PhotoPageFragment photoPageFragment = new PhotoPageFragment();
        photoPageFragment.setArguments(args);
        return photoPageFragment;
    }


    @SuppressLint("SetJavaScriptEnabled")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_photo_page,container,false);
        mWebView = (WebView) view.findViewById(R.id.fragment_photo_page_web_view);
        mProgressBar =(ProgressBar) view.findViewById(R.id.fragment_photo_page_progressbar);

        mWebView.getSettings().setJavaScriptEnabled(true);

        mWebView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                if(newProgress == 100){
                    mProgressBar.setVisibility(View.GONE);
                }else{
                    mProgressBar.setVisibility(View.VISIBLE);
                    mProgressBar.setProgress(newProgress);
                }
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                AppCompatActivity appCompatActivity =(AppCompatActivity) getActivity();
                appCompatActivity.getSupportActionBar().setSubtitle(title);
            }
        });


        mWebView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return false;
            }
        });
        mWebView.loadUrl(String.valueOf(getArguments().get(ARG_URI)));
        return view;
    }


}
