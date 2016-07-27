package vn.com.vng.zalopay.data.notification;

import android.text.TextUtils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.greenrobot.dao.async.AsyncSession;
import timber.log.Timber;
import vn.com.vng.zalopay.data.cache.SqlBaseScopeImpl;
import vn.com.vng.zalopay.data.cache.model.DaoSession;
import vn.com.vng.zalopay.data.cache.model.NotificationGD;
import vn.com.vng.zalopay.data.cache.model.NotificationGDDao;
import vn.com.vng.zalopay.data.util.Lists;
import vn.com.vng.zalopay.data.ws.model.NotificationData;
import vn.com.vng.zalopay.domain.model.User;

import static java.util.Collections.emptyList;

/**
 * Created by AnhHieu on 6/20/16.
 */
public class NotificationLocalStorage extends SqlBaseScopeImpl implements NotificationStore.LocalStorage {

    final JsonParser jsonParser;
    final AsyncSession asyncSession;

    final User user;

    public NotificationLocalStorage(DaoSession daoSession, User user) {
        super(daoSession);
        jsonParser = new JsonParser();
        asyncSession = getDaoSession().getNotificationGDDao().getSession().startAsyncSession();
        this.user = user;
    }

    @Override
    public void put(List<NotificationData> val) {
        List<NotificationGD> list = transform(val);
        if (!Lists.isEmptyOrNull(list)) {
            getAsyncSession().insertOrReplaceInTx(NotificationGD.class, list);
        }
    }

    @Override
    public void put(NotificationData val) {
        NotificationGD item = transform(val);
        if (item != null) {
            Timber.d("Put item %s", item.getMessage());
            getAsyncSession().insertInTx(NotificationGD.class, item);
        }
    }


    @Override
    public List<NotificationData> get(int pageIndex, int limit) {
        return queryList(pageIndex, limit);
    }

    AsyncSession getAsyncSession() {
        return asyncSession;
    }

    private List<NotificationGD> transform(Collection<NotificationData> notificationEntities) {
        if (Lists.isEmptyOrNull(notificationEntities)) {
            return emptyList();
        }

        List<NotificationGD> notificationGDs = new ArrayList<>(notificationEntities.size());
        for (NotificationData notificationEntity : notificationEntities) {
            NotificationGD notificationGD = transform(notificationEntity);
            if (notificationGD == null) {
                continue;
            }

            notificationGDs.add(notificationGD);
        }

        return notificationGDs;
    }

    private NotificationGD transform(NotificationData notificationEntity) {
        if (notificationEntity == null) {
            return null;
        }

        NotificationGD _notification = new NotificationGD();
        _notification.setAppid(notificationEntity.getAppid());
        _notification.setDestuserid(notificationEntity.getDestuserid());
        _notification.setMessage(notificationEntity.getMessage());
        _notification.setTimestamp(notificationEntity.getTimestamp());
        _notification.setNotificationtype(notificationEntity.getNotificationType());
        JsonObject embeddataJson = notificationEntity.getEmbeddata();


        String embeddata = "";
        if (embeddataJson != null) {
            embeddata = embeddataJson.toString();
        }

        Timber.d("put embeddata %s isRead %s  ", embeddata, notificationEntity.read);

        _notification.setEmbeddata(embeddata);
        _notification.setUserid(notificationEntity.getUserid());
        _notification.setTransid(notificationEntity.getTransid());
        _notification.setRead(notificationEntity.isRead());

        if (notificationEntity.notificationId > 0) {
            _notification.setId(notificationEntity.notificationId);
        } else {
            _notification.setId(null);

        }

        return _notification;
    }

    private NotificationData transform(NotificationGD notificationGD) {
        if (notificationGD == null) {
            return null;
        }

        NotificationData _notification = new NotificationData();

        _notification.setNotificationId(notificationGD.getId()); // FIXME: 6/22/16 Change Id

        _notification.setAppid(notificationGD.getAppid());
        _notification.setDestuserid(notificationGD.getDestuserid());
        _notification.setMessage(notificationGD.getMessage());
        _notification.setTimestamp(notificationGD.getTimestamp());
        _notification.setNotificationtype(notificationGD.getNotificationtype());

        String embeddata = notificationGD.getEmbeddata();

        Timber.d("embeddata [%s]", embeddata);
        if (TextUtils.isEmpty(embeddata)) {
            _notification.setEmbeddata(new JsonObject());
        } else {
            try {
                _notification.setEmbeddata(jsonParser.parse(embeddata).getAsJsonObject());
            } catch (Exception ex) {
                _notification.setEmbeddata(new JsonObject());
                Timber.w(ex, " parse exception Notification Entity");
            }
        }

        _notification.setUserid(notificationGD.getUserid());
        _notification.setTransid(notificationGD.getTransid());
        _notification.setRead(notificationGD.getRead());

        return _notification;
    }

    private List<NotificationData> transformEntity(Collection<NotificationGD> notificationGDs) {
        if (Lists.isEmptyOrNull(notificationGDs)) {
            return emptyList();
        }

        List<NotificationData> notificationEntities = new ArrayList<>();
        for (NotificationGD notificationGD : notificationGDs) {
            NotificationData entity = transform(notificationGD);
            if (entity == null) {
                continue;
            }

            notificationEntities.add(entity);
        }

        return notificationEntities;
    }

    private List<NotificationData> queryList(int pageIndex, int limit) {
        return transformEntity(
                getDaoSession()
                        .getNotificationGDDao()
                        .queryBuilder()
                        .limit(limit)
                        .offset(pageIndex * limit)
                        .orderDesc(NotificationGDDao.Properties.Timestamp)
                        .list());
    }

    private List<NotificationGD> queryList() {
        return getDaoSession()
                .getNotificationGDDao()
                .queryBuilder()
                .list();
    }

    private List<NotificationGD> queryListUnRead() {
        return getDaoSession()
                .getNotificationGDDao()
                .queryBuilder()
                .where(NotificationGDDao.Properties.Read.eq(false))
                .list();
    }

    private long totalUnRead() {
        return getDaoSession()
                .getNotificationGDDao()
                .queryBuilder()
                .where(NotificationGDDao.Properties.Read.eq(false))
                .count();
    }

    private List<NotificationGD> queryNotification(long id) {
        return getDaoSession()
                .getNotificationGDDao()
                .queryBuilder()
                .where(NotificationGDDao.Properties.Id.eq(id)) //// FIXME: 6/22/16 Change id
                .list();
    }

    @Override
    public void markAsRead(long nId) {
        NotificationGD notify = getDaoSession().load(NotificationGD.class, nId);
        if (notify != null) {
            notify.setRead(true);
            Timber.d("markAsRead: nId %s", nId);
            getAsyncSession().insertOrReplaceInTx(NotificationGD.class, notify);
        }
    }

    @Override
    public void markAsReadAll() {
        List<NotificationGD> list = queryListUnRead();
        for (NotificationGD notify : list) {
            notify.setRead(true);
        }

        if (!Lists.isEmptyOrNull(list)) {
            getAsyncSession().insertOrReplaceInTx(NotificationGD.class, list);
        }
    }

    @Override
    public int totalNotificationUnRead() {
        return (int) totalUnRead();
    }

    @Override
    public NotificationData get(long notifyId) {
        return transform(getDaoSession().getNotificationGDDao().load(notifyId));
    }
}
