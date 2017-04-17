package vn.com.vng.zalopay.feedback;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;

import com.zalopay.apploader.internal.FileUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import timber.log.Timber;
import vn.com.vng.zalopay.R;
import vn.com.vng.zalopay.data.UserCollector;
import vn.com.vng.zalopay.data.cache.UserConfig;
import vn.com.vng.zalopay.data.util.JsonUtil;
import vn.com.vng.zalopay.domain.interactor.DefaultSubscriber;
import vn.com.vng.zalopay.navigation.Navigator;
import vn.com.vng.zalopay.ui.presenter.AbstractPresenter;
import vn.com.vng.zalopay.utils.PhotoUtil;
import vn.zalopay.feedback.FeedbackCollector;
import vn.zalopay.feedback.collectors.AppCollector;
import vn.zalopay.feedback.collectors.DeviceCollector;
import vn.zalopay.feedback.collectors.DynamicCollector;
import vn.zalopay.feedback.collectors.NetworkCollector;

import static vn.com.vng.zalopay.data.util.ObservableHelper.makeObservable;

/**
 * Created by hieuvm on 1/6/17.
 */

final class FeedbackPresenter extends AbstractPresenter<IFeedbackView> {

    private final Context mContext;
    private final UserConfig mUserConfig;

    private final Navigator mNavigator;

    @Inject
    FeedbackPresenter(Context context, UserConfig userConfig, Navigator navigator) {
        this.mContext = context;
        this.mNavigator = navigator;
        this.mUserConfig = userConfig;
    }

    void sendEmail(String transactionID, String category, String email, final String description,
                   boolean user, boolean app, boolean device, final List<Uri> screenshot) {

        Timber.d("sendEmail: transId [%s] category [%s] email [%s] desc [%s] user [%s] app [%s] device [%s]",
                transactionID, category, email, description, user, app, device);

        FeedbackCollector feedbackCollector = collectInformation(user, app, device);
        DynamicCollector dynamicCollector = collectDynamic(transactionID, category, email, description);

        feedbackCollector.installCollector(dynamicCollector);

        feedbackCollector.startCollectors(new FeedbackCollector.CollectorListener() {
            @Override
            public void onCollectorEnd(JSONObject data) {
                Timber.d("onCollectorEnd: %s", data);
                if (mView == null) {
                    return;
                }

                Subscription subscription = saveCollectFile(data)
                        .map(new Func1<String, ArrayList<Uri>>() {
                            @Override
                            public ArrayList<Uri> call(String filePath) {
                                ArrayList<Uri> uris = new ArrayList<>(screenshot);
                                if (!TextUtils.isEmpty(filePath)) {
                                    Uri uri = FileProvider.getUriForFile(mContext,
                                            mContext.getString(R.string.file_provider),
                                            new File(filePath));
                                    Timber.d("uri file data %s", uri.toString());
                                    uris.add(uri);
                                }
                                return uris;
                            }
                        })
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new DefaultSubscriber<ArrayList<Uri>>() {
                            @Override
                            public void onNext(ArrayList<Uri> uris) {
                                if (mView == null) {
                                    return;
                                }

                                boolean result = mNavigator.startEmail((Activity) mView.getContext(), mContext.getString(R.string.email_support),
                                        null, mContext.getString(R.string.subject_compose_email_support),
                                        description, uris);
                                if (result) {
                                    mView.finish();
                                } else {
                                    mView.showError(mContext.getString(R.string.exception_send_feedback));
                                }

                            }
                        });
                mSubscription.add(subscription);

            }
        });
    }


    private Observable<String> saveCollectFile(final JSONObject data) {
        return makeObservable(new Callable<String>() {
            @Override
            public String call() throws Exception {
                String filePath = FileUtils.writeStringToFile(mContext, JsonUtil.toPrettyFormat(data.toString()), "data.txt");
                Timber.d("write to file _ filePath [%s] ", filePath);
                return filePath;
            }
        });
    }

    private DynamicCollector collectDynamic(String transactionID, String category, String email, String description) {
        DynamicCollector dynamicCollector = new DynamicCollector();
        try {
            dynamicCollector.put("transactionid", transactionID);
            dynamicCollector.put("category", category);
            dynamicCollector.put("email", email);
            dynamicCollector.put("description", description);
        } catch (JSONException ignore) {
        }
        return dynamicCollector;
    }

    void insertScreenshot(final byte[] screenshot) {
        Subscription subscription = makeObservable(new Callable<Uri>() {
            @Override
            public Uri call() throws Exception {
                File file = PhotoUtil.createPhotoFile(mContext, "screenshot-1.png");
                FileUtils.writeByteArrayToFile(screenshot, file.getAbsolutePath());
                return FileProvider.getUriForFile(mContext, mContext.getString(R.string.file_provider), file);
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DefaultSubscriber<Uri>() {
                    @Override
                    public void onNext(Uri uri) {
                        if (mView != null) {
                            mView.insertScreenshot(uri);
                        }
                    }
                });
        mSubscription.add(subscription);
    }

    private FeedbackCollector collectInformation(boolean user, boolean app, boolean device) {

        FeedbackCollector mCollector = new FeedbackCollector();

        if (user) {
            mCollector.installCollector(new UserCollector(mUserConfig));
        }

        if (app) {
            mCollector.installCollector(new AppCollector(mContext));
        }

        if (device) {
            mCollector.installCollector(new DeviceCollector(mContext));
        }
        mCollector.installCollector(new NetworkCollector(mContext));

        return mCollector;
    }


}
