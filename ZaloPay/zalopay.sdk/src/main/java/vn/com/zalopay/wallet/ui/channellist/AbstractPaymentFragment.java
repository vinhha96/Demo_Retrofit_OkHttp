package vn.com.zalopay.wallet.ui.channellist;

import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewStub;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import timber.log.Timber;
import vn.com.vng.zalopay.data.util.NameValuePair;
import vn.com.zalopay.utility.CurrencyUtil;
import vn.com.zalopay.utility.SdkUtils;
import vn.com.zalopay.wallet.R;
import vn.com.zalopay.wallet.business.data.GlobalData;
import vn.com.zalopay.wallet.business.data.Log;
import vn.com.zalopay.wallet.business.data.RS;
import vn.com.zalopay.wallet.business.entity.base.StatusResponse;
import vn.com.zalopay.wallet.business.entity.enumeration.ESuggestActionType;
import vn.com.zalopay.wallet.business.entity.user.UserInfo;
import vn.com.zalopay.wallet.helper.FontHelper;
import vn.com.zalopay.wallet.helper.FormatHelper;
import vn.com.zalopay.wallet.paymentinfo.AbstractOrder;
import vn.com.zalopay.wallet.repository.ResourceManager;
import vn.com.zalopay.wallet.ui.IPresenter;
import vn.com.zalopay.wallet.ui.channel.ChannelActivity;
import vn.com.zalopay.wallet.ui.channel.RenderFragment;

import static vn.com.zalopay.wallet.helper.FontHelper.applyFont;
import static vn.com.zalopay.wallet.helper.RenderHelper.genDynamicItemDetail;

/*
 * Created by chucvv on 7/23/17.
 */

public abstract class AbstractPaymentFragment<T extends IPresenter> extends RenderFragment<T> {

    public abstract void enablePaymentButton();

    public abstract void setTitle(String pTitle);

    private void updateToolBar() {
        ((ChannelActivity) getActivity()).hideDisplayHome();
        ((ChannelActivity) getActivity()).centerTitle();
    }

    private void changeSubmitButtonBackground(AbstractOrder order) {
        int appID = (order != null) ? (int) order.appid : 0;
        Button close_btn = (Button) findViewById(R.id.zpsdk_btn_submit);
        switch (appID) {
            case 12:
                if (close_btn != null) {
                    close_btn.setTextColor(ContextCompat.getColor(getContext(), R.color.button_text_color));
                    close_btn.setBackgroundResource(R.drawable.bg_btn_blue_border_selector);
                    return;
                }
                break;
            default:
                if (close_btn != null) {
                    close_btn.setTextColor(ContextCompat.getColor(getContext(), R.color.text_color_grey));
                    close_btn.setBackgroundResource(R.drawable.bg_btn_light_blue_border_selector);
                }
        }
    }

    public void renderDynamicItemDetail(View viewContainer, List<NameValuePair> nameValuePairList) throws Exception {
        List<View> views = genDynamicItemDetail(getContext(), nameValuePairList);
        boolean hasView = views != null && views.size() > 0;
        LinearLayout stubView = (LinearLayout) viewContainer.findViewById(R.id.item_detail_linearlayout);
        if (hasView && stubView != null) {
            for (View view : views) {
                stubView.addView(view);
            }
        }
        setVisible(R.id.item_detail_linearlayout, hasView);
    }

    private void renderTransDetail(View viewContainer, boolean isLink, String pTransID, AbstractOrder order, String appName, boolean visibleTrans) throws Exception {
        //service name
        boolean hasAppName = !TextUtils.isEmpty(appName);
        TextView appname_txt = (TextView) viewContainer.findViewById(R.id.appname_txt);
        if (hasAppName) {
            appname_txt.setText(appName);
        }
        appname_txt.setVisibility(hasAppName ? View.VISIBLE : View.GONE);
        //trans id
        boolean hasTransId = !TextUtils.isEmpty(pTransID) && Long.parseLong(pTransID) > 0;
        TextView transaction_id_txt = (TextView) viewContainer.findViewById(R.id.transaction_id_txt);
        if (hasTransId) {
            transaction_id_txt.setText(FormatHelper.formatTransID(pTransID));
        } else {
            transaction_id_txt.setText(getResources().getString(R.string.sdk_no_transid_mess));
        }
        View sdk_trans_id_relativelayout = viewContainer.findViewById(R.id.sdk_trans_id_relativelayout);
        sdk_trans_id_relativelayout.setVisibility(visibleTrans ? View.VISIBLE : View.GONE);//hide trans id if unlink account
        //trans time
        Long paymentTime = order != null ? order.apptime : new Date().getTime();
        TextView transaction_time_txt = (TextView) viewContainer.findViewById(R.id.transaction_time_txt);
        transaction_time_txt.setText(SdkUtils.convertDateTime(paymentTime));
        //trans fee
        String transFee = order != null && order.fee > 0 ?
                CurrencyUtil.formatCurrency(order.fee) :
                getResources().getString(R.string.sdk_order_fee_free);
        TextView order_fee_txt = (TextView) viewContainer.findViewById(R.id.order_fee_txt);
        order_fee_txt.setText(transFee);
        //render item detail dynamic
        if (order != null) {
            List<NameValuePair> items = order.parseItems();
            renderDynamicItemDetail(viewContainer, items);
        }

        if (isLink) {
            setVisible(R.id.appname_result_relativelayout, false);
            setVisible(R.id.fee_relativelayout, false);
        }
    }

    public void renderSuccess(boolean isLink, String pTransID, UserInfo userInfo, AbstractOrder order, String appName, String descLinkAccount, boolean hideAmount,
                              boolean isTransfer, UserInfo destinationUser, String pToolbarTitle) {
        //transaction amount
        boolean hasAmount = order != null && order.amount_total > 0;
        if (hasAmount) {
            applyFont(findViewById(R.id.success_order_amount_total_txt), GlobalData.getStringResource(RS.string.sdk_font_medium));
            setTextHtml(R.id.success_order_amount_total_txt, CurrencyUtil.formatCurrency(order.amount_total, false));
            ((TextView) findViewById(R.id.success_order_amount_total_txt)).setTextSize(getResources().getDimension(FontHelper.getFontSizeAmount(order.amount_total)));
        }
        if (!hasAmount || hideAmount) {
            setVisible(R.id.success_order_amount_total_linearlayout, false);
        }
        //desc
        String desc = descLinkAccount;
        if (TextUtils.isEmpty(desc)) {
            desc = order != null ? order.description : null;
        }
        boolean hasDesc = !TextUtils.isEmpty(desc);
        if (hasDesc) {
            setText(R.id.description_txt, desc);
        }
        setVisible(R.id.description_txt, hasDesc);
        //show 2 user avatar in tranfer money
        if (isTransfer) {
            //prevent capture screen
            getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
            setVisible(R.id.money_tranfer_useravatar_linearlayout, true);
            if (destinationUser != null) {
                loadIntoView(R.id.img_avatarTo, destinationUser.avatar);
            }
            if (userInfo != null && !TextUtils.isEmpty(userInfo.avatar)) {
                loadIntoView(R.id.img_avatarFrom, userInfo.avatar);
            }
            loadIntoView(R.id.arrow_imageview, ResourceManager.getAbsoluteImagePath(RS.drawable.ic_arrow));
        }
        //inflat trans detail layout
        ViewStub success_trans_detail_stub = (ViewStub) findViewById(R.id.success_trans_detail_stub);
        if (success_trans_detail_stub != null) {
            View trans_detail_view = success_trans_detail_stub.inflate();
            try {
                renderTransDetail(trans_detail_view, isLink, pTransID, order, appName, true);
            } catch (Exception e) {
                Timber.d(e);
            }
        }
        changeSubmitButtonBackground(order);
        updateToolBar();
        enablePaymentButton();
        new Handler().postDelayed(() -> setTitle(pToolbarTitle), 300);
    }

    public void renderFail(boolean isLink, String pMessage, String pTransID, AbstractOrder order, String appName, StatusResponse statusResponse,
                           boolean visibleTrans, String pToolBarTitle) {
        boolean hasTransFailMessage = !TextUtils.isEmpty(pMessage);
        if (hasTransFailMessage) {
            setText(R.id.sdk_trans_fail_reason_message_textview, pMessage);
        }
        setVisible(R.id.sdk_trans_fail_reason_message_textview, hasTransFailMessage);
        //inflate trans detail layout
        ViewStub fail_trans_detail_stub = (ViewStub) findViewById(R.id.fail_trans_detail_stub);
        if (fail_trans_detail_stub != null) {
            View trans_detail_view = fail_trans_detail_stub.inflate();
            try {
                renderTransDetail(trans_detail_view, isLink, pTransID, order, appName, visibleTrans);
            } catch (Exception e) {
                Log.e(this, e);
            }
        }
        // The inform text would be set from server
        if (statusResponse != null) {
            //message action
            boolean hasSuggestActionMessage = !TextUtils.isEmpty(statusResponse.suggestmessage);
            if (hasSuggestActionMessage) {
                setText(R.id.sdk_sugguest_action_message_textview, statusResponse.suggestmessage);
            }
            setVisible(R.id.sdk_sugguest_action_message_textview, hasSuggestActionMessage);
            /*if (statusResponse.hasSuggestAction()) {
                setLayoutBasedOnSuggestActions(statusResponse.suggestaction);
            }*/
        }
        changeSubmitButtonBackground(order);
        updateToolBar();
        enablePaymentButton();
        new Handler().postDelayed(() -> setTitle(pToolBarTitle), 300);
    }

    private void setLayoutBasedOnSuggestActions(int[] suggestActions) {
        // Define view to set view position based on suggest action from server response
        View rlUpdateInfo = findViewById(R.id.zpw_payment_fail_rl_update_info);
        View rlSupport = findViewById(R.id.zpw_payment_fail_rl_support);

        RelativeLayout.LayoutParams pUpdateInfo = (RelativeLayout.LayoutParams) rlUpdateInfo.getLayoutParams();
        RelativeLayout.LayoutParams pSupport = (RelativeLayout.LayoutParams) rlSupport.getLayoutParams();

        if (Arrays.equals(ESuggestActionType.UPDATE_INFO_DISPLAY.getValue(), suggestActions)) {
            setVisible(R.id.zpw_payment_fail_rl_support, false);
            setVisible(R.id.zpw_payment_fail_rl_update_info, true);
        } else if (Arrays.equals(ESuggestActionType.SUPPORT_DISPLAY.getValue(), suggestActions)) {
            setVisible(R.id.zpw_payment_fail_rl_support, true);
            setVisible(R.id.zpw_payment_fail_rl_update_info, false);
        } else if (Arrays.equals(ESuggestActionType.UPDATE_INFO_ABOVE.getValue(), suggestActions)) {
            setVisible(R.id.zpw_payment_fail_rl_update_info, true);
            setVisible(R.id.zpw_payment_fail_rl_support, true);
            pSupport.addRule(RelativeLayout.BELOW, rlUpdateInfo.getId());
            rlSupport.setLayoutParams(pSupport);
        } else if (Arrays.equals(ESuggestActionType.SUPPORT_ABOVE.getValue(), suggestActions)) {
            setVisible(R.id.zpw_payment_fail_rl_support, true);
            setVisible(R.id.zpw_payment_fail_rl_update_info, true);
            pUpdateInfo.addRule(RelativeLayout.BELOW, rlSupport.getId());
            rlUpdateInfo.setLayoutParams(pUpdateInfo);
        }
    }
}
