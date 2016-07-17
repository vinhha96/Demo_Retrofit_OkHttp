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

public final class RxJavaCallAdapterFactory extends CallAdapter.Factory {
    /**
     * TODO
     */
    public static RxJavaCallAdapterFactory create(Context context) {
        return new RxJavaCallAdapterFactory(null, context);
    }

    /**
     * TODO
     */
    public static RxJavaCallAdapterFactory createWithScheduler(Scheduler scheduler, Context context) {
        if (scheduler == null) throw new NullPointerException("scheduler == null");
        return new RxJavaCallAdapterFactory(scheduler, context);
    }

    private final Scheduler scheduler;

    private Context mApplicationContext;

    private RxJavaCallAdapterFactory(Scheduler scheduler, Context context) {
        this.scheduler = scheduler;
        this.mApplicationContext = context;
    }

    @Override
    public CallAdapter<?> get(Type returnType, Annotation[] annotations, Retrofit retrofit) {
        return getCallAdapter(returnType, scheduler);
    }

    private CallAdapter<Observable<?>> getCallAdapter(Type returnType, Scheduler scheduler) {
        Type observableType = getParameterUpperBound(0, (ParameterizedType) returnType);

        return new ZaloPayCallAdapter(mApplicationContext, observableType, scheduler);
    }
}
