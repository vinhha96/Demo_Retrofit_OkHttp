package vn.com.vng.zalopay.data.appresources;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;
import vn.com.vng.zalopay.data.api.entity.AppResourceEntity;
import vn.com.vng.zalopay.data.cache.SqlBaseScopeImpl;
import vn.com.vng.zalopay.data.cache.mapper.PlatformDaoMapper;
import vn.com.vng.zalopay.data.cache.model.AppResourceGD;
import vn.com.vng.zalopay.data.cache.model.AppResourceGDDao;
import vn.com.vng.zalopay.data.cache.model.DaoSession;
import vn.com.vng.zalopay.data.util.Lists;

/**
 * Created by huuhoa on 6/17/16.
 * Implementation for AppResource.LocalStorage
 */
public class AppResourceLocalStorage extends SqlBaseScopeImpl implements AppResource.LocalStorage {
    private PlatformDaoMapper platformDaoMapper;

    public AppResourceLocalStorage(DaoSession daoSession, PlatformDaoMapper mapper) {
        super(daoSession);
        this.platformDaoMapper = mapper;
    }

    @Override
    public List<AppResourceEntity> get() {
        try {
            return platformDaoMapper.transformAppResourceDao(getAppInfoDao().queryBuilder().list());
        } catch (android.database.sqlite.SQLiteException e) {
            Timber.e(e, "Exception");
            return new ArrayList<>();
        }
    }

    @Override
    public void put(List<AppResourceEntity> resourceEntities) {
        List<AppResourceGD> list = platformDaoMapper.transformAppResourceEntity(resourceEntities);
        if (Lists.isEmptyOrNull(list)) {
            return;
        }

        for (AppResourceGD appResource : list) {
            appResource.setStateDownload(0);
            appResource.setNumRetry(0);
            appResource.setTimeDownload(0L);
        }

        getAppInfoDao().insertOrReplaceInTx(list);
    }

    @Override
    public void updateAppList(List<Integer> listAppId) {
        getAppInfoDao().queryBuilder()
                .where(AppResourceGDDao.Properties.Appid.notIn(listAppId))
                .buildDelete()
                .executeDeleteWithoutDetachingEntities(); // TOdo: chú ý có khi trong session/cache van ton tai. (get App !=null)
    }

    @Override
    public void increaseStateDownload(int appId) {
        Timber.d("increaseStateDownload appId %s", appId);
        List<AppResourceGD> appResourceGD = getAppInfoDao().queryBuilder().where(AppResourceGDDao.Properties.Appid.eq(appId)).list();
        if (Lists.isEmptyOrNull(appResourceGD)) {
            return;
        }

        for (AppResourceGD app : appResourceGD) {

            int state = app.getStateDownload() + 1;
            app.setStateDownload(state);
            if (state >= 2) {
                app.setNumRetry(0);
                app.setTimeDownload(0L);
            }
        }

        getAppInfoDao().insertOrReplaceInTx(appResourceGD);
    }

    @Override
    public void increaseRetryDownload(long appId) {
        List<AppResourceGD> appResourceGD = getAppInfoDao().queryBuilder().where(AppResourceGDDao.Properties.Appid.eq(appId)).list();
        if (Lists.isEmptyOrNull(appResourceGD)) return;

        long currentTime = System.currentTimeMillis() / 1000;
        for (AppResourceGD app : appResourceGD) {
            app.setNumRetry(app.getNumRetry() + 1);
            app.setTimeDownload(currentTime);
        }

        getAppInfoDao().insertOrReplaceInTx(appResourceGD);
    }
}
