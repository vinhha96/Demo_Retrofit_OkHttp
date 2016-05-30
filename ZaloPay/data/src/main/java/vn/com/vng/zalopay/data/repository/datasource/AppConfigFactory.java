package vn.com.vng.zalopay.data.repository.datasource;

import android.content.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.OkHttpClient;
import rx.Observable;
import timber.log.Timber;
import vn.com.vng.zalopay.data.Constants;
import vn.com.vng.zalopay.data.api.AppConfigService;
import vn.com.vng.zalopay.data.api.entity.AppResourceEntity;
import vn.com.vng.zalopay.data.api.entity.CardEntity;
import vn.com.vng.zalopay.data.api.response.AppResourceResponse;
import vn.com.vng.zalopay.data.api.response.PlatformInfoResponse;
import vn.com.vng.zalopay.data.cache.SqlitePlatformScope;
import vn.com.vng.zalopay.data.download.DownLoadInfo;
import vn.com.vng.zalopay.data.download.DownloadAppResourceTask;
import vn.com.vng.zalopay.data.download.DownloadAppResourceTaskQueue;
import vn.com.vng.zalopay.data.util.Lists;
import vn.com.vng.zalopay.domain.model.User;

/**
 * Created by AnhHieu on 4/28/16.
 */

public class AppConfigFactory {

    private Context context;

    private AppConfigService appConfigService;

    private User user;

    private SqlitePlatformScope sqlitePlatformScope;

    private String platformcode = "android";
    private String dscreentype = "xhigh";
    private String appversion = "appversion";
    private String mno = "mno";
    private String devicemodel = "devicemodel";

    private HashMap<String, String> paramsReq;
    private DownloadAppResourceTaskQueue taskQueue;

    private OkHttpClient mOkHttpClient;

    public AppConfigFactory(Context context, AppConfigService service,
                            User user, SqlitePlatformScope sqlitePlatformScope,
                            HashMap<String, String> paramsReq,
                            DownloadAppResourceTaskQueue taskQueue,
                            OkHttpClient mOkHttpClient) {

        if (context == null || service == null) {
            throw new IllegalArgumentException("Constructor parameters cannot be null!!!");
        }

        this.context = context;
        this.appConfigService = service;
        this.user = user;
        this.sqlitePlatformScope = sqlitePlatformScope;
        this.paramsReq = paramsReq;
        this.taskQueue = taskQueue;
        this.mOkHttpClient = mOkHttpClient;
    }


    public Observable<PlatformInfoResponse> getPlatformInfo() {

        String platforminfochecksum = sqlitePlatformScope.getDataManifest(Constants.MANIF_PLATFORM_INFO_CHECKSUM);
        String rsversion = sqlitePlatformScope.getDataManifest(Constants.MANIF_RESOURCE_VERSION);

        return appConfigService.platforminfo(user.uid, user.accesstoken, platformcode, dscreentype, platforminfochecksum, rsversion, appversion, mno, devicemodel)
                .doOnNext(response -> processPlatformResp(response))
                ;

    }

    private void processPlatformResp(PlatformInfoResponse response) {
        //  sqlitePlatformScope.put
        sqlitePlatformScope.insertDataManifest(Constants.MANIF_PLATFORM_INFO_CHECKSUM, response.platforminfochecksum);
        sqlitePlatformScope.insertDataManifest(Constants.MANIF_RESOURCE_VERSION, response.resource.rsversion);

        sqlitePlatformScope.writeCards(response.platforminfo.cardlist);
    }


    public Observable<List<CardEntity>> listCardCache() {
        return sqlitePlatformScope.listCard();
    }

    public Observable<AppResourceResponse> getAppResourceCloud() {

        List<Integer> appidlist = new ArrayList<>();
        List<String> checksumlist = new ArrayList<>();

        listAppIdAndChecksum(appidlist, checksumlist);

        Timber.d("appid react-native list %s", appidlist);

        return appConfigService.insideappresource(appidlist.toString(), checksumlist.toString(), paramsReq)
                .doOnNext(resourceResponse -> processAppResourceResponse(resourceResponse))
                ;
    }

    public Observable<List<AppResourceEntity>> listAppResourceCache() {
        return sqlitePlatformScope.listApp();
    }

    private void listAppIdAndChecksum(List<Integer> appidlist, List<String> checksumlist) {
        List<AppResourceEntity> listApp = sqlitePlatformScope.listAppResourceEntity();
        if (!Lists.isEmptyOrNull(listApp)) {
            for (AppResourceEntity appResourceEntity : listApp) {
                appidlist.add(appResourceEntity.appid);
                checksumlist.add(appResourceEntity.checksum);
            }
        }
    }

    private void processAppResourceResponse(AppResourceResponse resourceReponse) {
        List<Integer> listAppId = resourceReponse.appidlist;

        List<AppResourceEntity> resourcelist = resourceReponse.resourcelist;

        long expiredtime = resourceReponse.expiredtime;

//        startDownloadService(resourcelist, resourceReponse.baseurl);

        Timber.d("baseurl %s listAppId %s resourcelistSize %s", resourceReponse.baseurl, listAppId, resourcelist.size());

        sqlitePlatformScope.write(resourcelist);
        sqlitePlatformScope.updateAppId(listAppId);
    }

    private void startDownloadService(List<AppResourceEntity> resourcelist, String baseurl) {

        List<DownloadAppResourceTask> needDownloadList = new ArrayList<>();
        for (AppResourceEntity appResourceEntity : resourcelist) {
            appResourceEntity.jsurl = baseurl + appResourceEntity.jsurl;
            appResourceEntity.imageurl = baseurl + appResourceEntity.imageurl;

            if (appResourceEntity.needdownloadrs == 1) {
                createTask(appResourceEntity, needDownloadList);
            }
        }

        if (!needDownloadList.isEmpty()) {
            Timber.d("Start download %s", needDownloadList.size());
            taskQueue.enqueue(needDownloadList);
        }

    }


    private void createTask(AppResourceEntity appResourceEntity, List<DownloadAppResourceTask> listTask) {

        DownLoadInfo downLoadJS = new DownLoadInfo();
        downLoadJS.appname = appResourceEntity.appname;
        downLoadJS.checksum = appResourceEntity.checksum;
        downLoadJS.appid = appResourceEntity.appid;
        downLoadJS.url = appResourceEntity.jsurl;

        DownloadAppResourceTask taskJs = new DownloadAppResourceTask(context, downLoadJS, mOkHttpClient);
        listTask.add(taskJs);

        DownLoadInfo downloadImg = new DownLoadInfo();
        downloadImg.appname = appResourceEntity.appname;
        downloadImg.checksum = appResourceEntity.checksum;
        downloadImg.appid = appResourceEntity.appid;
        downloadImg.url = appResourceEntity.imageurl;

        DownloadAppResourceTask taskImgUrl = new DownloadAppResourceTask(context, downloadImg, mOkHttpClient);
        listTask.add(taskImgUrl);
    }
}
