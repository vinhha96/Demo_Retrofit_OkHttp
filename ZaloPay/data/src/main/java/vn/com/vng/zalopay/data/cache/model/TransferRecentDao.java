package vn.com.vng.zalopay.data.cache.model;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "TRANSFER_RECENT".
*/
public class TransferRecentDao extends AbstractDao<TransferRecent, String> {

    public static final String TABLENAME = "TRANSFER_RECENT";

    /**
     * Properties of entity TransferRecent.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property ZaloPayId = new Property(0, String.class, "zaloPayId", true, "ZALO_PAY_ID");
        public final static Property ZaloPayName = new Property(1, String.class, "zaloPayName", false, "ZALO_PAY_NAME");
        public final static Property DisplayName = new Property(2, String.class, "displayName", false, "DISPLAY_NAME");
        public final static Property Avatar = new Property(3, String.class, "avatar", false, "AVATAR");
        public final static Property PhoneNumber = new Property(4, String.class, "phoneNumber", false, "PHONE_NUMBER");
        public final static Property TransferType = new Property(5, Integer.class, "transferType", false, "TRANSFER_TYPE");
        public final static Property Amount = new Property(6, Long.class, "amount", false, "AMOUNT");
        public final static Property Message = new Property(7, String.class, "message", false, "MESSAGE");
        public final static Property TimeCreate = new Property(8, Long.class, "timeCreate", false, "TIME_CREATE");
    }


    public TransferRecentDao(DaoConfig config) {
        super(config);
    }
    
    public TransferRecentDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"TRANSFER_RECENT\" (" + //
                "\"ZALO_PAY_ID\" TEXT PRIMARY KEY NOT NULL ," + // 0: zaloPayId
                "\"ZALO_PAY_NAME\" TEXT," + // 1: zaloPayName
                "\"DISPLAY_NAME\" TEXT," + // 2: displayName
                "\"AVATAR\" TEXT," + // 3: avatar
                "\"PHONE_NUMBER\" TEXT," + // 4: phoneNumber
                "\"TRANSFER_TYPE\" INTEGER," + // 5: transferType
                "\"AMOUNT\" INTEGER," + // 6: amount
                "\"MESSAGE\" TEXT," + // 7: message
                "\"TIME_CREATE\" INTEGER);"); // 8: timeCreate
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"TRANSFER_RECENT\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, TransferRecent entity) {
        stmt.clearBindings();
 
        String zaloPayId = entity.zaloPayId;
        if (zaloPayId != null) {
            stmt.bindString(1, zaloPayId);
        }
 
        String zaloPayName = entity.zaloPayName;
        if (zaloPayName != null) {
            stmt.bindString(2, zaloPayName);
        }
 
        String displayName = entity.displayName;
        if (displayName != null) {
            stmt.bindString(3, displayName);
        }
 
        String avatar = entity.avatar;
        if (avatar != null) {
            stmt.bindString(4, avatar);
        }
 
        String phoneNumber = entity.phoneNumber;
        if (phoneNumber != null) {
            stmt.bindString(5, phoneNumber);
        }
 
        Integer transferType = entity.transferType;
        if (transferType != null) {
            stmt.bindLong(6, transferType);
        }
 
        Long amount = entity.amount;
        if (amount != null) {
            stmt.bindLong(7, amount);
        }
 
        String message = entity.message;
        if (message != null) {
            stmt.bindString(8, message);
        }
 
        Long timeCreate = entity.timeCreate;
        if (timeCreate != null) {
            stmt.bindLong(9, timeCreate);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, TransferRecent entity) {
        stmt.clearBindings();
 
        String zaloPayId = entity.zaloPayId;
        if (zaloPayId != null) {
            stmt.bindString(1, zaloPayId);
        }
 
        String zaloPayName = entity.zaloPayName;
        if (zaloPayName != null) {
            stmt.bindString(2, zaloPayName);
        }
 
        String displayName = entity.displayName;
        if (displayName != null) {
            stmt.bindString(3, displayName);
        }
 
        String avatar = entity.avatar;
        if (avatar != null) {
            stmt.bindString(4, avatar);
        }
 
        String phoneNumber = entity.phoneNumber;
        if (phoneNumber != null) {
            stmt.bindString(5, phoneNumber);
        }
 
        Integer transferType = entity.transferType;
        if (transferType != null) {
            stmt.bindLong(6, transferType);
        }
 
        Long amount = entity.amount;
        if (amount != null) {
            stmt.bindLong(7, amount);
        }
 
        String message = entity.message;
        if (message != null) {
            stmt.bindString(8, message);
        }
 
        Long timeCreate = entity.timeCreate;
        if (timeCreate != null) {
            stmt.bindLong(9, timeCreate);
        }
    }

    @Override
    public String readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0);
    }    

    @Override
    public TransferRecent readEntity(Cursor cursor, int offset) {
        TransferRecent entity = new TransferRecent();
        readEntity(cursor, entity, offset);
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, TransferRecent entity, int offset) {
        entity.zaloPayId = cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0);
        entity.zaloPayName = cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1);
        entity.displayName = cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2);
        entity.avatar = cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3);
        entity.phoneNumber = cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4);
        entity.transferType = cursor.isNull(offset + 5) ? null : cursor.getInt(offset + 5);
        entity.amount = cursor.isNull(offset + 6) ? null : cursor.getLong(offset + 6);
        entity.message = cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7);
        entity.timeCreate = cursor.isNull(offset + 8) ? null : cursor.getLong(offset + 8);
     }
    
    @Override
    protected final String updateKeyAfterInsert(TransferRecent entity, long rowId) {
        return entity.zaloPayId;
    }
    
    @Override
    public String getKey(TransferRecent entity) {
        if(entity != null) {
            return entity.zaloPayId;
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(TransferRecent entity) {
        return entity.zaloPayId != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
