package vn.com.vng.zalopay.greendao;

import org.greenrobot.greendao.generator.DaoGenerator;
import org.greenrobot.greendao.generator.Entity;
import org.greenrobot.greendao.generator.Index;
import org.greenrobot.greendao.generator.Property;
import org.greenrobot.greendao.generator.Schema;


public class GreenDaoGenerator {
    private static final int APP_DB_VERSION = 62;
    private static final int GLOBAL_DB_VERSION = 4;

    /**
     * ./gradlew :greendaogenerator:run
     */

    public static void main(String[] args) throws Exception {
        Schema appSchema = new Schema(APP_DB_VERSION, "vn.com.vng.zalopay.data.cache.model");
        Schema globalSchema = new Schema(GLOBAL_DB_VERSION, "vn.com.vng.zalopay.data.cache.global");

        //ADD TABLE
        addApplicationInfo(appSchema);
        addTransactionLog(appSchema);
        addDataManifest(appSchema);
        addZaloContact(appSchema);
        addTransferRecent(appSchema);
        addNotification(appSchema);
        addRedPacket(appSchema);
        addMerchantUser(appSchema);

        addTransactionFragment(appSchema);

        //ADD TABLE GLOBAL
        addGlobalKeyValue(globalSchema);
        addApptransidLog(globalSchema);
        addApptransidLogTiming(globalSchema);
        addApptransidLogApiCall(globalSchema);
        addGoogleAnalytics(globalSchema);

        DaoGenerator daoGenerator = new DaoGenerator("./daogenerator/src-template/");
        daoGenerator.generateAll(appSchema, "../zalopay.data/src/main/java");
        daoGenerator.generateAll(globalSchema, "../zalopay.data/src/main/java");
    }

    private static void addRedPacket(Schema appSchema) {
        Entity receivePackageGD = appSchema.addEntity("ReceivePackageGD");
        receivePackageGD.setConstructors(false);
        receivePackageGD.addIdProperty().unique().notNull();//packageID
        receivePackageGD.addLongProperty("bundleID");
        receivePackageGD.addStringProperty("receiverZaloPayID");
        receivePackageGD.addStringProperty("senderZaloPayID");
        receivePackageGD.addStringProperty("senderFullName");
        receivePackageGD.addStringProperty("senderAvatar");
        receivePackageGD.addLongProperty("amount");
        receivePackageGD.addLongProperty("openedTime");
        receivePackageGD.addLongProperty("status");
        receivePackageGD.addStringProperty("messageStatus");
        receivePackageGD.addStringProperty("message");
        receivePackageGD.addLongProperty("isLuckiest");
        receivePackageGD.addLongProperty("createTime");
    }

    private static Entity addZaloFriendList(Schema appSchema) {
        Entity entity = appSchema.addEntity("ZFL");
        entity.setConstructors(false);
        //entity.addIdProperty().primaryKey().autoincrement();
        entity.addLongProperty("zaloId").dbName("_id").primaryKey().notNull();
        entity.addStringProperty("userName");
        entity.addStringProperty("avatar");
        entity.addStringProperty("birthday");
        entity.addStringProperty("displayName");
        entity.addStringProperty("normalizeDisplayName");

        entity.addLongProperty("gender").notNull();
        entity.addBooleanProperty("usingApp").notNull();

        return entity;
    }

    private static Entity addZaloPayContact(Schema appSchema) {
        Entity entity = appSchema.addEntity("ZPC");
        entity.setConstructors(false);
        entity.addIdProperty().primaryKey().autoincrement();
        entity.addLongProperty("zaloId").unique().notNull();
        entity.addLongProperty("zalopayId").unique().notNull();
        entity.addStringProperty("phoneNumber").unique();
        entity.addStringProperty("zalopayName").unique();
        entity.addStringProperty("avatar");
        entity.addStringProperty("displayName");
        entity.addStringProperty("normalizeDisplayName");
        entity.addLongProperty("status").notNull();
        return entity;
    }

    private static Entity addContactBook(Schema appSchema) {
        Entity entity = appSchema.addEntity("UCB");
        entity.setConstructors(false);
        entity.addIdProperty().primaryKey().autoincrement();
        entity.addStringProperty("phoneNumber").unique().notNull();
        entity.addStringProperty("displayName");
        entity.addStringProperty("normalizeDisplayName");
        entity.addStringProperty("photoUri");
        return entity;
    }

    private static Entity addFavoriteZPC(Schema schema) {
        Entity entity = schema.addEntity("FavoriteZPC");
        entity.setConstructors(false);
        Property phoneNumber = entity.addStringProperty("phoneNumber").getProperty();
        Property zaloId = entity.addLongProperty("zaloId").notNull().getProperty();
        entity.addLongProperty("createTime").notNull();

        Index index = new Index();
        index.addProperty(phoneNumber);
        index.addProperty(zaloId);
        index.makeUnique();
        entity.addIndex(index);

        return entity;
    }

    private static void addZaloContact(Schema appSchema) {
        addZaloFriendList(appSchema);
        addZaloPayContact(appSchema);
        addContactBook(appSchema);
        addFavoriteZPC(appSchema);
    }

    private static void addTransferRecent(Schema appSchema) {
        Entity entity = appSchema.addEntity("TransferRecent");
        entity.setConstructors(false);
        entity.addStringProperty("zaloPayId").primaryKey();
        entity.addStringProperty("zaloPayName");
        entity.addStringProperty("displayName");
        entity.addStringProperty("avatar");
        entity.addStringProperty("phoneNumber");
        entity.addLongProperty("transferType");
        entity.addLongProperty("amount");
        entity.addStringProperty("message");
        entity.addLongProperty("timeCreate");
    }

    private static void addApplicationInfo(Schema schema) {
        Entity entity = schema.addEntity("AppResourceGD");
        entity.setConstructors(false);

        entity.addLongProperty("appid").notNull().unique();
        entity.addStringProperty("appname");
        entity.addLongProperty("needdownloadrs");
        entity.addStringProperty("imageurl");
        entity.addStringProperty("jsurl");
        entity.addLongProperty("status");
        entity.addStringProperty("checksum");
        entity.addLongProperty("apptype");
        entity.addStringProperty("weburl");
        entity.addStringProperty("iconname");
        entity.addStringProperty("iconcolor");
        entity.addLongProperty("sortOrder");

        entity.addLongProperty("downloadState");
        entity.addLongProperty("downloadTime");
        entity.addLongProperty("retryNumber");
    }

    private static void addTransactionLog(Schema schema) {
        addTransactionLog(schema, "TransactionLog");
    }

    private static void addTransactionLog(Schema schema, String entityName) {
        Entity entity = schema.addEntity(entityName);
        entity.setConstructors(false);
        entity.addLongProperty("transid").notNull().unique().primaryKey();
        entity.addLongProperty("appid").notNull();

        entity.addStringProperty("userid");
        entity.addStringProperty("appuser");

        entity.addStringProperty("platform");
        entity.addStringProperty("description");
        entity.addLongProperty("pmcid");
        entity.addLongProperty("reqdate");
        entity.addLongProperty("userchargeamt");
        entity.addLongProperty("userfeeamt");
        entity.addLongProperty("amount");
        entity.addLongProperty("type");
        entity.addLongProperty("sign");
        entity.addStringProperty("username");
        entity.addStringProperty("appusername");
        entity.addLongProperty("statustype");
        entity.addStringProperty("thank_message");
    }

    private static void addDataManifest(Schema schema) {
        Entity entity = schema.addEntity("DataManifest");
        entity.setConstructors(false);
        entity.addStringProperty("key").notNull().unique().primaryKey();
        entity.addStringProperty("value");
    }

    private static void addNotification(Schema schema) {
        Entity entity = schema.addEntity("NotificationGD");
        entity.setConstructors(false);
        entity.addIdProperty().primaryKey().autoincrement();
        entity.addLongProperty("transid");
        entity.addLongProperty("appid");
        entity.addLongProperty("timestamp");
        entity.addStringProperty("message");
        entity.addStringProperty("userid");
        entity.addStringProperty("destuserid");
        entity.addLongProperty("area");
        entity.addLongProperty("notificationstate");
        entity.addLongProperty("notificationtype");
        Property mtaid = entity.addLongProperty("mtaid").getProperty();
        Property mtuid = entity.addLongProperty("mtuid").getProperty();
        entity.addStringProperty("embeddata");

        Index index = new Index();
        index.addProperty(mtaid);
        index.addProperty(mtuid);
        index.makeUnique();

        entity.addIndex(index);
    }

    private static void addMerchantUser(Schema schema) {
        Entity entity = schema.addEntity("MerchantUser");
        entity.setConstructors(false);
        entity.addLongProperty("appid").primaryKey().notNull();
        entity.addStringProperty("mUid");
        entity.addStringProperty("mAccessToken");
        entity.addStringProperty("displayName");
        entity.addStringProperty("avatar");
        entity.addStringProperty("birthday");
        entity.addLongProperty("gender");
    }

    private static void addApptransidLog(Schema schema) {
        Entity entity = schema.addEntity("ApptransidLogGD");
        entity.setConstructors(false);
        entity.addStringProperty("apptransid").notNull().unique().primaryKey();
        entity.addLongProperty("appid");
        entity.addIntProperty("step");
        entity.addIntProperty("step_result");
        entity.addIntProperty("pcmid");
        entity.addIntProperty("transtype");
        entity.addLongProperty("transid");
        entity.addIntProperty("sdk_result");
        entity.addIntProperty("server_result");
        entity.addIntProperty("source");
        entity.addLongProperty("start_time");
        entity.addLongProperty("finish_time");
        entity.addStringProperty("bank_code");
        entity.addIntProperty("status");
    }

    private static void addGoogleAnalytics(Schema schema) {
        Entity entity = schema.addEntity("GoogleAnalytics");
        entity.setConstructors(false);
        entity.addIdProperty().primaryKey().autoincrement();
        entity.addStringProperty("type").notNull();
        entity.addStringProperty("payload").notNull();
        entity.addLongProperty("timestamp").notNull();
    }

    private static void addApptransidLogTiming(Schema schema) {
        Entity entity = schema.addEntity("ApptransidLogTimingGD");
        entity.setConstructors(false);
        entity.addStringProperty("apptransid");
        entity.addIntProperty("step");
        entity.addLongProperty("timestamp").notNull().unique().primaryKey();
    }

    private static void addApptransidLogApiCall(Schema schema) {
        Entity entity = schema.addEntity("ApptransidLogApiCallGD");
        entity.setConstructors(false);
        entity.addIdProperty().primaryKey().autoincrement();
        entity.addStringProperty("apptransid");
        entity.addLongProperty("apiid");
        entity.addLongProperty("timebegin");
        entity.addLongProperty("timeend");
        entity.addIntProperty("returncode");
    }

    private static void addGlobalKeyValue(Schema schema) {
        Entity entity = schema.addEntity("KeyValueGD");
        entity.setConstructors(false);
        entity.addStringProperty("key").notNull().unique().primaryKey();
        entity.addStringProperty("value");
    }

    private static void addTransactionFragment(Schema schema) {
        Entity entity = schema.addEntity("TransactionFragmentGD");
        entity.setConstructors(false);
        entity.addLongProperty("statustype").notNull();
        entity.addLongProperty("maxreqdate").notNull();
        entity.addLongProperty("minreqdate").notNull().primaryKey();
        entity.addBooleanProperty("outofdata").notNull();
    }
}
