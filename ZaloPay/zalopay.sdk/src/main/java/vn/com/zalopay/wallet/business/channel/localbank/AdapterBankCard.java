package vn.com.zalopay.wallet.business.channel.localbank;

import android.content.Intent;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

import vn.com.zalopay.analytics.ZPPaymentSteps;
import vn.com.zalopay.wallet.R;
import vn.com.zalopay.wallet.business.channel.base.AdapterBase;
import vn.com.zalopay.wallet.business.dao.SharedPreferencesManager;
import vn.com.zalopay.wallet.business.data.Constants;
import vn.com.zalopay.wallet.business.data.GlobalData;
import vn.com.zalopay.wallet.business.data.RS;
import vn.com.zalopay.wallet.business.entity.atm.DAtmScriptOutput;
import vn.com.zalopay.wallet.business.entity.base.BaseResponse;
import vn.com.zalopay.wallet.business.entity.enumeration.ECardChannelType;
import vn.com.zalopay.wallet.business.entity.enumeration.EEventType;
import vn.com.zalopay.wallet.business.entity.enumeration.EPaymentReturnCode;
import vn.com.zalopay.wallet.business.entity.gatewayinfo.DMappedCard;
import vn.com.zalopay.wallet.business.entity.gatewayinfo.MiniPmcTransType;
import vn.com.zalopay.wallet.business.entity.staticconfig.atm.DOtpReceiverPattern;
import vn.com.zalopay.wallet.business.transaction.SDKTransactionAdapter;
import vn.com.zalopay.wallet.business.webview.base.PaymentWebViewClient;
import vn.com.zalopay.wallet.helper.PaymentStatusHelper;
import vn.com.zalopay.wallet.listener.ZPWOnEventConfirmDialogListener;
import vn.com.zalopay.wallet.listener.ZPWOnEventDialogListener;
import vn.com.zalopay.wallet.tracker.ZPAnalyticsTrackerWrapper;
import vn.com.zalopay.wallet.utils.GsonUtils;
import vn.com.zalopay.wallet.utils.Log;
import vn.com.zalopay.wallet.utils.PaymentUtils;
import vn.com.zalopay.wallet.view.component.activity.MapListSelectionActivity;
import vn.com.zalopay.wallet.view.component.activity.PaymentChannelActivity;

public class AdapterBankCard extends AdapterBase {
    private PaymentWebViewClient mWebViewProcessor = null;

    private int numberRetryCaptcha = 0;

    public AdapterBankCard(PaymentChannelActivity pOwnerActivity, MiniPmcTransType pMiniPmcTransType) throws Exception {
        super(pOwnerActivity, pMiniPmcTransType);
        mLayoutId = SCREEN_ATM;
        mPageCode = (GlobalData.isMapCardChannel() || GlobalData.isMapBankAccountChannel()) ? PAGE_CONFIRM : SCREEN_ATM;
        GlobalData.cardChannelType = ECardChannelType.ATM;

    }

    @Override
    public boolean needReloadPmcConfig(String pBankCode) {
        return mMiniPmcTransType == null || (mMiniPmcTransType != null && !mMiniPmcTransType.bankcode.equals(pBankCode));
    }

    @Override
    public MiniPmcTransType getConfig() {
        String bankCode = BankCardCheck.getInstance().getDetectBankCode();
        return getConfig(bankCode);
    }

    @Override
    public MiniPmcTransType getConfig(String pBankCode) {
        try {
            if (needReloadPmcConfig(pBankCode)) {
                Log.d(this, "start reload pmc transtype "+ pBankCode);
                mMiniPmcTransType = GsonUtils.fromJsonString(SharedPreferencesManager.getInstance().getATMChannelConfig(pBankCode), MiniPmcTransType.class);
                Log.d(this, "new pmc transype", mMiniPmcTransType);
            }
        } catch (Exception e) {
            Log.e(this, e);
        }
        return mMiniPmcTransType;
    }

    @Override
    public void init() {
        try {
            this.mGuiProcessor = new BankCardGuiProcessor(this);
            if (getGuiProcessor() != null && GlobalData.isChannelHasInputCard()) {
                getGuiProcessor().initPager();
            }

        } catch (Exception e) {
            Log.e(this, e);
            terminate(GlobalData.getStringResource(RS.string.zpw_string_error_layout), true);
            return;
        }
        showFee();
    }

    @Override
    public void initWebView(String pUrl) {
        mWebViewProcessor = PaymentWebViewClient.createPaymentWebViewClientByBank(this);
        mWebViewProcessor.start(pUrl);
    }

    @Override
    public void detectCard(String pCardNumber) {
        getGuiProcessor().getCreditCardFinder().reset();
        getGuiProcessor().getCardFinder().detectOnOtherThread(pCardNumber, getGuiProcessor().getOnDetectCardListener());
    }

    public PaymentWebViewClient getWebViewProcessor() {
        return mWebViewProcessor;
    }

    protected int getDefaultChannelId() {
        return Integer.parseInt(GlobalData.getStringResource(RS.string.zingpaysdk_conf_gwinfo_channel_atm));
    }

    @Override
    public int getChannelID() {
        int channelId = super.getChannelID();
        return channelId != -1 ? channelId : getDefaultChannelId();
    }

    @Override
    public void autoFillOtp(String pSender, String pOtp) {
        Log.d(this, "sender " + pSender + " otp " + pOtp);
        if (!((BankCardGuiProcessor) getGuiProcessor()).isBankOtpPhase()) {
            Log.d(this, "user is not in otp phase, skip auto fill otp");
            return;
        }

        try {
            List<DOtpReceiverPattern> patternList = getGuiProcessor().getCardFinder().getOtpReceiverPatternList();
            if (patternList != null && patternList.size() > 0) {
                for (DOtpReceiverPattern otpReceiverPattern : patternList) {
                    if (!TextUtils.isEmpty(otpReceiverPattern.sender) && otpReceiverPattern.sender.equalsIgnoreCase(pSender)) {
                        int start = 0;
                        pOtp = pOtp.trim();
                        //read the begining of sms content
                        if (otpReceiverPattern.begin) {
                            start = otpReceiverPattern.start;
                        }
                        //read otp from the ending of content
                        else {
                            start = pOtp.length() - otpReceiverPattern.length - otpReceiverPattern.start;
                        }

                        String otp = pOtp.substring(start, start + otpReceiverPattern.length);

                        /***
                         * vietinbank has 2 type of sms
                         * 1. 6 number otp in the fist of content
                         * 2. 6 number otp in the last of content
                         * need extract splited otp by search space ' ' again
                         * then compare #validOtp and length otp in config
                         */
                        int index = -1;
                        String validOtp = null;
                        if (otpReceiverPattern.begin) {
                            for (int i = otpReceiverPattern.start; i < pOtp.length(); i++) {
                                if (pOtp.charAt(i) == ' ') {
                                    index = i;
                                    break;
                                }
                            }
                            if (index != -1) {
                                validOtp = pOtp.substring(otpReceiverPattern.start, index);
                            }
                        } else {
                            for (int i = (pOtp.length() - otpReceiverPattern.start) - 1; i >= 0; i--) {
                                if (pOtp.charAt(i) == ' ') {
                                    index = i;
                                    break;
                                }
                            }
                            if (index != -1) {
                                validOtp = pOtp.substring(index, (pOtp.length() - otpReceiverPattern.start));
                            }
                        }
                        if (!TextUtils.isEmpty(validOtp)) {
                            validOtp = validOtp.trim();
                        }
                        //clear whitespace and - character
                        otp = PaymentUtils.clearOTP(validOtp);

                        Log.d(this, "otp after split by space " + validOtp);
                        //check it whether length match length of otp in config
                        if (!TextUtils.isEmpty(otp) && otp.length() != otpReceiverPattern.length) {
                            continue;
                        }

                        if ((!otpReceiverPattern.isdigit && TextUtils.isDigitsOnly(otp)) || (otpReceiverPattern.isdigit && !TextUtils.isDigitsOnly(otp))) {
                            continue;
                        }
                        if (otpReceiverPattern.bankcode.equalsIgnoreCase(GlobalData.getStringResource(RS.string.zpw_string_bankcode_bidv))) {
                            ((BankCardGuiProcessor) getGuiProcessor()).bidvAutoFillOtp(otp);
                        }

                        ((BankCardGuiProcessor) getGuiProcessor()).setOtp(otp);
                        getActivity().setView(R.id.txtOtpInstruction, false);
                        break;
                    }
                }

            }

        } catch (Exception e) {
            Log.e(this, e);
        }
    }

    @Override
    public Object onEvent(EEventType pEventType, Object... pAdditionParams) {
        try {
            Object base = super.onEvent(pEventType, pAdditionParams);

            if (pEventType == EEventType.ON_ATM_AUTHEN_PAYER_COMPLETE) {
                //check result authen, otp code is 17: wrong otp, other code terminate
                if (PaymentStatusHelper.isNeedToGetStatusAfterAuthenPayer(mResponseStatus) && !PaymentStatusHelper.isWrongOtpResponse(mResponseStatus)) {
                    getTransactionStatus(mTransactionID, false, GlobalData.getStringResource(RS.string.zingpaysdk_alert_get_status));
                }
                //retry otp
                else if (PaymentStatusHelper.isWrongOtpResponse(mResponseStatus)) {
                    processWrongOtp();
                } else if (mResponseStatus != null) {
                    showTransactionFailView(mResponseStatus.getMessage());
                    releaseClickSubmit();
                } else if (shouldCheckStatusAgain()) {
                    Log.d(this, "===continue get status because mResponseStatus=NULL after authen payer===");
                    getTransactionStatus(mTransactionID, false, GlobalData.getStringResource(RS.string.zingpaysdk_alert_get_status));
                } else {
                    showTransactionFailView(GlobalData.getStringResource(RS.string.zingpaysdk_alert_network_error));
                    releaseClickSubmit();
                }
            }
            //flow webview parse website
            else if (pEventType == EEventType.ON_FAIL) {
                //get status again if user input otp
                if (((BankCardGuiProcessor) getGuiProcessor()).isOtpWebProcessing()) {
                    getTransactionStatus(mTransactionID, false, null);
                } else {
                    String failMessage = null;
                    if (mResponseStatus != null) {
                        failMessage = mResponseStatus.getMessage();
                    }

                    if (!TextUtils.isEmpty(failMessage)) {
                        failMessage = GlobalData.getStringResource(RS.string.zpw_alert_networking_error_parse_website);
                    }
                    showTransactionFailView(failMessage);
                    showProgressBar(false, null);
                }

                //get website content and send to server
                getWebViewProcessor().getSiteContent();
            }
            //flow webview parse website
            else if (pEventType == EEventType.ON_PAYMENT_COMPLETED) {
                mOtpEndTime = System.currentTimeMillis();

                BaseResponse response = (BaseResponse) pAdditionParams[0];

                if (response.returncode == EPaymentReturnCode.ATM_VERIFY_OTP_SUCCESS.getValue()) {
                    getTransactionStatus(mTransactionID, false, GlobalData.getStringResource(RS.string.zingpaysdk_alert_get_status));
                } else {
                    showTransactionFailView(response.getMessage());
                }
            }
            //render webview flow
            else if (pEventType == EEventType.ON_REQUIRE_RENDER) {

                if (isFinalScreen()) {
                    Log.d(this, "EEventType.ON_REQUIRE_RENDER but in final screen now");
                    return null;
                }
                DAtmScriptOutput response = (DAtmScriptOutput) pAdditionParams[0];

                //hide pin view
                if (isRequirePinPharse()) {
                    getActivity().visiblePinView(false);
                }
                if (isBidvBankPayment() && !continueProcessForBidvBank(response.message)) {
                    if (isCaptchaStep()) {
                        showTransactionFailView(response.message);
                    }
                    return null;
                }
                // Reset captcha imediately
                if (!TextUtils.isEmpty(response.otpimg)) {
                    if (numberRetryCaptcha >= Constants.MAX_COUNT_RETRY_CAPTCHA) {
                        String message = response.message;
                        if (TextUtils.isEmpty(message)) {
                            message = GlobalData.getStringResource(RS.string.zpw_alert_captcha_vietcombank_update);
                        }
                        showTransactionFailView(message);
                        return null;
                    }

                    numberRetryCaptcha++;

                    ((BankCardGuiProcessor) getGuiProcessor()).setCaptchaImage(response.otpimg, response.otpimgsrc);
                }
                //vietcombank have 2 account on 1 card.
                if (response != null && !TextUtils.isEmpty(response.accountList)) {
                    ArrayList<String> accountList = new ArrayList<>();
                    try {
                        for (String accountName : response.accountList.split(Constants.COMMA)) {
                            if (!TextUtils.isEmpty(accountName))
                                accountList.add(accountName);
                        }

                    } catch (Exception ex) {
                        Log.e(this, ex);
                    }

                    if (accountList.size() <= 1) {
                        //CONTINUE HIT if this card only have 1 account
                        showProgressBar(true, GlobalData.getStringResource(RS.string.zingpaysdk_alert_processing_bank));
                        mWebViewProcessor.hit();
                    } else {
                        //SHOW SELECTION ACCOUNT LIST

                        mPageCode = PAGE_SELECTION_ACCOUNT_BANK;
                        getActivity().renderByResource();

                        ((BankCardGuiProcessor) getGuiProcessor()).showAccountList(accountList);

                        showProgressBar(false, null);

                        return null;
                    }
                }

                // re-render from web bank
                if (pAdditionParams.length > 1) {
                    mPageCode = PAGE_COVER_BANK_AUTHEN;
                    getActivity().renderByResource();

                    mPageCode = (String) pAdditionParams[1];

                    getActivity().renderByResource(response.staticView, response.dynamicView);
                    getGuiProcessor().checkEnableSubmitButton();

                }

                if (!response.isError()) {
                    if (!TextUtils.isEmpty(response.info)) {
                        showDialog(GlobalData.getStringResource(response.info));
                    }
                }
                //has an error on website(wrong captcha,otp)
                else {
                    if (response.message.equalsIgnoreCase(GlobalData.getStringResource(RS.string.zpw_alert_captcha_vietcombank))) {
                        response.message = GlobalData.getStringResource(RS.string.zpw_alert_captcha_vietcombank_update);
                    }

                    showDialogWithCallBack(response.message, GlobalData.getStringResource(RS.string.dialog_close_button), new ZPWOnEventDialogListener() {

                        @Override
                        public void onOKevent() {
                            if (((BankCardGuiProcessor) getGuiProcessor()).isCaptchaProcessing()) {
                                //reset otp and show keyboard again
                                ((BankCardGuiProcessor) getGuiProcessor()).resetCaptcha();
                                getGuiProcessor().showKeyBoardOnEditTextAndScroll(((BankCardGuiProcessor) getGuiProcessor()).getCaptchaEditText());
                            } else if (((BankCardGuiProcessor) getGuiProcessor()).isOtpWebProcessing()) {
                                //reset otp and show keyboard again
                                ((BankCardGuiProcessor) getGuiProcessor()).resetOtpWeb();
                                getGuiProcessor().showKeyBoardOnEditTextAndScroll(((BankCardGuiProcessor) getGuiProcessor()).getOtpWebEditText());
                            }
                        }
                    });
                }

                //update top info if this is tranfer money transaction
                getActivity().showConfirmView(true, true, getConfig());

                //set time process for otp and captcha to send log to server.
                if (((BankCardGuiProcessor) getGuiProcessor()).isOtpWebProcessing() && mOtpEndTime == 0) {
                    mOtpEndTime = System.currentTimeMillis();

                    getGuiProcessor().showKeyBoardOnEditTextAndScroll(((BankCardGuiProcessor) getGuiProcessor()).getOtpWebEditText());
                }

                if (((BankCardGuiProcessor) getGuiProcessor()).isCaptchaProcessing() && mCaptchaEndTime == 0) {
                    mCaptchaEndTime = System.currentTimeMillis();
                    //request permission read/view sms on android 6.0+
                    requestReadOtpPermission();

                    getGuiProcessor().showKeyBoardOnEditTextAndScroll(((BankCardGuiProcessor) getGuiProcessor()).getCaptchaEditText());
                }

                if (((BankCardGuiProcessor) getGuiProcessor()).isOtpWebProcessing()) {
                    getActivity().setView(R.id.txtOtpInstruction, true);

                    //testing broadcast otp viettinbak
                    /*
                    new Handler().postDelayed(new Runnable() {
						@Override
						public void run()
						{
							String sender = "VietinBank";
							String body = "Giao dich truc tuyen Viettinbank.... Mat khau: 4556104679";
							//send otp to channel activity
							Intent messageIntent = new Intent();
							messageIntent.setAction(Constants.FILTER_ACTION_BANK_SMS_RECEIVER);
							messageIntent.putExtra(Constants.BANK_SMS_RECEIVER_SENDER, sender);
							messageIntent.putExtra(Constants.BANK_SMS_RECEIVER_BODY,body);
							LocalBroadcastManager.getInstance(GlobalData.getAppContext()).sendBroadcast(messageIntent);
						}
					},5000);
					*/

                }

                showProgressBar(false, null);

                if (getActivity().getActivityRender() != null) {
                    getActivity().getActivityRender().renderKeyBoard();
                }
            }
        } catch (Exception ex) {
            Log.e(this, ex);

            showTransactionFailView(GlobalData.getStringResource(RS.string.zpw_alert_networking_error_check_status));

        }
        return null;
    }

    @Override
    public boolean isInputStep() {
        if (getPageName().equals(SCREEN_ATM))
            return true;

        return super.isInputStep();
    }

    @Override
    public boolean isCaptchaStep() {
        if (getGuiProcessor() instanceof BankCardGuiProcessor) {
            return ((BankCardGuiProcessor) getGuiProcessor()).isCaptchaProcessing();
        }
        return super.isCaptchaStep();
    }

    @Override
    public boolean isOtpStep() {
        if (getGuiProcessor() instanceof BankCardGuiProcessor) {
            return ((BankCardGuiProcessor) getGuiProcessor()).isOtpWebProcessing() || ((BankCardGuiProcessor) getGuiProcessor()).isOtpAuthenPayerProcessing();
        }
        return super.isOtpStep();
    }

    @Override
    public void moveToConfirmScreen(MiniPmcTransType pMiniPmcTransType) {
        try {
            super.moveToConfirmScreen(pMiniPmcTransType);

            showConfrimScreenForCardChannel(pMiniPmcTransType);

        } catch (Exception ex) {
            Log.e(this, ex);
        }

    }

    @Override
    public void onProcessPhrase() throws Exception {
        try {
            //authen payer atm
            if (isAuthenPayerPharse()) {

                showProgressBar(true, GlobalData.getStringResource(RS.string.zingpaysdk_alert_processing_otp));

                getActivity().processingOrder = true;

                SDKTransactionAdapter.shared().authenPayer(this, mTransactionID, ((BankCardGuiProcessor) getGuiProcessor()).getAuthenType().toString(), ((BankCardGuiProcessor) getGuiProcessor()).getAuthenValue());

                if (mOtpEndTime == 0)
                    mOtpBeginTime = System.currentTimeMillis();

                return;
            }

            //web flow
            if (((BankCardGuiProcessor) getGuiProcessor()).isCoverBankInProcess()) {
                if (!checkNetworkingAndShowRequest()) {
                    return;
                }

                showProgressBar(true, GlobalData.getStringResource(RS.string.zingpaysdk_alert_processing_bank));

                //the first time load captcha
                if (mCaptchaEndTime == 0) {
                    mCaptchaBeginTime = System.currentTimeMillis();
                    // TrackApptransidEvent confirm stage
                    ZPAnalyticsTrackerWrapper.getInstance().track(ZPPaymentSteps.OrderStep_WebInfoConfirm, ZPPaymentSteps.OrderStepResult_None, getChannelID());
                }

                //the first time load otp
                if (mOtpEndTime == 0) {
                    mOtpBeginTime = System.currentTimeMillis();
                }

                mWebViewProcessor.hit();

                return;
            }

            if (!GlobalData.isMapCardChannel() && !GlobalData.isMapBankAccountChannel()) {
                getGuiProcessor().populateCard();
                tranferPaymentCardToMapCard();
            }

            startSubmitTransaction();

        } catch (Exception e) {
            throw e;
        }
    }

    public boolean hasBidvBankInMapCardList(String pCardNumber) {
        try {
            if (TextUtils.isEmpty(pCardNumber) || pCardNumber.length() < 6) {
                Log.d(this, "===hasBidvBankInMapCardList()===pCardNumber.length() < 6====" + pCardNumber);
                return false;
            }

            List<DMappedCard> mappedCardList = SharedPreferencesManager.getInstance().getMapCardList(GlobalData.getPaymentInfo().userInfo.zaloPayUserId);

            DMappedCard bidvCard = new DMappedCard();
            bidvCard.first6cardno = pCardNumber.substring(0, 6);
            bidvCard.last4cardno = pCardNumber.substring(pCardNumber.length() - 4, pCardNumber.length());

            return mappedCardList != null && mappedCardList.contains(bidvCard);
        } catch (Exception e) {
            Log.e(this, e);
        }
        return false;
    }

    public boolean hasBidvBankInMapCardList() {
        try {
            List<DMappedCard> mappedCardList = SharedPreferencesManager.getInstance().getMapCardList(GlobalData.getPaymentInfo().userInfo.zaloPayUserId);

            if (mappedCardList != null && mappedCardList.size() > 0) {
                for (DMappedCard mappedCard : mappedCardList) {
                    if (mappedCard.bankcode.equalsIgnoreCase(GlobalData.getStringResource(RS.string.zpw_string_bankcode_bidv))) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            Log.e(this, e);
        }
        return false;
    }

    public boolean preventPaymentBidvCard(String pBankCode, String pCardNumber) {

        //have some card bidv in map card list and have this card
        if ((hasBidvBankInMapCardList() && getGuiProcessor().isCardLengthMatchIdentifier(pCardNumber) && hasBidvBankInMapCardList(pCardNumber))) {

            MapListSelectionActivity.setCloseDialogListener(getGuiProcessor().getCloseDialogListener());
            Intent intentBankList = new Intent(getActivity(), MapListSelectionActivity.class);
            intentBankList.putExtra(MapListSelectionActivity.BANKCODE_EXTRA, GlobalData.getStringResource(RS.string.zpw_string_bankcode_bidv));
            intentBankList.putExtra(MapListSelectionActivity.CARDNUMBER_EXTRA, getGuiProcessor().getCardNumber());
            intentBankList.putExtra(MapListSelectionActivity.NOTICE_CONTENT_EXTRA, GlobalData.getStringResource(RS.string.zpw_warning_bidv_select_linkcard_payment));
            intentBankList.putExtra(MapListSelectionActivity.BUTTON_LEFT_TEXT_EXTRA, GlobalData.getStringResource(RS.string.dialog_retry_input_card_button));
            getActivity().startActivity(intentBankList);
            return true;
        }
        //have some card bidv in map card list and but don't have this card
        if (hasBidvBankInMapCardList() && getGuiProcessor().isCardLengthMatchIdentifier(pCardNumber) && !hasBidvBankInMapCardList(pCardNumber)) {
            getActivity().showConfirmDialog(new ZPWOnEventConfirmDialogListener() {
                                                @Override
                                                public void onCancelEvent() {
                                                    getGuiProcessor().clearCardNumberAndShowKeyBoard();
                                                }

                                                @Override
                                                public void onOKevent() {
                                                    needLinkCardBeforePayment(pBankCode);
                                                }
                                            }, GlobalData.getStringResource(RS.string.zpw_warning_bidv_linkcard_before_payment),
                    GlobalData.getStringResource(RS.string.dialog_linkcard_button), GlobalData.getStringResource(RS.string.dialog_retry_input_card_button));

            return true;
        }
        //have no any card in map card list
        if (!hasBidvBankInMapCardList()) {
            getActivity().showConfirmDialog(new ZPWOnEventConfirmDialogListener() {
                                                @Override
                                                public void onCancelEvent() {
                                                    getGuiProcessor().clearCardNumberAndShowKeyBoard();
                                                }

                                                @Override
                                                public void onOKevent() {
                                                    needLinkCardBeforePayment(pBankCode);
                                                }
                                            }, GlobalData.getStringResource(RS.string.zpw_warning_bidv_linkcard_before_payment),
                    GlobalData.getStringResource(RS.string.dialog_linkcard_button), GlobalData.getStringResource(RS.string.dialog_retry_input_card_button));
            return true;
        }
        return false;
    }

    public boolean isBidvBankPayment() {
        BankCardCheck atmCardCheck = getGuiProcessor().getBankCardFinder();
        if (atmCardCheck != null && atmCardCheck.isDetected() && atmCardCheck.getDetectBankCode().equalsIgnoreCase(GlobalData.getStringResource(RS.string.zpw_string_bankcode_bidv))) {
            return true;
        }
        return false;
    }

    protected boolean continueProcessForBidvBank(String pMessage) {
        boolean isContinue = false;
        Log.d(this, "===continueProcessForBidvBank===pMessage=" + pMessage);

        if (TextUtils.isEmpty(pMessage)) {
            isContinue = true;
        }
        if (!TextUtils.isEmpty(pMessage) &&
                (pMessage.contains(GlobalData.getStringResource(RS.string.zpw_error_message_bidv_website_wrong_captcha))
                        || pMessage.equals(GlobalData.getStringResource(RS.string.zpw_error_message_bidv_website_wrong_password)))) {
            isContinue = true;
        }
        Log.d(this, "===continueProcessForBidvBank===isContinue=" + isContinue);
        return isContinue;
    }
}
