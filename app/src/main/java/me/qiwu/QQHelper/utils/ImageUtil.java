package me.qiwu.QQHelper.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

/**
 * Created by Deng on 2018/7/9.
 */

public class ImageUtil {
    public static Bitmap base64ToBitmap(String base64)throws OutOfMemoryError{
        byte[]decode= Base64.decode(base64,0);
        return BitmapFactory.decodeByteArray(decode,0,decode.length);
    }
}
