package vn.com.vng.zalopay.data.cache.model;

import android.database.sqlite.SQLiteDatabase;

import java.util.Map;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.AbstractDaoSession;
import de.greenrobot.dao.identityscope.IdentityScopeType;
import de.greenrobot.dao.internal.DaoConfig;

import vn.com.vng.zalopay.data.cache.model.AppResourceGD;
import vn.com.vng.zalopay.data.cache.model.PaymentTransTypeGD;
import vn.com.vng.zalopay.data.cache.model.TransactionLog;
import vn.com.vng.zalopay.data.cache.model.DataManifest;
import vn.com.vng.zalopay.data.cache.model.BankCardGD;
import vn.com.vng.zalopay.data.cache.model.ZaloFriend;
import vn.com.vng.zalopay.data.cache.model.TransferRecent;
import vn.com.vng.zalopay.data.cache.model.NotificationGD;

import vn.com.vng.zalopay.data.cache.model.AppResourceGDDao;
import vn.com.vng.zalopay.data.cache.model.PaymentTransTypeGDDao;
import vn.com.vng.zalopay.data.cache.model.TransactionLogDao;
import vn.com.vng.zalopay.data.cache.model.DataManifestDao;
import vn.com.vng.zalopay.data.cache.model.BankCardGDDao;
import vn.com.vng.zalopay.data.cache.model.ZaloFriendDao;
import vn.com.vng.zalopay.data.cache.model.TransferRecentDao;
import vn.com.vng.zalopay.data.cache.model.NotificationGDDao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see de.greenrobot.dao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig appResourceGDDaoConfig;
    private final DaoConfig paymentTransTypeGDDaoConfig;
    private final DaoConfig transactionLogDaoConfig;
    private final DaoConfig dataManifestDaoConfig;
    private final DaoConfig bankCardGDDaoConfig;
    private final DaoConfig zaloFriendDaoConfig;
    private final DaoConfig transferRecentDaoConfig;
    private final DaoConfig notificationGDDaoConfig;

    private final AppResourceGDDao appResourceGDDao;
    private final PaymentTransTypeGDDao paymentTransTypeGDDao;
    private final TransactionLogDao transactionLogDao;
    private final DataManifestDao dataManifestDao;
    private final BankCardGDDao bankCardGDDao;
    private final ZaloFriendDao zaloFriendDao;
    private final TransferRecentDao transferRecentDao;
    private final NotificationGDDao notificationGDDao;

    public DaoSession(SQLiteDatabase db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        appResourceGDDaoConfig = daoConfigMap.get(AppResourceGDDao.class).clone();
        appResourceGDDaoConfig.initIdentityScope(type);

        paymentTransTypeGDDaoConfig = daoConfigMap.get(PaymentTransTypeGDDao.class).clone();
        paymentTransTypeGDDaoConfig.initIdentityScope(type);

        transactionLogDaoConfig = daoConfigMap.get(TransactionLogDao.class).clone();
        transactionLogDaoConfig.initIdentityScope(type);

        dataManifestDaoConfig = daoConfigMap.get(DataManifestDao.class).clone();
        dataManifestDaoConfig.initIdentityScope(type);

        bankCardGDDaoConfig = daoConfigMap.get(BankCardGDDao.class).clone();
        bankCardGDDaoConfig.initIdentityScope(type);

        zaloFriendDaoConfig = daoConfigMap.get(ZaloFriendDao.class).clone();
        zaloFriendDaoConfig.initIdentityScope(type);

        transferRecentDaoConfig = daoConfigMap.get(TransferRecentDao.class).clone();
        transferRecentDaoConfig.initIdentityScope(type);

        notificationGDDaoConfig = daoConfigMap.get(NotificationGDDao.class).clone();
        notificationGDDaoConfig.initIdentityScope(type);

        appResourceGDDao = new AppResourceGDDao(appResourceGDDaoConfig, this);
        paymentTransTypeGDDao = new PaymentTransTypeGDDao(paymentTransTypeGDDaoConfig, this);
        transactionLogDao = new TransactionLogDao(transactionLogDaoConfig, this);
        dataManifestDao = new DataManifestDao(dataManifestDaoConfig, this);
        bankCardGDDao = new BankCardGDDao(bankCardGDDaoConfig, this);
        zaloFriendDao = new ZaloFriendDao(zaloFriendDaoConfig, this);
        transferRecentDao = new TransferRecentDao(transferRecentDaoConfig, this);
        notificationGDDao = new NotificationGDDao(notificationGDDaoConfig, this);

        registerDao(AppResourceGD.class, appResourceGDDao);
        registerDao(PaymentTransTypeGD.class, paymentTransTypeGDDao);
        registerDao(TransactionLog.class, transactionLogDao);
        registerDao(DataManifest.class, dataManifestDao);
        registerDao(BankCardGD.class, bankCardGDDao);
        registerDao(ZaloFriend.class, zaloFriendDao);
        registerDao(TransferRecent.class, transferRecentDao);
        registerDao(NotificationGD.class, notificationGDDao);
    }
    
    public void clear() {
        appResourceGDDaoConfig.getIdentityScope().clear();
        paymentTransTypeGDDaoConfig.getIdentityScope().clear();
        transactionLogDaoConfig.getIdentityScope().clear();
        dataManifestDaoConfig.getIdentityScope().clear();
        bankCardGDDaoConfig.getIdentityScope().clear();
        zaloFriendDaoConfig.getIdentityScope().clear();
        transferRecentDaoConfig.getIdentityScope().clear();
        notificationGDDaoConfig.getIdentityScope().clear();
    }

    public AppResourceGDDao getAppResourceGDDao() {
        return appResourceGDDao;
    }

    public PaymentTransTypeGDDao getPaymentTransTypeGDDao() {
        return paymentTransTypeGDDao;
    }

    public TransactionLogDao getTransactionLogDao() {
        return transactionLogDao;
    }

    public DataManifestDao getDataManifestDao() {
        return dataManifestDao;
    }

    public BankCardGDDao getBankCardGDDao() {
        return bankCardGDDao;
    }

    public ZaloFriendDao getZaloFriendDao() {
        return zaloFriendDao;
    }

    public TransferRecentDao getTransferRecentDao() {
        return transferRecentDao;
    }

    public NotificationGDDao getNotificationGDDao() {
        return notificationGDDao;
    }

}
