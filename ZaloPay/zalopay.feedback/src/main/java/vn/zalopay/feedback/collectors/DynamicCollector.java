package vn.zalopay.feedback.collectors;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import vn.zalopay.feedback.CollectorSetting;
import vn.zalopay.feedback.IFeedbackCollector;

/**
 * Created by khattn on 26/12/2016.
 */

public class DynamicCollector implements IFeedbackCollector {

    private Map<String, String> mRetVal = new HashMap<>();

    private static CollectorSetting sSetting;

    static {
        sSetting = new CollectorSetting();
        sSetting.userVisibility = true;
        sSetting.displayName = "Dynamic Information";
        sSetting.dataKeyName = "dynamicinfo";
    }

    public DynamicCollector() {
    }

    public void put(@NonNull String name, @NonNull String value) {
        mRetVal.put(name, value);
    }

    @Nullable
    public String getValue(String key) {
        return mRetVal.get(key);
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
        Set<Map.Entry<String, String>> entrySet = mRetVal.entrySet();
        JSONObject ret = new JSONObject();
        for (Map.Entry<String, String> entry : entrySet) {
            try {
                ret.put(entry.getKey(), entry.getValue());
            } catch (JSONException ignore) {
            }
        }

        return ret;
    }

    @Override
    public void dispose() {
        mRetVal.clear();
    }
}
