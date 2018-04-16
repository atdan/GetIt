package com.example.eets_nostredame.getit.BackgroundTask;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.widget.Toast;

import com.example.eets_nostredame.getit.PostFragment;
import com.example.eets_nostredame.getit.SearchActivity;

/**
 * Created by Eets_Nostredame on 26/03/2018.
 */

public class BackgroundImageResize extends AsyncTask<Uri,Integer,byte[]> {
    Bitmap bitmap;
    public BackgroundImageResize(Bitmap bitmap){
        if (bitmap != null){
            this.bitmap = bitmap;
        }
    }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
//        Toast.makeText(SearchActivity,"Compressing image", Toast.LENGTH_SHORT).show();
    }
    @Override
    protected byte[] doInBackground(Uri... uris) {
        return new byte[0];
    }



    @Override
    protected void onPostExecute(byte[] bytes) {
        super.onPostExecute(bytes);
    }
}
