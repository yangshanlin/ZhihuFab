package com.yangsl.library.zhfab;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.content.res.AppCompatResources;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;

import com.yangsl.library.R;

/**
 * 该ViewGroup是点击Tab后展开的条目
 * 组合一个TagView和一个FloatingActionButton
 * 默认Tag在左，button在右
 */
public class ZhihuMenuLayout extends ViewGroup {
    //menu fab背景颜色
    private int mFabMenubackground;
    //tag标签文字
    private String mTagText;
    //标签文字颜色
    @ColorInt
    private int mTagTextColor;
    //弹出menu fab图片
    private Drawable mMenuFabIcon;
    @DrawableRes
    private int mMenuDrawableResId = -1;

    private OnTagClickListener mOnTagClickListener;
    private OnFabClickListener mOnFabClickListener;
    private ZhihuTagView mTagView;
    private ZhihuFab mTagButton;

    public interface OnTagClickListener {
        void onTagClick();
    }

    public interface OnFabClickListener {
        void onFabClick();
    }

    public void setOnTagClickListener(OnTagClickListener onTagClickListener) {
        mOnTagClickListener = onTagClickListener;
    }

    public void setOnFabClickListener(OnFabClickListener onFabClickListener) {
        mOnFabClickListener = onFabClickListener;
    }

    public ZhihuMenuLayout(Context context) {
        this(context, null);
    }

    public ZhihuMenuLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ZhihuMenuLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ZhihuMenuLayout);
        mTagText = typedArray.getString(R.styleable.ZhihuMenuLayout_zhTagText);
        mTagTextColor = typedArray.getColor(R.styleable.ZhihuMenuLayout_zhTagTextColor, Color.GRAY);
        mMenuDrawableResId = typedArray.getResourceId(R.styleable.ZhihuMenuLayout_zhFabIcon, -1);
        typedArray.recycle();
        settingTagView(context);
    }


    int getColor(@ColorRes int id) {
        return ContextCompat.getColor(getContext(), id);
    }

    private void settingTagView(Context context) {
        mTagView = new ZhihuTagView(context);
        mTagView.setTagText(mTagText);
        mTagView.setTextColor(mTagTextColor);
        addView(mTagView);

        mTagButton = new ZhihuFab(context);
        mTagButton.setSize(ZhihuFab.SIZE_MINI);
        if (mMenuDrawableResId != -1) {
            mMenuFabIcon = AppCompatResources.getDrawable(context, mMenuDrawableResId);
            if (mMenuFabIcon != null) {
                mTagButton.setIconDrawable(mMenuFabIcon);
            }
        }
        addView(mTagButton);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = 0;
        int height = 0;

        int count = getChildCount();
        for (int i = 0; i < count; i++) {

            View view = getChildAt(i);

            measureChild(view, widthMeasureSpec, heightMeasureSpec);

            width += view.getMeasuredWidth();
            height = Math.max(height, view.getMeasuredHeight());
        }

        width += dp2px(8 + 8 + 8);
        height += dp2px(8 + 8);

        //直接将该ViewGroup设定为wrap_content的
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        //为子View布局
        View tagView = getChildAt(0);
        View fabView = getChildAt(1);

        int tagWidth = tagView.getMeasuredWidth();
        int tagHeight = tagView.getMeasuredHeight();

        int fabWidth = fabView.getMeasuredWidth();
        int fabHeight = fabView.getMeasuredHeight();

        int tl = dp2px(8);
        int tt = (getMeasuredHeight() - tagHeight) / 2;
        int tr = tl + tagWidth;
        int tb = tt + tagHeight;

        int fl = tr + dp2px(8);
        int ft = (getMeasuredHeight() - fabHeight) / 2;
        int fr = fl + fabWidth;
        int fb = ft + fabHeight;

        fabView.layout(fl, ft, fr, fb);
        tagView.layout(tl, tt, tr, tb);
        bindEvents(tagView, fabView);
    }

    private void bindEvents(View tagView, View fabView) {
        tagView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnTagClickListener != null) {
                    mOnTagClickListener.onTagClick();
                }
            }
        });

        fabView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnFabClickListener != null) {
                    mOnFabClickListener.onFabClick();
                }
            }
        });
    }

    private int dp2px(int value) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP
                , value, getResources().getDisplayMetrics());

    }

    public void setBackgroundColor(int color) {
        mTagView.setBackgroundColor(color);
    }

    public void setTextColor(@ColorInt int color) {
        mTagTextColor = color;
        mTagView.setTextColor(color);
    }

    public void setTextSize(float size) {
        mTagView.setTextSize(size);
    }

    public void setText(String tagText) {
        if (TextUtils.isEmpty(tagText)) {
            return;
        }
        mTagText = tagText;
        mTagView.setText(tagText);
    }

    public String getTagText() {
        return mTagText;
    }

    public void setTagButtonIcon(@DrawableRes int iconResId) {
        mTagButton.setIcon(iconResId);
    }

}