package vn.com.vng.zalopay.utils;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import vn.com.vng.zalopay.R;

/**
 * Created by longlv on 10/6/15.
 */


public class ToastUtil {

    public static void showToast(Context context, int message) {
        showToast(context, message, Toast.LENGTH_SHORT);
    }

    public static void showToast(Context context, int message, int duration, int gravity) {
        showToast(context, context.getString(message, duration, gravity));
    }

    public static void showToast(Context context, int message, int duration) {
        showToast(context, message, duration, Gravity.CENTER);
    }

    public static void showToast(final Activity activity, final CharSequence message) {
        if (activity == null)
            return;
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showToast(activity.getApplicationContext(), message, Toast.LENGTH_SHORT);
            }
        });
    }

    public static void showToast(Context context, CharSequence message) {
        showToast(context, message, Toast.LENGTH_SHORT);
    }

    public static void showToast(Context context, CharSequence message, int duration) {
        showToast(context, message, duration, Gravity.CENTER);
    }

    public static void showToast(Context context, CharSequence message, int duration, int gravity) {
        if (TextUtils.isEmpty(message)) {
            return;
        }

        Toast toast = Toast.makeText(context, message, duration);
        toast.setGravity(gravity, 0, 0);
        toast.show();
    }

    public static void showToastOTPSuccess(Context context, CharSequence message) {
        Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        View view = LayoutInflater.from(context).inflate(R.layout.layout_toast_opt_success, null);
        toast.setView(view);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

}