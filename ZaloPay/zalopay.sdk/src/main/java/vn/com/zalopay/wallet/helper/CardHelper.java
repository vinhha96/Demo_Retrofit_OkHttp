package vn.com.zalopay.wallet.helper;

import android.text.TextUtils;

import vn.com.zalopay.wallet.BuildConfig;
import vn.com.zalopay.wallet.business.channel.creditcard.CreditCardCheck;
import vn.com.zalopay.wallet.business.channel.localbank.BankCardCheck;
import vn.com.zalopay.wallet.business.data.GlobalData;
import vn.com.zalopay.wallet.business.data.RS;
import vn.com.zalopay.wallet.business.entity.base.DMapCardResult;
import vn.com.zalopay.wallet.business.entity.gatewayinfo.MapCard;
import vn.com.zalopay.wallet.constants.CardTypeUtils;

public class CardHelper {

    public static DMapCardResult cast(MapCard saveCardInfo) {
        DMapCardResult mapCardResult = new DMapCardResult();
        mapCardResult.setLast4Number(saveCardInfo.last4cardno);
        String bankName = null;
        //this is atm card
        if (!BuildConfig.CC_CODE.equals(saveCardInfo.bankcode)) {
            mapCardResult.setCardLogo(ChannelHelper.makeCardIconNameFromBankCode(saveCardInfo.bankcode));
            bankName = BankCardCheck.getInstance().getShortBankName();
            if (!TextUtils.isEmpty(bankName)) {
                bankName = String.format(GlobalData.getStringResource(RS.string.sdk_card_generic_label), bankName);
            }
        }
        //cc
        else {
            CreditCardCheck cardCheck = CreditCardCheck.getInstance();
            cardCheck.detectOnSync(saveCardInfo.first6cardno);
            if (cardCheck.isDetected()) {
                String cardType = CardTypeUtils.fromBankCode(cardCheck.getCodeBankForVerify());
                mapCardResult.setCardLogo(ChannelHelper.makeCardIconNameFromBankCode(cardType));
                bankName = String.format(GlobalData.getStringResource(RS.string.sdk_creditcard_label), cardCheck.getBankName());
            }
        }
        if (TextUtils.isEmpty(bankName)) {
            bankName = GlobalData.getStringResource(RS.string.sdk_card_default_label);
        }
        mapCardResult.setBankName(bankName);
        return mapCardResult;
    }
}
