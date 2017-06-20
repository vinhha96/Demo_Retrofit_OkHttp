/**
 * Copyright (c) 2015-present, Facebook, Inc.
 * All rights reserved.
 * <p>
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */

package com.zalopay.apploader.zpmodal;

import java.util.Map;

import android.content.DialogInterface;
import android.support.annotation.Nullable;

import com.facebook.react.common.MapBuilder;
import com.facebook.react.module.annotations.ReactModule;
import com.facebook.react.uimanager.LayoutShadowNode;
import com.facebook.react.uimanager.PixelUtil;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.UIManagerModule;
import com.facebook.react.uimanager.ViewGroupManager;
import com.facebook.react.uimanager.annotations.ReactProp;
import com.facebook.react.uimanager.events.EventDispatcher;
import com.facebook.react.bridge.ReactApplicationContext;
import com.zalopay.apploader.ReactNativeHostable;

/**
 * View manager for {@link ReactModalHostView} components.
 */
@ReactModule(name = ReactModalHostManager.REACT_CLASS)
public class ReactModalHostManager extends ViewGroupManager<ReactModalHostView> {

    protected static final String REACT_CLASS = "ZPModalHostView";

    private final ReactApplicationContext mContext;
    private final ReactNativeHostable mReactNativeHostable;

    public ReactModalHostManager(ReactApplicationContext context, ReactNativeHostable nativeInstanceManager) {
        mContext = context;
        mReactNativeHostable = nativeInstanceManager;
    }

    @Override
    public String getName() {
        return REACT_CLASS;
    }

    @Override
    protected ReactModalHostView createViewInstance(ThemedReactContext reactContext) {
        return new ReactModalHostView(reactContext, mReactNativeHostable);
    }

    @Override
    public LayoutShadowNode createShadowNodeInstance() {
        return new ModalHostShadowNode();
    }

    @Override
    public Class<? extends LayoutShadowNode> getShadowNodeClass() {
        return ModalHostShadowNode.class;
    }

    @Override
    public void onDropViewInstance(ReactModalHostView view) {
        super.onDropViewInstance(view);
        view.onDropInstance();
    }

    @ReactProp(name = "animationType")
    public void setAnimationType(ReactModalHostView view, String animationType) {
        view.setAnimationType(animationType);
    }

    @ReactProp(name = "transparent")
    public void setTransparent(ReactModalHostView view, boolean transparent) {
        view.setTransparent(transparent);
    }

    @ReactProp(name = "hardwareAccelerated")
    public void setHardwareAccelerated(ReactModalHostView view, boolean hardwareAccelerated) {
        view.setHardwareAccelerated(hardwareAccelerated);
    }

    @Override
    protected void addEventEmitters(
            ThemedReactContext reactContext,
            final ReactModalHostView view) {
        final EventDispatcher dispatcher =
                reactContext.getNativeModule(UIManagerModule.class).getEventDispatcher();
        view.setOnRequestCloseListener(
                new ReactModalHostView.OnRequestCloseListener() {
                    @Override
                    public void onRequestClose(DialogInterface dialog) {
                        dispatcher.dispatchEvent(new RequestCloseEvent(view.getId()));
                    }
                });
        view.setOnShowListener(
                new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialog) {
                        dispatcher.dispatchEvent(new ShowEvent(view.getId()));
                    }
                });
    }

    @Override
    public Map<String, Object> getExportedCustomDirectEventTypeConstants() {
        return MapBuilder.<String, Object>builder()
                .put(RequestCloseEvent.EVENT_NAME, MapBuilder.of("registrationName", "onRequestClose"))
                .put(ShowEvent.EVENT_NAME, MapBuilder.of("registrationName", "onShow"))
                .build();
    }

    @Override
    protected void onAfterUpdateTransaction(ReactModalHostView view) {
        super.onAfterUpdateTransaction(view);
        view.showOrUpdate();
    }

    @Override
    public
    @Nullable
    Map<String, Object> getExportedViewConstants() {
        final int heightResId = mContext.getResources().getIdentifier("status_bar_height", "dimen", "android");
        final float height = heightResId > 0 ?
                PixelUtil.toDIPFromPixel(mContext.getResources().getDimensionPixelSize(heightResId)) :
                0;

        return MapBuilder.of(
                "StatusBarHeight", height
        );
    }
}
