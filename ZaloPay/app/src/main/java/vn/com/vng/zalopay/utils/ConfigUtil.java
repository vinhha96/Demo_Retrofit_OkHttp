package vn.com.vng.zalopay.utils;

import android.content.res.AssetManager;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.zalopay.ui.widget.util.FileUtil;

import java.io.IOException;
import java.util.Locale;

import timber.log.Timber;
import vn.com.vng.zalopay.BuildConfig;
import vn.com.vng.zalopay.data.appresources.ResourceHelper;
import vn.com.vng.zalopay.data.util.InsideAppUtil;
import vn.com.vng.zalopay.data.util.PhoneUtil;
import vn.com.vng.zalopay.data.zfriend.FriendConfig;
import vn.com.vng.zalopay.domain.model.Config;

/**
 * Created by longlv on 2/14/17.
 * Using to load config from assert or resource app 1.
 */

public class ConfigUtil {
    private final static String CONFIG_FILE_PATH = "config/zalopay_config.json";
    private static Config mConfig;

    static {
        mConfig = new Config();
    }

    private static String getFileConfigPath(long appId) {
        return String.format(Locale.getDefault(), "%s/%s",
                ResourceHelper.getPath(appId),
                CONFIG_FILE_PATH);
    }

    public static void initConfig(AssetManager assetManager) {
        if (loadConfigFromResource()) {
            Timber.d("Load config from resource app 1 successfully.");
            return;
        }
        if (loadConfigFromAssets(assetManager)) {
            Timber.d("Load config from assets successfully.");
        }
    }

    private static boolean loadConfigFromAssets(AssetManager assetManager) {
        try {
            String jsonConfig = FileUtil.readAssetToString(assetManager, CONFIG_FILE_PATH);
            return loadConfig(jsonConfig);
        } catch (IOException e) {
            Timber.e(e, "Read config from assets throw exception.");
            return false;
        }
    }

    public static boolean loadConfigFromResource() {
        try {
            String jsonConfig = FileUtil.readFileToString(getFileConfigPath(BuildConfig.ZALOPAY_APP_ID));
            return loadConfig(jsonConfig);
        } catch (IOException e) {
            Timber.d(e, "Read config from resource app 1 throw exception.");
            return false;
        }
    }

    private static boolean loadConfig(String jsonConfig) {
        if (TextUtils.isEmpty(jsonConfig)) {
            return false;
        }

        try {
            Gson gson = new Gson();
            Config config = gson.fromJson(jsonConfig, Config.class);
            if (config != null) {
                mConfig = config;
                loadConfigPhoneFormat(config);
                loadConfigInsideApp(config);
                FriendConfig.sEnableSyncContact = isSyncContact();
                return true;
            }
        } catch (JsonSyntaxException e) {
            Timber.e("Fail to load config with config: %s", jsonConfig);
            return false;
        }

        return false;
    }

    private static boolean loadConfigPhoneFormat(@NonNull Config config) {
        return PhoneUtil.setPhoneFormat(config.mPhoneFormat);
    }

    private static boolean loadConfigInsideApp(@NonNull Config config) {
        return InsideAppUtil.setInsideApps(config.mInsideAppList);
    }

    /**
     * Chế độ bật/tắt việc merge tên hiển thị từ danh bạ điện thoại cho danh sách bạn Zalo
     * Mặc định là TRUE.
     */

    private static boolean isSyncContact() {
        if (mConfig != null && mConfig.friendConfig != null) {
            return mConfig.friendConfig.enableMergeContactName != 0;
        }
        return true;
    }

    /**
     * Config sử dụng payment connector hoặc https
     * Default sử dụng https
     */
    public static boolean isHttpsRoute() {
        boolean isHttpsRoute = mConfig == null || !"connector".equals(mConfig.apiRoute);
        Timber.d("Network use Https route : [%s]", isHttpsRoute);
        return isHttpsRoute;
    }
}

