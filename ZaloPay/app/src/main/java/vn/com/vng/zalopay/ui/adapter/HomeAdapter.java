package vn.com.vng.zalopay.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.airbnb.epoxy.EpoxyAdapter;
import com.airbnb.epoxy.EpoxyHolder;
import com.airbnb.epoxy.EpoxyModel;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;
import vn.com.vng.zalopay.data.util.Lists;
import vn.com.vng.zalopay.domain.model.AppResource;
import vn.com.vng.zalopay.ui.adapter.model.AppItemModel;
import vn.com.vng.zalopay.ui.adapter.model.BannerModel;
import vn.com.zalopay.wallet.business.entity.gatewayinfo.DBanner;

/**
 * Created by hieuvm on 3/21/17.
 * Adapter list application in home page
 */

public class HomeAdapter extends EpoxyAdapter {

    public interface OnClickAppItemListener {
        void onClickBanner(DBanner banner, int index);

        void onClickAppItem(AppResource app, int position);
    }

    private BannerModel bannerModel;
    private static final int POSITION_BANNER = 6;
    private OnClickAppItemListener clickListener;

    public HomeAdapter(Context context, OnClickAppItemListener listener) {
        super();
        clickListener = listener;
        bannerModel = new BannerModel();
        bannerModel.setClickListener(clickListener);
        enableDiffing();
    }


    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        clickListener = null;
        super.onDetachedFromRecyclerView(recyclerView);
    }

    private EpoxyModel<?> getModelForPosition(int position) {
        EpoxyModel<?> epoxyModel = models.get(position);
        return epoxyModel.isShown() ? epoxyModel : null;
    }

    public void setAppItems(@NonNull List<AppResource> list) {
        Timber.d("set app items size [%s]", list.size());

        int sizeApp = list.size();

        if (sizeApp == 0) {
            removeAllModels();
            return;
        }

        int sizeModels = models.size();
        if (sizeModels == 0) {
            addModels(transform(list));
            return;
        }

        try {
            updateAppModels(models, getAppModels(), list);
        } catch (IndexOutOfBoundsException e) {
            Timber.d(e);
        }

        // Tự động kiểm tra thay đổi rùi render lại.
        notifyModelsChanged();
    }


    private List<AppItemModel> getAppModels() {
        List<AppItemModel> modelsTmp = new ArrayList<>();
        for (EpoxyModel<?> model : models) {
            if (model instanceof AppItemModel) {
                modelsTmp.add((AppItemModel) model);
            }
        }
        return modelsTmp;
    }

    private void updateAppModels(List<EpoxyModel<?>> rootModels, List<AppItemModel> modelsApp, @NonNull List<AppResource> list) throws IndexOutOfBoundsException {
        int sizeApp = list.size();
        int sizeModelsApp = modelsApp.size();
        //  int indexBanner = models.indexOf(bannerModel);

        if (sizeModelsApp == sizeApp) {
            for (int i = 0; i < sizeApp; i++) {
                AppItemModel model = modelsApp.get(i);
                if (model == null) {
                    continue;
                }
                model.show();
                model.setApp(list.get(i));
            }
        } else if (sizeModelsApp > sizeApp) {
            for (int i = 0; i < sizeApp; i++) {
                AppItemModel model = modelsApp.get(i);
                if (model == null) {
                    continue;
                }
                model.show();
                model.setApp(list.get(i));
            }

            int hideModelsApp = sizeModelsApp - sizeApp;

            for (int i = 0; i < hideModelsApp; i++) {
                AppItemModel appItemModel = modelsApp.get(sizeApp + i);
                if (appItemModel == null) {
                    continue;
                }
                appItemModel.hide();
            }

        } else {
            for (int i = 0; i < sizeModelsApp; i++) {
                AppItemModel model = modelsApp.get(i);
                if (model == null) {
                    continue;
                }
                model.show();
                model.setApp(list.get(i));
            }

            int newModelsApp = sizeApp - sizeModelsApp;

            for (int i = 0; i < newModelsApp; i++) {
                AppResource appResource = list.get(sizeModelsApp + i);
                if (appResource == null) {
                    continue;
                }
                rootModels.add(transform(appResource));
            }
        }

    }


    public void setBanners(@NonNull List<DBanner> banners) {
        Timber.d("set banners size [%s]", banners.size());
        int sizeModels = models.size();
        if (sizeModels == 0) {
            return;
        }

        bannerModel.setData(banners);

        boolean exist = models.contains(bannerModel);
        int sizeBanner = banners.size();

        if (sizeBanner == 0) {
            if (exist) {
                hideModel(bannerModel);
            }
        } else {
            bannerModel.show();
            if (exist) {
                notifyModelChanged(bannerModel);
                return;
            }

            if (sizeModels <= POSITION_BANNER) {
                addModel(bannerModel);
            } else {
                EpoxyModel epoxyModel = getModelForPosition(POSITION_BANNER - 1);
                if (epoxyModel != null) {
                    insertModelAfter(bannerModel, epoxyModel);
                }
            }
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
            ((LinearLayoutManager) recyclerView.getLayoutManager()).setRecycleChildrenOnDetach(true);
        }
    }

    private final AppItemModel.OnAppModelClickListener appClickListener = appModer -> {
        if (clickListener != null) {
            clickListener.onClickAppItem(appModer.app, getModelPosition(appModer));
        }
    };

    private AppItemModel transform(AppResource resource) {
        return new AppItemModel(resource, appClickListener);
    }

    private List<AppItemModel> transform(List<AppResource> resources) {
        return Lists.transform(resources, this::transform);
    }

    public void pause() {
        if (bannerModel.isShown()) {
            bannerModel.pause();
        }
    }

    public void resume() {
        if (bannerModel.isShown()) {
            bannerModel.resume();
        }
    }
}
