package vn.com.vng.zalopay.mdl.impl;

import android.app.Application;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.os.Environment;
import android.text.TextUtils;

import com.facebook.react.ReactInstanceManager;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import timber.log.Timber;
import vn.com.vng.zalopay.data.download.FileUtil;
import vn.com.vng.zalopay.domain.model.AppResource;
import vn.com.vng.zalopay.domain.repository.LocalResourceRepository;
import vn.com.vng.zalopay.mdl.BundleService;
import vn.com.vng.zalopay.mdl.MiniApplicationException;
import vn.com.vng.zalopay.mdl.internal.FileUtils;
import vn.com.vng.zalopay.mdl.model.ReactBundleAssetData;

/**
 * Created by huuhoa on 4/25/16.
 * Manage mini application bundles.
 * There are two types of bundle to manage:
 * + Internal mini application bundle: some core app's functionality are implemented using react-native
 * + External mini application bundle: external payment scenarios implemented using react-native
 * `BundleService` provide following functions:
 * + Check for update of internalBundle, some externalBundles (externalBundles that are parts of app)
 * + Download bundle updates
 */
public class BundleServiceImpl implements BundleService {
    Application mApplication;
    public String mCurrentInternalBundleFolder;
    private final LocalResourceRepository mLocalResourceRepository;
    private final String mBundleRootFolder;
    private Gson mGson;

    public BundleServiceImpl(Application application, LocalResourceRepository localResourceRepository, Gson gson) {
        mApplication = application;
        this.mLocalResourceRepository = localResourceRepository;
        this.mGson = gson;
        String packageName = mApplication.getPackageName();
        mBundleRootFolder = application.getFilesDir().getAbsolutePath() + File.separator + packageName + File.separator + "bundles";
    }

    @Override
    public String getInternalBundleFolder() {
        return mCurrentInternalBundleFolder;
    }

    @Override
    public String getExternalBundleFolder(int appId) {
        return String.format(Locale.getDefault(), "%s/modules/%d/app", mBundleRootFolder, appId);
    }

    @Override
    public void ensureLocalResources() {
        try {
            PackageInfo packageInfo = mApplication.getPackageManager().getPackageInfo(mApplication.getPackageName(), 0);
            Timber.i("Version name: %s", packageInfo.versionName);

            ensureInternalLocalResources(packageInfo);
            ensurePaymentAppLocalResources(packageInfo);
        } catch (PackageManager.NameNotFoundException e) {
            Timber.e(e, "Error!!!");
        }
    }

    private String getInternalBundleRoot() {
        return mBundleRootFolder + File.separator + "modules/zalopay";
    }

    private void ensureInternalLocalResources(PackageInfo packageInfo) {
        String currentInternalVersion = mLocalResourceRepository.getInternalResourceVersion();
        if (currentInternalVersion == null) {
            currentInternalVersion = "";
        }
        Timber.i("Internal version: %s", currentInternalVersion);
        if (!currentInternalVersion.equalsIgnoreCase(packageInfo.versionName)) {
            Timber.i("Need to update internal resource");
            if (updateInternalResource()) {
                mLocalResourceRepository.setInternalResourceVersion(packageInfo.versionName);
            }
        } else {
            Timber.i("Internal resource is updated");
        }

        mCurrentInternalBundleFolder = getInternalBundleRoot();
    }

    private void ensurePaymentAppLocalResources(PackageInfo packageInfo) {

        String bundle;

        try {
            AssetManager assetManager = mApplication.getAssets();
            bundle = FileUtils.loadStringFromStream(assetManager.open("bundle.json"));
        } catch (IOException ex) {
            Timber.e(ex, "IOException loadStringFromStream");
            return;
        }

        ReactBundleAssetData reactBundleAssetData = mGson.fromJson(bundle, ReactBundleAssetData.class);

        for (ReactBundleAssetData.ExternalBundle ebundle : reactBundleAssetData.external_bundle) {
            String appVersion = mLocalResourceRepository.getExternalResourceVersion(ebundle.appid);
            if (appVersion != null && appVersion.equalsIgnoreCase(packageInfo.versionName)) {
                continue;
            }

            Timber.i("Application %s need to be updated", ebundle.appname);
            if (!updatePaymentAppLocalResource(ebundle)) {
                continue;
            }

            mLocalResourceRepository.setExternalResourceVersion(ebundle.appid, packageInfo.versionName);
        }

        Timber.i("Update PaymentApp done");
    }

    private boolean updatePaymentAppLocalResource(ReactBundleAssetData.ExternalBundle bundle) {
        String destination = getExternalBundleFolder(bundle.appid);

        Timber.d("destination %s %s ", destination, bundle.appname);

        return unzipAssetToFolder(bundle.asset, destination);
    }

    /**
     * Extract zalopay_internal.zip from apk's assets and unzip to destination folder
     *
     * @return true if succeeded
     */
    private boolean updateInternalResource() {
        Timber.d("updateInternalResource");
        String internalRoot = getInternalBundleRoot();

        return unzipAssetToFolder("zalopay_internal.zip", internalRoot);
    }

    private boolean unzipAssetToFolder(String assetName, String dstPath) {
        try {
            InputStream stream = mApplication.getAssets().open(assetName);
            FileUtils.unzipFile(stream, dstPath, true);
            return true;
        } catch (Exception e) {
            Timber.e(e, "exception %s", e);
            return false;
        }
    }
}
