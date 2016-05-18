package vn.com.vng.zalopay.data.cache;

import java.util.List;

import rx.Observable;
import vn.com.vng.zalopay.data.api.entity.CardEntity;

/**
 * Created by AnhHieu on 4/28/16.
 */
public interface SqlitePlatformScope extends SqlBaseScope {
    void writeCards(List<CardEntity> listCard);

    void write(CardEntity card);

    Observable<List<CardEntity>> listCard();
}
