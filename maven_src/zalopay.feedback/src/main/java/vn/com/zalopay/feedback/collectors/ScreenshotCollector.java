package vn.com.zalopay.feedback.collectors;

import android.app.Activity;
import android.graphics.Bitmap;
import android.util.Base64;
import android.view.View;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;

import vn.com.zalopay.feedback.CollectorSetting;
import vn.com.zalopay.feedback.IFeedbackCollector;

/**
 * Created by khattn on 26/12/2016.
 */

public class ScreenshotCollector implements IFeedbackCollector {
    private Activity mActivity;
    private View mView;
    private static CollectorSetting sSetting;
    static {
        sSetting = new CollectorSetting();
        sSetting.userVisibility = true;
        sSetting.displayName = "Screenshot Information";
        sSetting.dataKeyName = "screenshot";
    }

    public ScreenshotCollector(Activity activity) {
        mActivity = activity;
        mView = mActivity.getWindow().getDecorView().getRootView();
    }

    /**
     * Get pre-config settings for data collector
     */
    @Override
    public CollectorSetting getSetting() {
        return sSetting;
    }

    /**
     * Start collecting data. If data is collected, then return JSONObject of the encoded data
     *
     * @return JSONObject value, null if data is not collected
     */
    @Override
    public JSONObject doInBackground() {
        try {
            String image = convertBitmapToString(
                    takeScreenshot()
            );

            JSONObject retVal = new JSONObject();
            retVal.put("image", image);

            return retVal;
        } catch (JSONException e) {
            return null;
        }
    }

    private Bitmap takeScreenshot() {
        mView.setDrawingCacheEnabled(true);

        return Bitmap.createBitmap(mView.getDrawingCache());
    }

    private String convertBitmapToString(Bitmap bitmap) {

        int quality = 100;
        String strImage;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        bitmap.compress(Bitmap.CompressFormat.PNG, quality, byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        strImage = Base64.encodeToString(bytes, Base64.DEFAULT);

        return strImage;
    }
}