package vn.com.vng.zalopay.data.cache.model;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

import vn.com.vng.zalopay.data.cache.model.BundleGD;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "BUNDLE_GD".
*/
public class BundleGDDao extends AbstractDao<BundleGD, Long> {

    public static final String TABLENAME = "BUNDLE_GD";

    /**
     * Properties of entity BundleGD.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, long.class, "id", true, "_id");
        public final static Property CreateTime = new Property(1, Long.class, "createTime", false, "CREATE_TIME");
        public final static Property LastTimeGetPackage = new Property(2, Long.class, "lastTimeGetPackage", false, "LAST_TIME_GET_PACKAGE");
    };


    public BundleGDDao(DaoConfig config) {
        super(config);
    }
    
    public BundleGDDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"BUNDLE_GD\" (" + //
                "\"_id\" INTEGER PRIMARY KEY NOT NULL UNIQUE ," + // 0: id
                "\"CREATE_TIME\" INTEGER," + // 1: createTime
                "\"LAST_TIME_GET_PACKAGE\" INTEGER);"); // 2: lastTimeGetPackage
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"BUNDLE_GD\"";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, BundleGD entity) {
        stmt.clearBindings();
        stmt.bindLong(1, entity.getId());
 
        Long createTime = entity.getCreateTime();
        if (createTime != null) {
            stmt.bindLong(2, createTime);
        }
 
        Long lastTimeGetPackage = entity.getLastTimeGetPackage();
        if (lastTimeGetPackage != null) {
            stmt.bindLong(3, lastTimeGetPackage);
        }
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.getLong(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public BundleGD readEntity(Cursor cursor, int offset) {
        BundleGD entity = new BundleGD( //
            cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getLong(offset + 1), // createTime
            cursor.isNull(offset + 2) ? null : cursor.getLong(offset + 2) // lastTimeGetPackage
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, BundleGD entity, int offset) {
        entity.setId(cursor.getLong(offset + 0));
        entity.setCreateTime(cursor.isNull(offset + 1) ? null : cursor.getLong(offset + 1));
        entity.setLastTimeGetPackage(cursor.isNull(offset + 2) ? null : cursor.getLong(offset + 2));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(BundleGD entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(BundleGD entity) {
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
