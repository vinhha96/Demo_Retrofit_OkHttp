package vn.com.zalopay.wallet.repository.platforminfo;

import java.util.Map;

import javax.inject.Inject;

import rx.Observable;
import vn.com.zalopay.wallet.business.entity.gatewayinfo.PlatformInfoResponse;
import vn.com.zalopay.wallet.constants.ConstantParams;
import vn.com.zalopay.wallet.api.RetryWithDelay;
import vn.com.zalopay.wallet.business.entity.gatewayinfo.PlatformInfoResponse;
import vn.com.zalopay.wallet.constants.ConstantParams;
import vn.com.zalopay.wallet.constants.Constants;

/**
 * Created by chucvv on 6/7/17.
 */

public class PlatformInfoRepository implements PlatformInfoStore.Repository {
    private PlatformInfoStore.LocalStorage localStorage;
    private PlatformInfoStore.PlatformInfoService platformInfoService;

    @Inject
    public PlatformInfoRepository(PlatformInfoStore.PlatformInfoService platformInfoService, PlatformInfoStore.LocalStorage localStorage) {
        this.platformInfoService = platformInfoService;
        this.localStorage = localStorage;
    }

    @Override
    public Observable<PlatformInfoResponse> fetchCloud(Map<String, String> params) {
        return platformInfoService.fetch(params)
                .doOnNext(platformInfoResponse -> localStorage.put(params.get(ConstantParams.USER_ID), platformInfoResponse));
    }

    @Override
    public PlatformInfoStore.LocalStorage getLocalStorage() {
        return localStorage;
    }
}
