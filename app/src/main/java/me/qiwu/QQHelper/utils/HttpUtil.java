package me.qiwu.QQHelper.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import de.robv.android.xposed.XposedBridge;

/**
 * Created by Deng on 2018/7/25.
 */

public class HttpUtil {
    public static String InputStreamToString(InputStream is) {
        byte[] result;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len;
            while ((len = is.read(buffer)) != -1) {
                baos.write(buffer, 0, len);
            }
            is.close();
            baos.close();
            result = baos.toByteArray();

        } catch (Exception e) {
            XposedBridge.log(e);
            return "";
        }

        return new String(result);
    }
}
