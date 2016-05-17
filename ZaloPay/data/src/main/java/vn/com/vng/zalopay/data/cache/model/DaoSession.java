package vn.com.vng.zalopay.data.cache.model;

import android.database.sqlite.SQLiteDatabase;

import java.util.Map;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.AbstractDaoSession;
import de.greenrobot.dao.identityscope.IdentityScopeType;
import de.greenrobot.dao.internal.DaoConfig;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see de.greenrobot.dao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig appInfoDaoConfig;
    private final DaoConfig transactionLogDaoConfig;
    private final DaoConfig dataManifestDaoConfig;

    private final AppInfoDao appInfoDao;
    private final TransactionLogDao transactionLogDao;
    private final DataManifestDao dataManifestDao;

    public DaoSession(SQLiteDatabase db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        appInfoDaoConfig = daoConfigMap.get(AppInfoDao.class).clone();
        appInfoDaoConfig.initIdentityScope(type);

        transactionLogDaoConfig = daoConfigMap.get(TransactionLogDao.class).clone();
        transactionLogDaoConfig.initIdentityScope(type);

        dataManifestDaoConfig = daoConfigMap.get(DataManifestDao.class).clone();
        dataManifestDaoConfig.initIdentityScope(type);

        appInfoDao = new AppInfoDao(appInfoDaoConfig, this);
        transactionLogDao = new TransactionLogDao(transactionLogDaoConfig, this);
        dataManifestDao = new DataManifestDao(dataManifestDaoConfig, this);

        registerDao(AppInfo.class, appInfoDao);
        registerDao(TransactionLog.class, transactionLogDao);
        registerDao(DataManifest.class, dataManifestDao);
    }

    //clear cache
    public void clear() {
        appInfoDaoConfig.getIdentityScope().clear();
        transactionLogDaoConfig.getIdentityScope().clear();
        dataManifestDaoConfig.getIdentityScope().clear();
    }

    public AppInfoDao getAppInfoDao() {
        return appInfoDao;
    }

    public TransactionLogDao getTransactionLogDao() {
        return transactionLogDao;
    }

    public DataManifestDao getDataManifestDao() {
        return dataManifestDao;
    }

}
