package vn.com.zalopay.wallet.view.custom.cardview.pager;

import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TextInputLayout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;

import timber.log.Timber;
import vn.com.zalopay.wallet.R;
import vn.com.zalopay.wallet.business.data.GlobalData;
import vn.com.zalopay.wallet.business.data.Log;
import vn.com.zalopay.wallet.business.data.RS;
import vn.com.zalopay.wallet.card.BankDetector;
import vn.com.zalopay.wallet.card.CreditCardDetector;
import vn.com.zalopay.wallet.repository.ResourceManager;
import vn.com.zalopay.wallet.view.custom.VPaymentDrawableEditText;
import vn.com.zalopay.wallet.view.custom.VPaymentEditText;
import vn.com.zalopay.wallet.workflow.ui.CardGuiProcessor;

/**
 * Card number fragment
 */
public class CardNumberFragment extends CreditCardFragment {
    protected VPaymentDrawableEditText mCardNumberView;

    protected ImageView mImageViewQuestion;

    protected View mVirtualView;

    protected FrameLayout mRootView;

    public CardNumberFragment() {
    }

    /***
     * show icon support bank ?
     * user click into this icon that will show support card list dialog
     */
    public void showQuestionIcon() {
        if (mImageViewQuestion == null) {
            mImageViewQuestion = new ImageView(GlobalData.getAppContext());

            Bitmap bmBankSupportHelp = ResourceManager.getImage(RS.drawable.ic_bank_support_help);

            if (bmBankSupportHelp == null) {
                Timber.d("===bmBankSupportHelp=null===");
            }

            if (bmBankSupportHelp != null) {
                mImageViewQuestion.setImageBitmap(bmBankSupportHelp);
                try {
                    if (getGuiProcessor() != null) {
                        mImageViewQuestion.setOnClickListener(getGuiProcessor().getOnQuestionIconClick());
                    }
                } catch (Exception e) {
                    Timber.w(e);
                }
            }
        }

        if (mImageViewQuestion != null && mCardNumberView != null && mRootView != null) {
            //calculate the width of text
            String text = GlobalData.getAppContext().getResources().getString(R.string.sdk_card_not_support);

            Rect bounds = new Rect();
            Paint textPaint = mCardNumberView.getPaint();
            textPaint.getTextBounds(text, 0, text.length(), bounds);

            final int width = bounds.width();

            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.WRAP_CONTENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT
            );

            params.leftMargin = width / 4 * 3 + 20;
            params.topMargin = (int) GlobalData.getAppContext().getResources().getDimension(R.dimen.margin_small);

            mImageViewQuestion.setLayoutParams(params);

            mRootView.removeView(mImageViewQuestion);
            mRootView.addView(mImageViewQuestion);

            //add virtual view to extend region of touched
            if (mVirtualView == null) {
                mVirtualView = new View(GlobalData.getAppContext());
                try {
                    mVirtualView.setOnClickListener(getGuiProcessor().getOnQuestionIconClick());
                } catch (Exception e) {
                    Timber.d(e);
                }

            }
            // get height when iconQuestion loadconplete
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    try {
                        FrameLayout.LayoutParams virtualParams = new FrameLayout.LayoutParams(width + mImageViewQuestion.getWidth(), mImageViewQuestion.getHeight() + 20);
                        mVirtualView.setLayoutParams(virtualParams);
                        mRootView.removeView(mVirtualView);
                        mRootView.addView(mVirtualView);
                        mRootView.requestLayout();
                    } catch (Exception e) {
                        Log.e(this, e);
                    }
                }
            }, 100);
        }
    }

    public void hideQuestionIcon() {
        if (mRootView != null && mImageViewQuestion != null) {
            mRootView.removeView(mImageViewQuestion);

            mRootView.removeView(mVirtualView);
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup group, Bundle state) {

        View v = inflater.inflate(R.layout.lyt_card_number, group, false);

        mCardNumberView = (VPaymentDrawableEditText) v.findViewById(R.id.edittext_localcard_number);

        if (mCardNumberView != null) {
            mCardNumberView.setImeOptions(EditorInfo.IME_ACTION_NEXT | EditorInfo.IME_FLAG_NO_EXTRACT_UI);

            onChangeTextHintColor(mCardNumberView);
        }

        mRootView = (FrameLayout) v.findViewById(R.id.framelayoutContainer);

        try {
            CardGuiProcessor cardGuiProcessor = getGuiProcessor();
            if (cardGuiProcessor == null) {
                return v;
            }
            if (mCardNumberView == null) {
                return v;
            }
            mCardNumberView.addTextChangedListener(cardGuiProcessor.getCardDetectionTextWatcher());
            mCardNumberView.setOnEditorActionListener(cardGuiProcessor.getEditorActionListener());
            mCardNumberView.setOnFocusChangeListener(cardGuiProcessor.getOnFocusChangeListener());

            //user touch on edittext,show keyboard
            if (mCardNumberView instanceof VPaymentEditText && mCardNumberView.getTextInputLayout() instanceof TextInputLayout) {
                (mCardNumberView.getTextInputLayout()).setOnClickListener(cardGuiProcessor.getClickOnEditTextListener());
            } else {
                mCardNumberView.setOnClickListener(cardGuiProcessor.getClickOnEditTextListener());
            }
        } catch (Exception e) {
            Timber.w(e);
        }

        return v;
    }

    @Override
    public void clearText() {
        if (mCardNumberView != null) {
            mCardNumberView.setText(null);
        }
    }

    @Override
    public EditText getEditText() {
        return mCardNumberView;
    }

    @Override
    public void setHint(String pMessage) {
        if (mCardNumberView != null) {
            hideQuestionIcon();

            setHint(mCardNumberView, pMessage);
        }
    }

    @Override
    public void clearError() {
        if (mCardNumberView != null) {
            hideQuestionIcon();

            setHint(mCardNumberView, null);
        }
    }

    /***
     * get hint error
     *
     * @return
     */
    @Override
    public String getError() {
        String errorMess = null;

        if (mCardNumberView instanceof VPaymentEditText && mCardNumberView.getTextInputLayout() instanceof TextInputLayout) {
            errorMess = (String) (mCardNumberView.getTextInputLayout()).getHint();

            if (!TextUtils.isEmpty(errorMess) && errorMess.equalsIgnoreCase((mCardNumberView.getTextInputLayout()).getTag().toString())) {
                errorMess = null;
            }
        }

        //this is not error hint,it is detected bank name
        String warning = null;
        try {
            warning = getPaymentAdapter().getGuiProcessor().warningCardExist();
        } catch (Exception e) {
            Log.e(this, e);
        }
        if (!TextUtils.isEmpty(errorMess) && !errorMess.equalsIgnoreCase(warning)) {
            try {
                if ((getPaymentAdapter().isATMFlow() && (BankDetector.getInstance().detected()
                        || CreditCardDetector.getInstance().detected()))) {
                    errorMess = null;

                } else if (getPaymentAdapter().isCCFlow() && (CreditCardDetector.getInstance().detected()
                        || BankDetector.getInstance().detected())) {
                    errorMess = null;
                }

            } catch (Exception e) {
                Log.e(this, e);

                errorMess = null;
            }
        }

        return errorMess;
    }

    @Override
    public void setError(String pMessage) {
        if (mCardNumberView != null) {
            setErrorHint(mCardNumberView, pMessage);
        }
    }
}
