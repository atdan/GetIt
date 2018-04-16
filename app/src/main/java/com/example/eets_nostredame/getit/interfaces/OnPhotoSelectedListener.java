package com.example.eets_nostredame.getit.interfaces;

import android.graphics.Bitmap;
import android.net.Uri;

/**
 * Created by Eets_Nostredame on 23/03/2018.
 */

public interface OnPhotoSelectedListener {
    void getImagePath(Uri imagePath);
    void getImageBitmap(Bitmap bitmap);
}
