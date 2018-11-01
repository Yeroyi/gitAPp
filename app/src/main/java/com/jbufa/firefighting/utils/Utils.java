package com.jbufa.firefighting.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.widget.EditText;

import com.jbufa.firefighting.common.AppContext;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by tiansj on 15/7/29.
 */
public class Utils {

    private static final String TAG = "Utils";


    // 获取ApiKey
    public static String getMetaValue(Context context, String metaKey) {
        Bundle metaData = null;
        String apiKey = null;
        if (context == null || metaKey == null) {
            return null;
        }
        try {
            ApplicationInfo ai = context.getPackageManager()
                    .getApplicationInfo(context.getPackageName(),
                            PackageManager.GET_META_DATA);
            if (null != ai) {
                metaData = ai.metaData;
            }
            if (null != metaData) {
                apiKey = metaData.getString(metaKey);
            }
        } catch (PackageManager.NameNotFoundException e) {

        }
        return apiKey;
    }


    /**
     * 设置手机网络类型，wifi，cmwap，ctwap，用于联网参数选择
     *
     * @return
     */
    static String getNetworkType() {
        String networkType = "wifi";
        ConnectivityManager manager = (ConnectivityManager) AppContext.getInstance()
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo netWrokInfo = manager.getActiveNetworkInfo();
        if (netWrokInfo == null || !netWrokInfo.isAvailable()) {
            return ""; // 当前网络不可用
        }

        String info = netWrokInfo.getExtraInfo();
        if ((info != null)
                && ((info.trim().toLowerCase().equals("cmwap"))
                || (info.trim().toLowerCase().equals("uniwap"))
                || (info.trim().toLowerCase().equals("3gwap")) || (info
                .trim().toLowerCase().equals("ctwap")))) {
            // 上网方式为wap
            if (info.trim().toLowerCase().equals("ctwap")) {
                // 电信
                networkType = "ctwap";
            } else {
                networkType = "cmwap";
            }
        }
        return networkType;
    }

    /**
     * 检测网络是否可用
     *
     * @param context
     * @return
     */
    public static boolean isNetWorkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }

        return false;
    }

    /**
     * 检测Sdcard是否存在
     *
     * @return
     */
    public static boolean isExitsSdcard() {
        if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
            return true;
        else
            return false;
    }

    /**
     * 检测Sdcard是否存在
     *
     * @return
     */
    public static boolean isEditTextEmpty(EditText mEditText) {
        if (mEditText != null) {
            if (mEditText.getText().toString().isEmpty()) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }


    static String getString(Context context, int resId) {
        return context.getResources().getString(resId);
    }

    public static boolean isPhoneNumber(String mobiles) {
        if (mobiles.isEmpty()) {
            return false;
        } else {
            String regExp = "^((13[0-9])|(15[^4])|(166)|(17[0-8])|(18[0-9])|(19[8-9])|(147,145))\\d{8}$";
            Pattern p = Pattern.compile(regExp);
            Matcher m = p.matcher(mobiles);
            return m.matches();
        }
    }


    /**
     * 时间戳转换成日期格式字符串
     *
     * @param seconds 精确到秒的字符串
     * @param format
     * @return
     */
    public static String timeStamp2Date(String seconds, String format) {
        if (seconds == null || seconds.isEmpty() || seconds.equals("null")) {
            return "";
        }
        if (format == null || format.isEmpty()) format = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(new Date(Long.valueOf(seconds + "000")));
    }


}
