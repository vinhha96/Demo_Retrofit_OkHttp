package vn.com.vng.zalopay.data.cache.global;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "APPTRANSID_LOG_GD".
*/
public class ApptransidLogGDDao extends AbstractDao<ApptransidLogGD, String> {

    public static final String TABLENAME = "APPTRANSID_LOG_GD";

    /**
     * Properties of entity ApptransidLogGD.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Apptransid = new Property(0, String.class, "apptransid", true, "APPTRANSID");
        public final static Property Appid = new Property(1, Long.class, "appid", false, "APPID");
        public final static Property Step = new Property(2, Integer.class, "step", false, "STEP");
        public final static Property Step_result = new Property(3, Integer.class, "step_result", false, "STEP_RESULT");
        public final static Property Pcmid = new Property(4, Integer.class, "pcmid", false, "PCMID");
        public final static Property Transtype = new Property(5, Integer.class, "transtype", false, "TRANSTYPE");
        public final static Property Transid = new Property(6, Long.class, "transid", false, "TRANSID");
        public final static Property Sdk_result = new Property(7, Integer.class, "sdk_result", false, "SDK_RESULT");
        public final static Property Server_result = new Property(8, Integer.class, "server_result", false, "SERVER_RESULT");
        public final static Property Source = new Property(9, String.class, "source", false, "SOURCE");
        public final static Property Start_time = new Property(10, Long.class, "start_time", false, "START_TIME");
        public final static Property Finish_time = new Property(11, Long.class, "finish_time", false, "FINISH_TIME");
        public final static Property Bank_code = new Property(12, String.class, "bank_code", false, "BANK_CODE");
        public final static Property Status = new Property(13, Integer.class, "status", false, "STATUS");
    }


    public ApptransidLogGDDao(DaoConfig config) {
        super(config);
    }
    
    public ApptransidLogGDDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"APPTRANSID_LOG_GD\" (" + //
                "\"APPTRANSID\" TEXT PRIMARY KEY NOT NULL UNIQUE ," + // 0: apptransid
                "\"APPID\" INTEGER," + // 1: appid
                "\"STEP\" INTEGER," + // 2: step
                "\"STEP_RESULT\" INTEGER," + // 3: step_result
                "\"PCMID\" INTEGER," + // 4: pcmid
                "\"TRANSTYPE\" INTEGER," + // 5: transtype
                "\"TRANSID\" INTEGER," + // 6: transid
                "\"SDK_RESULT\" INTEGER," + // 7: sdk_result
                "\"SERVER_RESULT\" INTEGER," + // 8: server_result
                "\"SOURCE\" TEXT," + // 9: source
                "\"START_TIME\" INTEGER," + // 10: start_time
                "\"FINISH_TIME\" INTEGER," + // 11: finish_time
                "\"BANK_CODE\" TEXT," + // 12: bank_code
                "\"STATUS\" INTEGER);"); // 13: status
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"APPTRANSID_LOG_GD\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, ApptransidLogGD entity) {
        stmt.clearBindings();
        stmt.bindString(1, entity.apptransid);
 
        Long appid = entity.appid;
        if (appid != null) {
            stmt.bindLong(2, appid);
        }
 
        Integer step = entity.step;
        if (step != null) {
            stmt.bindLong(3, step);
        }
 
        Integer step_result = entity.step_result;
        if (step_result != null) {
            stmt.bindLong(4, step_result);
        }
 
        Integer pcmid = entity.pcmid;
        if (pcmid != null) {
            stmt.bindLong(5, pcmid);
        }
 
        Integer transtype = entity.transtype;
        if (transtype != null) {
            stmt.bindLong(6, transtype);
        }
 
        Long transid = entity.transid;
        if (transid != null) {
            stmt.bindLong(7, transid);
        }
 
        Integer sdk_result = entity.sdk_result;
        if (sdk_result != null) {
            stmt.bindLong(8, sdk_result);
        }
 
        Integer server_result = entity.server_result;
        if (server_result != null) {
            stmt.bindLong(9, server_result);
        }
 
        String source = entity.source;
        if (source != null) {
            stmt.bindString(10, source);
        }
 
        Long start_time = entity.start_time;
        if (start_time != null) {
            stmt.bindLong(11, start_time);
        }
 
        Long finish_time = entity.finish_time;
        if (finish_time != null) {
            stmt.bindLong(12, finish_time);
        }
 
        String bank_code = entity.bank_code;
        if (bank_code != null) {
            stmt.bindString(13, bank_code);
        }
 
        Integer status = entity.status;
        if (status != null) {
            stmt.bindLong(14, status);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, ApptransidLogGD entity) {
        stmt.clearBindings();
        stmt.bindString(1, entity.apptransid);
 
        Long appid = entity.appid;
        if (appid != null) {
            stmt.bindLong(2, appid);
        }
 
        Integer step = entity.step;
        if (step != null) {
            stmt.bindLong(3, step);
        }
 
        Integer step_result = entity.step_result;
        if (step_result != null) {
            stmt.bindLong(4, step_result);
        }
 
        Integer pcmid = entity.pcmid;
        if (pcmid != null) {
            stmt.bindLong(5, pcmid);
        }
 
        Integer transtype = entity.transtype;
        if (transtype != null) {
            stmt.bindLong(6, transtype);
        }
 
        Long transid = entity.transid;
        if (transid != null) {
            stmt.bindLong(7, transid);
        }
 
        Integer sdk_result = entity.sdk_result;
        if (sdk_result != null) {
            stmt.bindLong(8, sdk_result);
        }
 
        Integer server_result = entity.server_result;
        if (server_result != null) {
            stmt.bindLong(9, server_result);
        }
 
        String source = entity.source;
        if (source != null) {
            stmt.bindString(10, source);
        }
 
        Long start_time = entity.start_time;
        if (start_time != null) {
            stmt.bindLong(11, start_time);
        }
 
        Long finish_time = entity.finish_time;
        if (finish_time != null) {
            stmt.bindLong(12, finish_time);
        }
 
        String bank_code = entity.bank_code;
        if (bank_code != null) {
            stmt.bindString(13, bank_code);
        }
 
        Integer status = entity.status;
        if (status != null) {
            stmt.bindLong(14, status);
        }
    }

    @Override
    public String readKey(Cursor cursor, int offset) {
        return cursor.getString(offset + 0);
    }    

    @Override
    public ApptransidLogGD readEntity(Cursor cursor, int offset) {
        ApptransidLogGD entity = new ApptransidLogGD();
        readEntity(cursor, entity, offset);
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, ApptransidLogGD entity, int offset) {
        entity.apptransid = cursor.getString(offset + 0);
        entity.appid = cursor.isNull(offset + 1) ? null : cursor.getLong(offset + 1);
        entity.step = cursor.isNull(offset + 2) ? null : cursor.getInt(offset + 2);
        entity.step_result = cursor.isNull(offset + 3) ? null : cursor.getInt(offset + 3);
        entity.pcmid = cursor.isNull(offset + 4) ? null : cursor.getInt(offset + 4);
        entity.transtype = cursor.isNull(offset + 5) ? null : cursor.getInt(offset + 5);
        entity.transid = cursor.isNull(offset + 6) ? null : cursor.getLong(offset + 6);
        entity.sdk_result = cursor.isNull(offset + 7) ? null : cursor.getInt(offset + 7);
        entity.server_result = cursor.isNull(offset + 8) ? null : cursor.getInt(offset + 8);
        entity.source = cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9);
        entity.start_time = cursor.isNull(offset + 10) ? null : cursor.getLong(offset + 10);
        entity.finish_time = cursor.isNull(offset + 11) ? null : cursor.getLong(offset + 11);
        entity.bank_code = cursor.isNull(offset + 12) ? null : cursor.getString(offset + 12);
        entity.status = cursor.isNull(offset + 13) ? null : cursor.getInt(offset + 13);
     }
    
    @Override
    protected final String updateKeyAfterInsert(ApptransidLogGD entity, long rowId) {
        return entity.apptransid;
    }
    
    @Override
    public String getKey(ApptransidLogGD entity) {
        if(entity != null) {
            return entity.apptransid;
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(ApptransidLogGD entity) {
        throw new UnsupportedOperationException("Unsupported for entities with a non-null key");
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
