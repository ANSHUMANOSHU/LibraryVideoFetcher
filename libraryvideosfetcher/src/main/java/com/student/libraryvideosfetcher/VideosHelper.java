package com.student.libraryvideosfetcher;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.Log;

import java.util.ArrayList;

public class VideosHelper {

    private Context context;
    private OnFetchedListener onFetchedListener;
    private Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
    private ArrayList<VideoItem> videos;

    public VideosHelper(Context context) {
        this.context = context;
    }

    public void fetchAllVideos(OnFetchedListener onFetchedListener){
        this.onFetchedListener=onFetchedListener;
        AsyncVideosFetcher asyncVideosFetcher = new AsyncVideosFetcher();
        asyncVideosFetcher.execute();
    }

    private class AsyncVideosFetcher extends AsyncTask<Void, Void, Void> {

        @SuppressLint("InlinedApi")
        @Override
        protected Void doInBackground(Void... voids) {
            videos = new ArrayList<>();
            String[] proj = {MediaStore.Video.VideoColumns.DATA, MediaStore.Video.Media.DISPLAY_NAME
                    , MediaStore.Video.Media.DURATION, MediaStore.Video.Media.MINI_THUMB_MAGIC};

            try (Cursor cursor = context.getContentResolver().query(uri, proj, null, null, null)) {
                if (cursor != null) {
                    while (cursor.moveToNext()) {
                        VideoItem video = new VideoItem();
                        video.setPath(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)));
                        video.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME)));
                        video.setDuration(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION)));
                        videos.add(video);
                    }
                }
            } catch (Exception ignored) {
                
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            onFetchedListener.onFetched(videos);
        }
    }
}
