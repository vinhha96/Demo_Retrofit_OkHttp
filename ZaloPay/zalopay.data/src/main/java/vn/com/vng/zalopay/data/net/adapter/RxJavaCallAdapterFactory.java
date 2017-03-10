/*
 * Copyright (C) 2015 Square, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package vn.com.vng.zalopay.data.net.adapter;

import android.content.Context;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import retrofit2.CallAdapter;
import retrofit2.Retrofit;
import rx.Observable;
import rx.Scheduler;
import vn.com.vng.zalopay.data.Constants;

public final class RxJavaCallAdapterFactory extends CallAdapter.Factory {
    public enum AdapterType {
        ZaloPay,
        RedPacket,
        PaymentAppWithRetry,
        PaymentAppWithoutRetry,
        Connector
    }

    /**
     * TODO
     */
    public static RxJavaCallAdapterFactory create(Context context, AdapterType adapterType) {
        return new RxJavaCallAdapterFactory(null, context, adapterType);
    }

    /**
     * TODO
     */
    public static RxJavaCallAdapterFactory createWithScheduler(Scheduler scheduler, Context context, AdapterType adapterType) {
        if (scheduler == null) throw new NullPointerException("scheduler == null");
        return new RxJavaCallAdapterFactory(scheduler, context, adapterType);
    }

    private final Scheduler scheduler;
    private Context mApplicationContext;
    private final AdapterType mAdapterType;

    private RxJavaCallAdapterFactory(Scheduler scheduler, Context context, AdapterType adapterType) {
        this.scheduler = scheduler;
        this.mApplicationContext = context;
        this.mAdapterType = adapterType;
    }

    @Override
    public CallAdapter<?> get(Type returnType, Annotation[] annotations, Retrofit retrofit) {
        int apiEventId = -1;
        API_NAME apiNameAnnotation = getAnnotation(annotations);
        if (apiNameAnnotation != null) {
            apiEventId = apiNameAnnotation.value();
        }

        return getCallAdapter(returnType, scheduler, apiEventId);
    }

    private CallAdapter<Observable<?>> getCallAdapter(Type returnType, Scheduler scheduler, int apiEventId) {
        Type observableType = getParameterUpperBound(0, (ParameterizedType) returnType);

        switch (mAdapterType) {
            case ZaloPay:
                return new ZaloPayCallAdapter(mApplicationContext, apiEventId, observableType, scheduler);
            case RedPacket:
                return new RedPacketCallAdapter(mApplicationContext, apiEventId, observableType, scheduler);
            case PaymentAppWithRetry:
                return new RNCallAdapter(mApplicationContext, apiEventId, observableType, scheduler, Constants.NUMBER_RETRY_REST);
            case PaymentAppWithoutRetry:
                return new RNCallAdapter(mApplicationContext, apiEventId, observableType, scheduler, 0);
            case Connector:
                return new ConnectorCallAdapter(mApplicationContext, apiEventId, observableType, scheduler);
            default:
                return new ZaloPayCallAdapter(mApplicationContext, apiEventId, observableType, scheduler);
        }
    }

    private API_NAME getAnnotation(Annotation[] annotations) {
        for (Annotation annotation : annotations) {
            if (API_NAME.class == annotation.annotationType()) {
                return (API_NAME) annotation;
            }
        }
        return null;
    }
}
