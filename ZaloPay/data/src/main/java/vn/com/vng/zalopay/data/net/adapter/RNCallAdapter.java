package vn.com.vng.zalopay.data.net.adapter;

import android.content.Context;
import android.support.annotation.NonNull;

import java.lang.reflect.Type;

import retrofit2.Response;
import retrofit2.adapter.rxjava.HttpException;
import rx.Observable;
import rx.Scheduler;
import timber.log.Timber;
import vn.com.vng.zalopay.data.api.response.BaseResponse;
import vn.com.vng.zalopay.data.exception.HttpEmptyResponseException;

/**
 * Created by AnhHieu on 9/20/16.
 * *
 */

public class RNCallAdapter extends BaseCallAdapter {

    public RNCallAdapter(Context context, int apiClientId, Type responseType, Scheduler scheduler) {
        super(context, apiClientId, responseType, scheduler, 0);
    }

    public RNCallAdapter(Context context, int apiClientId, Type responseType, Scheduler scheduler, int retryNumber) {
        super(context, apiClientId, responseType, scheduler, retryNumber);
    }

    @NonNull
    @Override
    protected <R> Observable<? extends R> makeObservableFromResponse(Response<R> response) {
        if (response == null) {
            Timber.d("makeObservableFromResponse NULL response");
            return Observable.error(new HttpEmptyResponseException());
        }

        if (!response.isSuccessful()) {
            Timber.d("makeObservableFromResponse UNSUCCESSFUL response");

            return Observable.error(new HttpException(response));
        }

        return Observable.just(response.body());
    }

    @Override
    protected <R> Observable<? extends R> handleServerResponseError(BaseResponse body, BaseResponse baseResponse) {
        return null;
    }
}