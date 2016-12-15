package vn.zalopay.feedback.collectors;

import vn.zalopay.feedback.CollectorSetting;
import vn.zalopay.feedback.IFeedbackCollector;

/**
 * Created by huuhoa on 12/15/16.
 * Collect app information
 */

public class AppCollector implements IFeedbackCollector {
    private static CollectorSetting sSetting;
    static {
        sSetting = new CollectorSetting();
        sSetting.userVisibility = true;
        sSetting.displayName = "Application Information";
        sSetting.dataKeyName = "appinfo";
    }

    /**
     * Get pre-config settings for data collector
     */
    @Override
    public CollectorSetting getSetting() {
        return sSetting;
    }
}
