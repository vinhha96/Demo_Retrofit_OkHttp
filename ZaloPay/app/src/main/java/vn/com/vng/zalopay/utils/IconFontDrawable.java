package vn.com.vng.zalopay.utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.Log;

import com.zalopay.ui.widget.iconfont.IconFontHelper;
import com.zalopay.ui.widget.iconfont.IconFontInfo;
import com.zalopay.ui.widget.util.FontHelper;

import timber.log.Timber;

/**
 * Created by khattn on 2/24/17.
 */

public class IconFontDrawable extends Drawable {

    private final Context mContext;
    private String mText;
    private TextPaint mPaint;
    private int mSize = -1;

    public IconFontDrawable(Context context) {
        this.mContext = context;
        mPaint = new TextPaint();
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setTextAlign(Paint.Align.CENTER);
        mPaint.setUnderlineText(false);
        mPaint.setColor(Color.BLACK);
        mPaint.setAntiAlias(true);

        setTypefaceWithoutStyle(IconFontHelper.getInstance().getCurrentTypeface());
    }

    public IconFontDrawable(Context context, String fontAsset) {
        this.mContext = context;
        mPaint = new TextPaint();
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setTextAlign(Paint.Align.CENTER);
        mPaint.setUnderlineText(false);
        mPaint.setColor(Color.BLACK);
        mPaint.setAntiAlias(true);

        if (!TextUtils.isEmpty(fontAsset)) {
            setTypefaceFromAsset(fontAsset);
        } else {
            setTypefaceWithoutStyle(IconFontHelper.getInstance().getCurrentTypeface());
        }
    }

    public IconFontDrawable setResourcesSize(int dimenRes) {
        return setPxSize(mContext.getResources().getDimensionPixelSize(dimenRes));
    }

    public IconFontDrawable setDpSize(int size) {
        return setPxSize((int) AndroidUtils.dpToPixels(mContext, size));
    }

    public IconFontDrawable setPxSize(int size) {
        this.mSize = size;
        setBounds(0, 0, size, size);
        invalidateSelf();
        return this;
    }

    public IconFontDrawable setColor(int color) {
        mPaint.setColor(color);
        invalidateSelf();
        return this;
    }

    public IconFontDrawable setResourcesColor(int colorRes) {
        mPaint.setColor(ContextCompat.getColor(mContext, colorRes));
        invalidateSelf();
        return this;
    }

    @Override
    public int getIntrinsicHeight() {
        return mSize;
    }

    @Override
    public int getIntrinsicWidth() {
        return mSize;
    }

    @Override
    public void draw(Canvas canvas) {
        if (!TextUtils.isEmpty(mText)) {
            mPaint.setTextSize(getBounds().height());
            Rect textBounds = new Rect();
            mPaint.getTextBounds(mText, 0, 1, textBounds);
            float textBottom = (getBounds().height() - textBounds.height()) / 2f + textBounds.height() - textBounds.bottom;
            canvas.drawText(mText, getBounds().width() / 2f, textBottom, mPaint);
        }
    }

    @Override
    public void setAlpha(int alpha) {
        mPaint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        mPaint.setColorFilter(cf);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.OPAQUE;
    }

    public IconFontDrawable setIcon(Integer iconResource) {
        String iconName = mContext.getString(iconResource);
        setIcon(iconName);
        return this;
    }

    public IconFontDrawable setIcon(String iconName) {
        if (TextUtils.isEmpty(iconName)) {
            mText = "";
        } else {
            IconFontInfo iconFontInfo = IconFontHelper.getInstance().getIconFontInfo(iconName);
            if (iconFontInfo == null) {
                Timber.w("setIcon fail, not found info of iconName: %s", iconName);
                mText = "";
            } else {
                mText = iconFontInfo.code;
            }
        }
        return this;
    }

    private void setTypefaceFromAsset(String fontAsset) {
        if (TextUtils.isEmpty(fontAsset)
                || mContext == null
                || mContext.getAssets() == null) {
            return;
        }
        Typeface typeface = FontHelper.getInstance().getFontFromAsset(mContext.getAssets(), fontAsset);
        if (typeface == null) {
            Timber.d("Could not create a typeface from asset: %s", fontAsset);
        } else {
            setTypefaceWithoutStyle(typeface);
        }
    }

    private void setTypefaceWithoutStyle(Typeface typeface) {
        if (typeface == null) {
            return;
        }

        mPaint.setTypeface(typeface);
    }
}