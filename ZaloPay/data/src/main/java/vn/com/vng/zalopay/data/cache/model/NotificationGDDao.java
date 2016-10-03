package vn.com.vng.zalopay.data.cache.model;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

import vn.com.vng.zalopay.data.cache.model.NotificationGD;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "NOTIFICATION_GD".
*/
public class NotificationGDDao extends AbstractDao<NotificationGD, Long> {

    public static final String TABLENAME = "NOTIFICATION_GD";

    /**
     * Properties of entity NotificationGD.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property Transid = new Property(1, Long.class, "transid", false, "TRANSID");
        public final static Property Appid = new Property(2, Integer.class, "appid", false, "APPID");
        public final static Property Timestamp = new Property(3, Long.class, "timestamp", false, "TIMESTAMP");
        public final static Property Message = new Property(4, String.class, "message", false, "MESSAGE");
        public final static Property Userid = new Property(5, String.class, "userid", false, "USERID");
        public final static Property Destuserid = new Property(6, String.class, "destuserid", false, "DESTUSERID");
        public final static Property Notificationstate = new Property(7, Integer.class, "notificationstate", false, "NOTIFICATIONSTATE");
        public final static Property Notificationtype = new Property(8, Integer.class, "notificationtype", false, "NOTIFICATIONTYPE");
        public final static Property Mtaid = new Property(9, Long.class, "mtaid", false, "MTAID");
        public final static Property Mtuid = new Property(10, Long.class, "mtuid", false, "MTUID");
        public final static Property Embeddata = new Property(11, String.class, "embeddata", false, "EMBEDDATA");
    };


    public NotificationGDDao(DaoConfig config) {
        super(config);
    }
    
    public NotificationGDDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"NOTIFICATION_GD\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "\"TRANSID\" INTEGER," + // 1: transid
                "\"APPID\" INTEGER," + // 2: appid
                "\"TIMESTAMP\" INTEGER," + // 3: timestamp
                "\"MESSAGE\" TEXT," + // 4: message
                "\"USERID\" TEXT," + // 5: userid
                "\"DESTUSERID\" TEXT," + // 6: destuserid
                "\"NOTIFICATIONSTATE\" INTEGER," + // 7: notificationstate
                "\"NOTIFICATIONTYPE\" INTEGER," + // 8: notificationtype
                "\"MTAID\" INTEGER," + // 9: mtaid
                "\"MTUID\" INTEGER," + // 10: mtuid
                "\"EMBEDDATA\" TEXT);"); // 11: embeddata
        // Add Indexes
        db.execSQL("CREATE UNIQUE INDEX " + constraint + "IDX_NOTIFICATION_GD_MTAID_MTUID ON NOTIFICATION_GD" +
                " (\"MTAID\",\"MTUID\");");
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"NOTIFICATION_GD\"";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, NotificationGD entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        Long transid = entity.getTransid();
        if (transid != null) {
            stmt.bindLong(2, transid);
        }
 
        Integer appid = entity.getAppid();
        if (appid != null) {
            stmt.bindLong(3, appid);
        }
 
        Long timestamp = entity.getTimestamp();
        if (timestamp != null) {
            stmt.bindLong(4, timestamp);
        }
 
        String message = entity.getMessage();
        if (message != null) {
            stmt.bindString(5, message);
        }
 
        String userid = entity.getUserid();
        if (userid != null) {
            stmt.bindString(6, userid);
        }
 
        String destuserid = entity.getDestuserid();
        if (destuserid != null) {
            stmt.bindString(7, destuserid);
        }
 
        Integer notificationstate = entity.getNotificationstate();
        if (notificationstate != null) {
            stmt.bindLong(8, notificationstate);
        }
 
        Integer notificationtype = entity.getNotificationtype();
        if (notificationtype != null) {
            stmt.bindLong(9, notificationtype);
        }
 
        Long mtaid = entity.getMtaid();
        if (mtaid != null) {
            stmt.bindLong(10, mtaid);
        }
 
        Long mtuid = entity.getMtuid();
        if (mtuid != null) {
            stmt.bindLong(11, mtuid);
        }
 
        String embeddata = entity.getEmbeddata();
        if (embeddata != null) {
            stmt.bindString(12, embeddata);
        }
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public NotificationGD readEntity(Cursor cursor, int offset) {
        NotificationGD entity = new NotificationGD( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getLong(offset + 1), // transid
            cursor.isNull(offset + 2) ? null : cursor.getInt(offset + 2), // appid
            cursor.isNull(offset + 3) ? null : cursor.getLong(offset + 3), // timestamp
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // message
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // userid
            cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6), // destuserid
            cursor.isNull(offset + 7) ? null : cursor.getInt(offset + 7), // notificationstate
            cursor.isNull(offset + 8) ? null : cursor.getInt(offset + 8), // notificationtype
            cursor.isNull(offset + 9) ? null : cursor.getLong(offset + 9), // mtaid
            cursor.isNull(offset + 10) ? null : cursor.getLong(offset + 10), // mtuid
            cursor.isNull(offset + 11) ? null : cursor.getString(offset + 11) // embeddata
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, NotificationGD entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setTransid(cursor.isNull(offset + 1) ? null : cursor.getLong(offset + 1));
        entity.setAppid(cursor.isNull(offset + 2) ? null : cursor.getInt(offset + 2));
        entity.setTimestamp(cursor.isNull(offset + 3) ? null : cursor.getLong(offset + 3));
        entity.setMessage(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setUserid(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setDestuserid(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
        entity.setNotificationstate(cursor.isNull(offset + 7) ? null : cursor.getInt(offset + 7));
        entity.setNotificationtype(cursor.isNull(offset + 8) ? null : cursor.getInt(offset + 8));
        entity.setMtaid(cursor.isNull(offset + 9) ? null : cursor.getLong(offset + 9));
        entity.setMtuid(cursor.isNull(offset + 10) ? null : cursor.getLong(offset + 10));
        entity.setEmbeddata(cursor.isNull(offset + 11) ? null : cursor.getString(offset + 11));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(NotificationGD entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(NotificationGD entity) {
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
