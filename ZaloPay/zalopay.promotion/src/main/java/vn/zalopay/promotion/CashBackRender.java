package vn.zalopay.promotion;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import timber.log.Timber;
import vn.com.zalopay.utility.CurrencyUtil;
import vn.zalopay.promotion.model.CashBackEvent;
import vn.zalopay.promotion.model.PromotionEvent;

public class CashBackRender extends PromotionRender {
    public CashBackRender(IBuilder pBuilder) {
        super(pBuilder);
    }

    public static IBuilder getBuilder() {
        return new CashBackBuilder();
    }

    @Override
    public void render(final Context pContext) {
        if (mBuilder == null) {
            return;
        }
        final PromotionEvent promotionEvent = mBuilder.getPromotion();
        if (!(promotionEvent instanceof CashBackEvent)) {
            return;
        }
        View view = mBuilder.getView();
        if (view == null) {
            return;
        }
        try {
            renderView(pContext, view, promotionEvent);
            TextView tvCashBackAmount = (TextView) view.findViewById(R.id.promotion_cash_back_tv_amount);
            tvCashBackAmount.setText(CurrencyUtil.formatCurrency(((CashBackEvent) promotionEvent).amount, false));
        } catch (Exception e) {
            Timber.w(e, "Exception render cash back view");
        }
    }
}
