package vn.zalopay.feedback.collectors;

import android.os.Build;

import org.json.JSONException;
import org.json.JSONObject;

import vn.zalopay.feedback.CollectorSetting;
import vn.zalopay.feedback.IFeedbackCollector;

/**
 * Created by huuhoa on 12/15/16.
 * Collect device information
 */

public class DeviceCollector implements IFeedbackCollector {
    private static CollectorSetting sSetting;
    static {
        sSetting = new CollectorSetting();
        sSetting.userVisibility = true;
        sSetting.displayName = "Device Information";
        sSetting.dataKeyName = "deviceinfo";
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
            JSONObject retVal = new JSONObject();
            retVal.put("product", Build.PRODUCT);
            retVal.put("model", Build.MODEL);
            retVal.put("serial", Build.SERIAL);
            retVal.put("version", Build.VERSION.RELEASE);
            retVal.put("sdk_version", Build.VERSION.SDK_INT);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                retVal.put("abilist", Build.SUPPORTED_ABIS);
            }

            return retVal;
        } catch (JSONException e) {
            return null;
        }
    }
}
