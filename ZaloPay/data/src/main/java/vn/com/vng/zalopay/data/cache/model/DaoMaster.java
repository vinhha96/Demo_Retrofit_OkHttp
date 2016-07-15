package vn.com.vng.zalopay.data.cache.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import de.greenrobot.dao.AbstractDaoMaster;
import de.greenrobot.dao.identityscope.IdentityScopeType;

import vn.com.vng.zalopay.data.cache.model.AppResourceGDDao;
import vn.com.vng.zalopay.data.cache.model.PaymentTransTypeGDDao;
import vn.com.vng.zalopay.data.cache.model.TransactionLogDao;
import vn.com.vng.zalopay.data.cache.model.DataManifestDao;
import vn.com.vng.zalopay.data.cache.model.BankCardGDDao;
import vn.com.vng.zalopay.data.cache.model.ZaloFriendGDDao;
import vn.com.vng.zalopay.data.cache.model.TransferRecentDao;
import vn.com.vng.zalopay.data.cache.model.NotificationGDDao;
import vn.com.vng.zalopay.data.cache.model.SentPackageGDDao;
import vn.com.vng.zalopay.data.cache.model.SentBundleGDDao;
import vn.com.vng.zalopay.data.cache.model.ReceivePackageGDDao;
import vn.com.vng.zalopay.data.cache.model.ReceiveBundleGDDao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * Master of DAO (schema version 20): knows all DAOs.
*/
public class DaoMaster extends AbstractDaoMaster {
    public static final int SCHEMA_VERSION = 20;

    /** Creates underlying database table using DAOs. */
    public static void createAllTables(SQLiteDatabase db, boolean ifNotExists) {
        AppResourceGDDao.createTable(db, ifNotExists);
        PaymentTransTypeGDDao.createTable(db, ifNotExists);
        TransactionLogDao.createTable(db, ifNotExists);
        DataManifestDao.createTable(db, ifNotExists);
        BankCardGDDao.createTable(db, ifNotExists);
        ZaloFriendGDDao.createTable(db, ifNotExists);
        TransferRecentDao.createTable(db, ifNotExists);
        NotificationGDDao.createTable(db, ifNotExists);
        SentPackageGDDao.createTable(db, ifNotExists);
        SentBundleGDDao.createTable(db, ifNotExists);
        ReceivePackageGDDao.createTable(db, ifNotExists);
        ReceiveBundleGDDao.createTable(db, ifNotExists);
    }
    
    /** Drops underlying database table using DAOs. */
    public static void dropAllTables(SQLiteDatabase db, boolean ifExists) {
        AppResourceGDDao.dropTable(db, ifExists);
        PaymentTransTypeGDDao.dropTable(db, ifExists);
        TransactionLogDao.dropTable(db, ifExists);
        DataManifestDao.dropTable(db, ifExists);
        BankCardGDDao.dropTable(db, ifExists);
        ZaloFriendGDDao.dropTable(db, ifExists);
        TransferRecentDao.dropTable(db, ifExists);
        NotificationGDDao.dropTable(db, ifExists);
        SentPackageGDDao.dropTable(db, ifExists);
        SentBundleGDDao.dropTable(db, ifExists);
        ReceivePackageGDDao.dropTable(db, ifExists);
        ReceiveBundleGDDao.dropTable(db, ifExists);
    }
    
    public static abstract class OpenHelper extends SQLiteOpenHelper {

        public OpenHelper(Context context, String name, CursorFactory factory) {
            super(context, name, factory, SCHEMA_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.i("greenDAO", "Creating tables for schema version " + SCHEMA_VERSION);
            createAllTables(db, false);
        }
    }
    
    /** WARNING: Drops all table on Upgrade! Use only during development. */
    public static class DevOpenHelper extends OpenHelper {
        public DevOpenHelper(Context context, String name, CursorFactory factory) {
            super(context, name, factory);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.i("greenDAO", "Upgrading schema from version " + oldVersion + " to " + newVersion + " by dropping all tables");
            dropAllTables(db, true);
            onCreate(db);
        }
    }

    public DaoMaster(SQLiteDatabase db) {
        super(db, SCHEMA_VERSION);
        registerDaoClass(AppResourceGDDao.class);
        registerDaoClass(PaymentTransTypeGDDao.class);
        registerDaoClass(TransactionLogDao.class);
        registerDaoClass(DataManifestDao.class);
        registerDaoClass(BankCardGDDao.class);
        registerDaoClass(ZaloFriendGDDao.class);
        registerDaoClass(TransferRecentDao.class);
        registerDaoClass(NotificationGDDao.class);
        registerDaoClass(SentPackageGDDao.class);
        registerDaoClass(SentBundleGDDao.class);
        registerDaoClass(ReceivePackageGDDao.class);
        registerDaoClass(ReceiveBundleGDDao.class);
    }
    
    public DaoSession newSession() {
        return new DaoSession(db, IdentityScopeType.Session, daoConfigMap);
    }
    
    public DaoSession newSession(IdentityScopeType type) {
        return new DaoSession(db, type, daoConfigMap);
    }
    
}
