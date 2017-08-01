package vn.com.zalopay.wallet.ui.channellist;

import com.zalopay.ui.widget.dialog.listener.ZPWOnEventConfirmDialogListener;

import java.util.List;

import vn.com.vng.zalopay.data.util.NameValuePair;
import vn.com.zalopay.wallet.business.entity.base.StatusResponse;
import vn.com.zalopay.wallet.business.entity.user.UserInfo;
import vn.com.zalopay.wallet.paymentinfo.AbstractOrder;
import vn.com.zalopay.wallet.ui.IContract;

/**
 * Created by chucvv on 6/12/17.
 */

public interface ChannelListContract extends IContract {
    interface IView extends IContract {
        void setTitle(String title);

        void updateDefaultTitle();

        void renderVoucher();

        void renderActiveVoucher(String voucherCode, double discountAmount);

        void renderAppInfo(String appName);

        void renderOrderInfo(AbstractOrder order);

        void renderDynamicItemDetail(List<NameValuePair> nameValuePair);

        void onBindingChannel(ChannelListAdapter pChannelAdapter);

        void showAppInfoNotFoundDialog();

        void showWarningLinkCardBeforeWithdraw();

        void showSupportBankVersionDialog(String pMessage);

        void disableConfirmButton();

        void showQuitConfirm();

        void showConfirmDeleteVoucherDialog(ZPWOnEventConfirmDialogListener pListener);

        void scrollToPos(int position);

        void enablePaymentButton(int buttonTextId, int bgResourceId);

        void switchToResultScreen(StatusResponse pResponse, boolean pShouldShowFingerPrintToast) throws Exception;

        void setVoucherError(String error);

        void hideVoucherCodePopup();

        void renderOrderAmount(double order_total_amount);

        void renderOrderFee(double order_fee);

        ChannelListAdapter initChannelListAdapter(long amount, UserInfo userInfo, int userLevel, int transtype);
    }
}
