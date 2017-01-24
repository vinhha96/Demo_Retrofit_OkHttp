package vn.com.vng.zalopay.data.cache.model;

import java.util.Map;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.AbstractDaoSession;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.identityscope.IdentityScopeType;
import org.greenrobot.greendao.internal.DaoConfig;

import vn.com.vng.zalopay.data.cache.model.AppResourceGD;
import vn.com.vng.zalopay.data.cache.model.PaymentTransTypeGD;
import vn.com.vng.zalopay.data.cache.model.TransactionLog;
import vn.com.vng.zalopay.data.cache.model.TransactionLogBackup;
import vn.com.vng.zalopay.data.cache.model.DataManifest;
import vn.com.vng.zalopay.data.cache.model.BankCardGD;
import vn.com.vng.zalopay.data.cache.model.ZaloFriendGD;
import vn.com.vng.zalopay.data.cache.model.ZaloPayProfileGD;
import vn.com.vng.zalopay.data.cache.model.ContactGD;
import vn.com.vng.zalopay.data.cache.model.TransferRecent;
import vn.com.vng.zalopay.data.cache.model.NotificationGD;
import vn.com.vng.zalopay.data.cache.model.SentBundleSummaryDB;
import vn.com.vng.zalopay.data.cache.model.ReceivePacketSummaryDB;
import vn.com.vng.zalopay.data.cache.model.BundleGD;
import vn.com.vng.zalopay.data.cache.model.PackageInBundleGD;
import vn.com.vng.zalopay.data.cache.model.SentBundleGD;
import vn.com.vng.zalopay.data.cache.model.ReceivePackageGD;
import vn.com.vng.zalopay.data.cache.model.RedPacketAppInfoGD;
import vn.com.vng.zalopay.data.cache.model.MerchantUser;
import vn.com.vng.zalopay.data.cache.model.ApptransidLogGD;

import vn.com.vng.zalopay.data.cache.model.AppResourceGDDao;
import vn.com.vng.zalopay.data.cache.model.PaymentTransTypeGDDao;
import vn.com.vng.zalopay.data.cache.model.TransactionLogDao;
import vn.com.vng.zalopay.data.cache.model.TransactionLogBackupDao;
import vn.com.vng.zalopay.data.cache.model.DataManifestDao;
import vn.com.vng.zalopay.data.cache.model.BankCardGDDao;
import vn.com.vng.zalopay.data.cache.model.ZaloFriendGDDao;
import vn.com.vng.zalopay.data.cache.model.ZaloPayProfileGDDao;
import vn.com.vng.zalopay.data.cache.model.ContactGDDao;
import vn.com.vng.zalopay.data.cache.model.TransferRecentDao;
import vn.com.vng.zalopay.data.cache.model.NotificationGDDao;
import vn.com.vng.zalopay.data.cache.model.SentBundleSummaryDBDao;
import vn.com.vng.zalopay.data.cache.model.ReceivePacketSummaryDBDao;
import vn.com.vng.zalopay.data.cache.model.BundleGDDao;
import vn.com.vng.zalopay.data.cache.model.PackageInBundleGDDao;
import vn.com.vng.zalopay.data.cache.model.SentBundleGDDao;
import vn.com.vng.zalopay.data.cache.model.ReceivePackageGDDao;
import vn.com.vng.zalopay.data.cache.model.RedPacketAppInfoGDDao;
import vn.com.vng.zalopay.data.cache.model.MerchantUserDao;
import vn.com.vng.zalopay.data.cache.model.ApptransidLogGDDao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see org.greenrobot.greendao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig appResourceGDDaoConfig;
    private final DaoConfig paymentTransTypeGDDaoConfig;
    private final DaoConfig transactionLogDaoConfig;
    private final DaoConfig transactionLogBackupDaoConfig;
    private final DaoConfig dataManifestDaoConfig;
    private final DaoConfig bankCardGDDaoConfig;
    private final DaoConfig zaloFriendGDDaoConfig;
    private final DaoConfig zaloPayProfileGDDaoConfig;
    private final DaoConfig contactGDDaoConfig;
    private final DaoConfig transferRecentDaoConfig;
    private final DaoConfig notificationGDDaoConfig;
    private final DaoConfig sentBundleSummaryDBDaoConfig;
    private final DaoConfig receivePacketSummaryDBDaoConfig;
    private final DaoConfig bundleGDDaoConfig;
    private final DaoConfig packageInBundleGDDaoConfig;
    private final DaoConfig sentBundleGDDaoConfig;
    private final DaoConfig receivePackageGDDaoConfig;
    private final DaoConfig redPacketAppInfoGDDaoConfig;
    private final DaoConfig merchantUserDaoConfig;
    private final DaoConfig apptransidLogGDDaoConfig;

    private final AppResourceGDDao appResourceGDDao;
    private final PaymentTransTypeGDDao paymentTransTypeGDDao;
    private final TransactionLogDao transactionLogDao;
    private final TransactionLogBackupDao transactionLogBackupDao;
    private final DataManifestDao dataManifestDao;
    private final BankCardGDDao bankCardGDDao;
    private final ZaloFriendGDDao zaloFriendGDDao;
    private final ZaloPayProfileGDDao zaloPayProfileGDDao;
    private final ContactGDDao contactGDDao;
    private final TransferRecentDao transferRecentDao;
    private final NotificationGDDao notificationGDDao;
    private final SentBundleSummaryDBDao sentBundleSummaryDBDao;
    private final ReceivePacketSummaryDBDao receivePacketSummaryDBDao;
    private final BundleGDDao bundleGDDao;
    private final PackageInBundleGDDao packageInBundleGDDao;
    private final SentBundleGDDao sentBundleGDDao;
    private final ReceivePackageGDDao receivePackageGDDao;
    private final RedPacketAppInfoGDDao redPacketAppInfoGDDao;
    private final MerchantUserDao merchantUserDao;
    private final ApptransidLogGDDao apptransidLogGDDao;

    public DaoSession(Database db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        appResourceGDDaoConfig = daoConfigMap.get(AppResourceGDDao.class).clone();
        appResourceGDDaoConfig.initIdentityScope(type);

        paymentTransTypeGDDaoConfig = daoConfigMap.get(PaymentTransTypeGDDao.class).clone();
        paymentTransTypeGDDaoConfig.initIdentityScope(type);

        transactionLogDaoConfig = daoConfigMap.get(TransactionLogDao.class).clone();
        transactionLogDaoConfig.initIdentityScope(type);

        transactionLogBackupDaoConfig = daoConfigMap.get(TransactionLogBackupDao.class).clone();
        transactionLogBackupDaoConfig.initIdentityScope(type);

        dataManifestDaoConfig = daoConfigMap.get(DataManifestDao.class).clone();
        dataManifestDaoConfig.initIdentityScope(type);

        bankCardGDDaoConfig = daoConfigMap.get(BankCardGDDao.class).clone();
        bankCardGDDaoConfig.initIdentityScope(type);

        zaloFriendGDDaoConfig = daoConfigMap.get(ZaloFriendGDDao.class).clone();
        zaloFriendGDDaoConfig.initIdentityScope(type);

        zaloPayProfileGDDaoConfig = daoConfigMap.get(ZaloPayProfileGDDao.class).clone();
        zaloPayProfileGDDaoConfig.initIdentityScope(type);

        contactGDDaoConfig = daoConfigMap.get(ContactGDDao.class).clone();
        contactGDDaoConfig.initIdentityScope(type);

        transferRecentDaoConfig = daoConfigMap.get(TransferRecentDao.class).clone();
        transferRecentDaoConfig.initIdentityScope(type);

        notificationGDDaoConfig = daoConfigMap.get(NotificationGDDao.class).clone();
        notificationGDDaoConfig.initIdentityScope(type);

        sentBundleSummaryDBDaoConfig = daoConfigMap.get(SentBundleSummaryDBDao.class).clone();
        sentBundleSummaryDBDaoConfig.initIdentityScope(type);

        receivePacketSummaryDBDaoConfig = daoConfigMap.get(ReceivePacketSummaryDBDao.class).clone();
        receivePacketSummaryDBDaoConfig.initIdentityScope(type);

        bundleGDDaoConfig = daoConfigMap.get(BundleGDDao.class).clone();
        bundleGDDaoConfig.initIdentityScope(type);

        packageInBundleGDDaoConfig = daoConfigMap.get(PackageInBundleGDDao.class).clone();
        packageInBundleGDDaoConfig.initIdentityScope(type);

        sentBundleGDDaoConfig = daoConfigMap.get(SentBundleGDDao.class).clone();
        sentBundleGDDaoConfig.initIdentityScope(type);

        receivePackageGDDaoConfig = daoConfigMap.get(ReceivePackageGDDao.class).clone();
        receivePackageGDDaoConfig.initIdentityScope(type);

        redPacketAppInfoGDDaoConfig = daoConfigMap.get(RedPacketAppInfoGDDao.class).clone();
        redPacketAppInfoGDDaoConfig.initIdentityScope(type);

        merchantUserDaoConfig = daoConfigMap.get(MerchantUserDao.class).clone();
        merchantUserDaoConfig.initIdentityScope(type);

        apptransidLogGDDaoConfig = daoConfigMap.get(ApptransidLogGDDao.class).clone();
        apptransidLogGDDaoConfig.initIdentityScope(type);

        appResourceGDDao = new AppResourceGDDao(appResourceGDDaoConfig, this);
        paymentTransTypeGDDao = new PaymentTransTypeGDDao(paymentTransTypeGDDaoConfig, this);
        transactionLogDao = new TransactionLogDao(transactionLogDaoConfig, this);
        transactionLogBackupDao = new TransactionLogBackupDao(transactionLogBackupDaoConfig, this);
        dataManifestDao = new DataManifestDao(dataManifestDaoConfig, this);
        bankCardGDDao = new BankCardGDDao(bankCardGDDaoConfig, this);
        zaloFriendGDDao = new ZaloFriendGDDao(zaloFriendGDDaoConfig, this);
        zaloPayProfileGDDao = new ZaloPayProfileGDDao(zaloPayProfileGDDaoConfig, this);
        contactGDDao = new ContactGDDao(contactGDDaoConfig, this);
        transferRecentDao = new TransferRecentDao(transferRecentDaoConfig, this);
        notificationGDDao = new NotificationGDDao(notificationGDDaoConfig, this);
        sentBundleSummaryDBDao = new SentBundleSummaryDBDao(sentBundleSummaryDBDaoConfig, this);
        receivePacketSummaryDBDao = new ReceivePacketSummaryDBDao(receivePacketSummaryDBDaoConfig, this);
        bundleGDDao = new BundleGDDao(bundleGDDaoConfig, this);
        packageInBundleGDDao = new PackageInBundleGDDao(packageInBundleGDDaoConfig, this);
        sentBundleGDDao = new SentBundleGDDao(sentBundleGDDaoConfig, this);
        receivePackageGDDao = new ReceivePackageGDDao(receivePackageGDDaoConfig, this);
        redPacketAppInfoGDDao = new RedPacketAppInfoGDDao(redPacketAppInfoGDDaoConfig, this);
        merchantUserDao = new MerchantUserDao(merchantUserDaoConfig, this);
        apptransidLogGDDao = new ApptransidLogGDDao(apptransidLogGDDaoConfig, this);

        registerDao(AppResourceGD.class, appResourceGDDao);
        registerDao(PaymentTransTypeGD.class, paymentTransTypeGDDao);
        registerDao(TransactionLog.class, transactionLogDao);
        registerDao(TransactionLogBackup.class, transactionLogBackupDao);
        registerDao(DataManifest.class, dataManifestDao);
        registerDao(BankCardGD.class, bankCardGDDao);
        registerDao(ZaloFriendGD.class, zaloFriendGDDao);
        registerDao(ZaloPayProfileGD.class, zaloPayProfileGDDao);
        registerDao(ContactGD.class, contactGDDao);
        registerDao(TransferRecent.class, transferRecentDao);
        registerDao(NotificationGD.class, notificationGDDao);
        registerDao(SentBundleSummaryDB.class, sentBundleSummaryDBDao);
        registerDao(ReceivePacketSummaryDB.class, receivePacketSummaryDBDao);
        registerDao(BundleGD.class, bundleGDDao);
        registerDao(PackageInBundleGD.class, packageInBundleGDDao);
        registerDao(SentBundleGD.class, sentBundleGDDao);
        registerDao(ReceivePackageGD.class, receivePackageGDDao);
        registerDao(RedPacketAppInfoGD.class, redPacketAppInfoGDDao);
        registerDao(MerchantUser.class, merchantUserDao);
        registerDao(ApptransidLogGD.class, apptransidLogGDDao);
    }
    
    public void clear() {
        appResourceGDDaoConfig.clearIdentityScope();
        paymentTransTypeGDDaoConfig.clearIdentityScope();
        transactionLogDaoConfig.clearIdentityScope();
        transactionLogBackupDaoConfig.clearIdentityScope();
        dataManifestDaoConfig.clearIdentityScope();
        bankCardGDDaoConfig.clearIdentityScope();
        zaloFriendGDDaoConfig.clearIdentityScope();
        zaloPayProfileGDDaoConfig.clearIdentityScope();
        contactGDDaoConfig.clearIdentityScope();
        transferRecentDaoConfig.clearIdentityScope();
        notificationGDDaoConfig.clearIdentityScope();
        sentBundleSummaryDBDaoConfig.clearIdentityScope();
        receivePacketSummaryDBDaoConfig.clearIdentityScope();
        bundleGDDaoConfig.clearIdentityScope();
        packageInBundleGDDaoConfig.clearIdentityScope();
        sentBundleGDDaoConfig.clearIdentityScope();
        receivePackageGDDaoConfig.clearIdentityScope();
        redPacketAppInfoGDDaoConfig.clearIdentityScope();
        merchantUserDaoConfig.clearIdentityScope();
        apptransidLogGDDaoConfig.clearIdentityScope();
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

    public TransactionLogBackupDao getTransactionLogBackupDao() {
        return transactionLogBackupDao;
    }

    public DataManifestDao getDataManifestDao() {
        return dataManifestDao;
    }

    public BankCardGDDao getBankCardGDDao() {
        return bankCardGDDao;
    }

    public ZaloFriendGDDao getZaloFriendGDDao() {
        return zaloFriendGDDao;
    }

    public ZaloPayProfileGDDao getZaloPayProfileGDDao() {
        return zaloPayProfileGDDao;
    }

    public ContactGDDao getContactGDDao() {
        return contactGDDao;
    }

    public TransferRecentDao getTransferRecentDao() {
        return transferRecentDao;
    }

    public NotificationGDDao getNotificationGDDao() {
        return notificationGDDao;
    }

    public SentBundleSummaryDBDao getSentBundleSummaryDBDao() {
        return sentBundleSummaryDBDao;
    }

    public ReceivePacketSummaryDBDao getReceivePacketSummaryDBDao() {
        return receivePacketSummaryDBDao;
    }

    public BundleGDDao getBundleGDDao() {
        return bundleGDDao;
    }

    public PackageInBundleGDDao getPackageInBundleGDDao() {
        return packageInBundleGDDao;
    }

    public SentBundleGDDao getSentBundleGDDao() {
        return sentBundleGDDao;
    }

    public ReceivePackageGDDao getReceivePackageGDDao() {
        return receivePackageGDDao;
    }

    public RedPacketAppInfoGDDao getRedPacketAppInfoGDDao() {
        return redPacketAppInfoGDDao;
    }

    public MerchantUserDao getMerchantUserDao() {
        return merchantUserDao;
    }

    public ApptransidLogGDDao getApptransidLogGDDao() {
        return apptransidLogGDDao;
    }

}
