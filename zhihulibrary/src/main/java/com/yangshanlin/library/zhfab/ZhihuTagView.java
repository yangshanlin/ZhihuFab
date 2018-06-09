package com.yangshanlin.library.zhfab;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yangshanlin.library.R;

/**
 * 在TabTagLayout中需要一个能设置Text的CardView
 * 所以继承CardView实现一个TagView
 *
 * @author yangshanlin
 */
public class ZhihuTagView extends CardView {

    private TextView mTextView;
    private int CARD_ATTR = R.attr.selectableItemBackgroundBorderless;

    public ZhihuTagView(Context context) {
        this(context, null);
    }

    public ZhihuTagView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ZhihuTagView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray array = context.obtainStyledAttributes(new int[]{CARD_ATTR});
        Drawable foreground = array.getDrawable(0);
        array.recycle();
        mTextView = new TextView(context);
        mTextView.setSingleLine(true);
        this.setForeground(foreground);
    }

    protected void setTextSize(float size) {
        mTextView.setTextSize(size);
    }

    protected void setTextColor(@ColorInt int color) {
        mTextView.setTextColor(color);
    }

    protected void setText(String text) {
        mTextView.setText(text);
    }

    protected void setTagText(String text) {
        mTextView.setText(text);
        addTag();
    }

    private void addTag() {
        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT
                , ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER);
        int l = dp2px(8);
        int t = dp2px(4);
        int r = l;
        int b = t;
        layoutParams.setMargins(l, t, r, b);
        //addView会引起所有View的layout
        addView(mTextView, layoutParams);
    }

    private int dp2px(int value) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP
                , value, getResources().getDisplayMetrics());
    }

}