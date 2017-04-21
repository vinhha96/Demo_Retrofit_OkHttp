package vn.com.vng.zalopay.tracker;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import vn.com.zalopay.analytics.ZPEvents;
import vn.com.zalopay.analytics.ZPTracker;

/**
 * Created by huuhoa on 6/27/16.
 * Default implementation for translate ZPTracker to Google Analytics
 */
public class ZPTrackerGA implements ZPTracker {

    private static final String FORMAT_GOOGLE_ANALYTICS = "[Android][%s]";

    private final Tracker mTracker;

    public ZPTrackerGA(Tracker tracker) {
        mTracker = tracker;
    }

    @Override
    public void trackEvent(int eventId, Long eventValue) {

        HitBuilders.EventBuilder eventBuilder = new HitBuilders.EventBuilder()
                .setCategory(ZPEvents.categoryFromEventId(eventId))
                .setAction(ZPEvents.actionFromEventId(eventId))
                .setLabel(ZPEvents.actionFromEventId(eventId));

//        if (eventValue != null) {
//            eventBuilder.setValue(eventValue);
//        }

        mTracker.send(eventBuilder.build());
    }

    @Override
    public void trackScreen(String screenName) {
        String screenWithFormat = String.format(FORMAT_GOOGLE_ANALYTICS, screenName);
        mTracker.setScreenName(screenWithFormat);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    /**
     * Log timing value for given event.
     * Events are defined in https://docs.google.com/spreadsheets/d/1kdqC78-qMsRGY_n4hMyzUsbHlS3Gl4yWwgr62qLO8Co/edit#gid=0
     *
     * @param eventId If of timing event
     * @param value   time recorded in milliseconds
     */
    @Override
    public void trackTiming(int eventId, long value) {
        mTracker.send(new HitBuilders.TimingBuilder()
            .setCategory(ZPEvents.categoryFromEventId(eventId))
            .setValue(value)
            .setLabel(ZPEvents.actionFromEventId(eventId))
            .setVariable(ZPEvents.actionFromEventId(eventId))
            .build());
    }

    @Override
    public void trackApptransidEvent(String apptransid, int appid, int step, int step_result,
                                     int pcmid, int transtype, long transid, int sdk_result,
                                     int server_result, String source) {

    }
}