package vn.com.vng.zalopay.data.cache.model;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

import vn.com.vng.zalopay.data.cache.model.ReceivePackageGD;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "RECEIVE_PACKAGE_GD".
*/
public class ReceivePackageGDDao extends AbstractDao<ReceivePackageGD, Long> {

    public static final String TABLENAME = "RECEIVE_PACKAGE_GD";

    /**
     * Properties of entity ReceivePackageGD.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, long.class, "id", true, "_id");
        public final static Property BundleID = new Property(1, Long.class, "bundleID", false, "BUNDLE_ID");
        public final static Property RevZaloPayID = new Property(2, String.class, "revZaloPayID", false, "REV_ZALO_PAY_ID");
        public final static Property SendZaloPayID = new Property(3, String.class, "sendZaloPayID", false, "SEND_ZALO_PAY_ID");
        public final static Property SendFullName = new Property(4, String.class, "sendFullName", false, "SEND_FULL_NAME");
        public final static Property Amount = new Property(5, Long.class, "amount", false, "AMOUNT");
        public final static Property OpenedTime = new Property(6, Long.class, "openedTime", false, "OPENED_TIME");
        public final static Property IsOpen = new Property(7, Boolean.class, "isOpen", false, "IS_OPEN");
    };

    private DaoSession daoSession;


    public ReceivePackageGDDao(DaoConfig config) {
        super(config);
    }
    
    public ReceivePackageGDDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
        this.daoSession = daoSession;
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"RECEIVE_PACKAGE_GD\" (" + //
                "\"_id\" INTEGER PRIMARY KEY NOT NULL UNIQUE ," + // 0: id
                "\"BUNDLE_ID\" INTEGER," + // 1: bundleID
                "\"REV_ZALO_PAY_ID\" TEXT," + // 2: revZaloPayID
                "\"SEND_ZALO_PAY_ID\" TEXT," + // 3: sendZaloPayID
                "\"SEND_FULL_NAME\" TEXT," + // 4: sendFullName
                "\"AMOUNT\" INTEGER," + // 5: amount
                "\"OPENED_TIME\" INTEGER," + // 6: openedTime
                "\"IS_OPEN\" INTEGER);"); // 7: isOpen
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"RECEIVE_PACKAGE_GD\"";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, ReceivePackageGD entity) {
        stmt.clearBindings();
        stmt.bindLong(1, entity.getId());
 
        Long bundleID = entity.getBundleID();
        if (bundleID != null) {
            stmt.bindLong(2, bundleID);
        }
 
        String revZaloPayID = entity.getRevZaloPayID();
        if (revZaloPayID != null) {
            stmt.bindString(3, revZaloPayID);
        }
 
        String sendZaloPayID = entity.getSendZaloPayID();
        if (sendZaloPayID != null) {
            stmt.bindString(4, sendZaloPayID);
        }
 
        String sendFullName = entity.getSendFullName();
        if (sendFullName != null) {
            stmt.bindString(5, sendFullName);
        }
 
        Long amount = entity.getAmount();
        if (amount != null) {
            stmt.bindLong(6, amount);
        }
 
        Long openedTime = entity.getOpenedTime();
        if (openedTime != null) {
            stmt.bindLong(7, openedTime);
        }
 
        Boolean isOpen = entity.getIsOpen();
        if (isOpen != null) {
            stmt.bindLong(8, isOpen ? 1L: 0L);
        }
    }

    @Override
    protected void attachEntity(ReceivePackageGD entity) {
        super.attachEntity(entity);
        entity.__setDaoSession(daoSession);
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.getLong(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public ReceivePackageGD readEntity(Cursor cursor, int offset) {
        ReceivePackageGD entity = new ReceivePackageGD( //
            cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getLong(offset + 1), // bundleID
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // revZaloPayID
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // sendZaloPayID
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // sendFullName
            cursor.isNull(offset + 5) ? null : cursor.getLong(offset + 5), // amount
            cursor.isNull(offset + 6) ? null : cursor.getLong(offset + 6), // openedTime
            cursor.isNull(offset + 7) ? null : cursor.getShort(offset + 7) != 0 // isOpen
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, ReceivePackageGD entity, int offset) {
        entity.setId(cursor.getLong(offset + 0));
        entity.setBundleID(cursor.isNull(offset + 1) ? null : cursor.getLong(offset + 1));
        entity.setRevZaloPayID(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setSendZaloPayID(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setSendFullName(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setAmount(cursor.isNull(offset + 5) ? null : cursor.getLong(offset + 5));
        entity.setOpenedTime(cursor.isNull(offset + 6) ? null : cursor.getLong(offset + 6));
        entity.setIsOpen(cursor.isNull(offset + 7) ? null : cursor.getShort(offset + 7) != 0);
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(ReceivePackageGD entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(ReceivePackageGD entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    /** @inheritdoc */
    @Override    
    protected boolean isEntityUpdateable() {
        return true;
    }
    
}
