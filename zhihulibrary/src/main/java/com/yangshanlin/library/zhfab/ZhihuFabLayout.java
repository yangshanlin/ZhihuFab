package com.yangshanlin.library.zhfab;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.IntDef;
import android.support.v4.content.ContextCompat;
import android.support.v7.content.res.AppCompatResources;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;
import android.view.animation.LinearInterpolator;

import com.yangshanlin.library.R;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 自定义按钮父布局
 *
 * @author yangshanlin
 */
public class ZhihuFabLayout extends ViewGroup {

    /**
     * 显示位置位置
     */
    public final static int GRAVITY_LEFT_BOTTOM = 0;
    public final static int GRAVITY_RIGHT_BOTTOM = 1;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({GRAVITY_LEFT_BOTTOM, GRAVITY_RIGHT_BOTTOM})
    public @interface ZHGravity {
    }

    /**
     * 菜单显示动画类型
     */
    public static final int ANI_FADE = 0;
    public static final int ANI_SCALE = 1;
    public static final int ANI_BOUNCE = 2;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({ANI_FADE, ANI_SCALE, ANI_BOUNCE})
    public @interface ZHANI {
    }

    /**
     * 默认动画时长
     */
    private static final int DEFAULT_ANIM_DURATION = 200;

    /**
     * 用于显示背景颜色的View
     */
    private View mBackgroundView;
    /**
     * 变暗背景颜色
     */
    private int mBackgroundColor;

    /**
     * 开关FloatingActionButton
     */
    private ZhihuFab mSwitchFab;
    private int mSwitchFabDrawableResId = -1;
    /**
     * 开关fab图片
     */
    private Drawable mSwitchFabIcon;
    //开关fab颜色
//    private ColorStateList mSwitchFabColor;

    /**
     * 动画时长
     */
    private int mAnimationDuration;
    /**
     * 动画模式
     */
    private int mAnimationMode;

    /**
     * 显示位置
     */
    private int mGrayity;

    /**
     * 主Fab是否被点开
     */
    private boolean isMenuOpen = false;
    /**
     * 是否在执行背景动画
     */
    private boolean inBackgroundAni = false;
    /**
     * 是否在执行主Fab旋转动画
     */
    private boolean inFabRotationAni = false;

    /**
     * 是否在执行menu动画
     */
    private boolean inFabMenuAni = false;
    private OnFabItemClickListener mOnFabItemClickListener;

    /**
     * 弹出按钮点击事件
     */
    public interface OnFabItemClickListener {
        /**
         * 按钮点击事件回调
         *
         * @param view 点击的
         * @param pos
         */
        void onFabItemClick(ZhihuMenuLayout view, int pos);
    }

    public void setOnFabItemClickListener(OnFabItemClickListener onFabItemClickListener) {
        mOnFabItemClickListener = onFabItemClickListener;
    }


    public ZhihuFabLayout(Context context) {
        this(context, null);
    }

    public ZhihuFabLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ZhihuFabLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //获取属性值
        getAttributes(context, attrs);
        //添加一个背景View和一个FloatingActionButton
        setBaseViews(context);
    }

    /**
     * 获取自定义属性
     *
     * @param context
     * @param attrs
     */
    private void getAttributes(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ZhihuFabLayout);

        mBackgroundColor = typedArray.getColor(
                R.styleable.ZhihuFabLayout_zhBackgroundColor, Color.WHITE);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            mBackgroundColor = Color.TRANSPARENT;
        }
        mSwitchFabDrawableResId = typedArray.getResourceId(R.styleable.ZhihuFabLayout_zhFabIcon, -1);

        mAnimationDuration = typedArray.getInt(R.styleable.ZhihuFabLayout_zhAnimationDuration, DEFAULT_ANIM_DURATION);
        mAnimationMode = typedArray.getInt(R.styleable.ZhihuFabLayout_zhAnimationMode, ANI_SCALE);
        mGrayity = typedArray.getInt(R.styleable.ZhihuFabLayout_zhGrayity, GRAVITY_RIGHT_BOTTOM);
        typedArray.recycle();
    }

    /**
     * 添加背景，及开关Fab（主Fab）
     *
     * @param context
     */
    private void setBaseViews(Context context) {
        mBackgroundView = new View(context);
        mBackgroundView.setBackgroundColor(mBackgroundColor);
        mBackgroundView.setAlpha(0);
        addView(mBackgroundView);

        mSwitchFab = new ZhihuFab(context);
        if (mSwitchFabDrawableResId != -1) {
            mSwitchFabIcon = AppCompatResources.getDrawable(context, mSwitchFabDrawableResId);
            if (mSwitchFabIcon != null) {
                mSwitchFab.setIconDrawable(mSwitchFabIcon);
            }
        }
        addView(mSwitchFab);

    }


    /**
     * 添加一个menu
     *
     * @param tagText
     * @param menuIconResId
     */
    public ZhihuFabLayout addMenu(String tagText, @DrawableRes int menuIconResId) {
        ZhihuMenuLayout zhihuMenuLayout = new ZhihuMenuLayout(getContext());
        zhihuMenuLayout.setText(tagText);
        zhihuMenuLayout.setTagButtonIcon(menuIconResId);
        addView(zhihuMenuLayout);
        return this;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            measureChild(getChildAt(i), widthMeasureSpec, heightMeasureSpec);
            if (i >= 2 && !(getChildAt(i) instanceof ZhihuMenuLayout)) {
                throw new IllegalArgumentException("the child view must be ZhihuMenuLayout");
            }
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (changed) {
            //布局背景和主Fab
            layoutSwitchFab();
            layoutBackgroundView();
            layoutItems();
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean intercepted = false;
        int x = (int) ev.getX();
        int y = (int) ev.getY();
        if (isMenuOpen) {
            switch (ev.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (judgeIfTouchBackground(x, y)) {
                        intercepted = true;
                    }
                    intercepted = false;
                    break;
                case MotionEvent.ACTION_MOVE:
                    intercepted = false;
                    break;
                case MotionEvent.ACTION_UP:
                    intercepted = false;
                    break;
                default:
            }
        }
        return intercepted;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (isMenuOpen) {
            closeMenu();
            changeBackground();
            rotateFloatingButton();
            changeStatus();
            return true;
        }
        return super.onTouchEvent(event);
    }

    private boolean judgeIfTouchBackground(int x, int y) {
        Rect a = new Rect();
        Rect b = new Rect();
        a.set(0, 0, getWidth(), getHeight() - getChildAt(getChildCount() - 1).getTop());
        b.set(0, getChildAt(getChildCount() - 1).getTop(), getChildAt(getChildCount() - 1).getLeft(), getHeight());
        if (a.contains(x, y) || b.contains(x, y)) {
            return true;
        }
        return false;
    }


    private void layoutSwitchFab() {
        int width = mSwitchFab.getMeasuredWidth();
        int height = mSwitchFab.getMeasuredHeight();

        int fl = 0;
        int ft = 0;
        int fr = 0;
        int fb = 0;

        switch (mGrayity) {
            case GRAVITY_LEFT_BOTTOM:
            case GRAVITY_RIGHT_BOTTOM:
                fl = getMeasuredWidth() - width - dp2px(16);
                ft = getMeasuredHeight() - height - dp2px(16);
                fr = fl + width;
                fb = ft + height;
                break;

        }

        mSwitchFab.layout(fl, ft, fr, fb);
        bindFloatingEvent();

    }

    private void layoutBackgroundView() {
        mBackgroundView.layout(0, 0
                , getMeasuredWidth(), getMeasuredHeight());
    }

    private void layoutItems() {
        int count = getChildCount();
        int fabHeight = mSwitchFab.getMeasuredHeight();
        for (int i = 2; i < count; i++) {
            ZhihuMenuLayout child = (ZhihuMenuLayout) getChildAt(i);
            child.setVisibility(INVISIBLE);

            int width = child.getMeasuredWidth();
            int height = child.getMeasuredHeight();


            int cl = 0;
            int ct = 0;

            switch (mGrayity) {
                case GRAVITY_LEFT_BOTTOM:
                case GRAVITY_RIGHT_BOTTOM:
                    cl = getMeasuredWidth() - width - dp2px(16);
                    ct = getMeasuredHeight() - fabHeight - (i - 1) * height - dp2px(16) - dp2px(8);
                default:
            }
            child.layout(cl, ct, cl + width, ct + height);
            bindMenuEvents(child, i);
            prepareAnim(child);
        }
    }

    /**
     * 绑定主fab点击事件，用于展开和关闭menu菜单
     */
    private void bindFloatingEvent() {
        mSwitchFab.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!inBackgroundAni && !inFabRotationAni && !inFabMenuAni) {
                    rotateFloatingButton();
                    changeBackground();
                    if (isMenuOpen) {
                        closeMenu();
                    } else {
                        openMenu();
                    }
                    changeStatus();
                }
            }
        });
    }

    /**
     * 绑定menu菜单点击事件
     *
     * @param child
     * @param pos
     */
    private void bindMenuEvents(final ZhihuMenuLayout child, final int pos) {
        child.setOnTagClickListener(new ZhihuMenuLayout.OnTagClickListener() {
            @Override
            public void onTagClick() {
                rotateFloatingButton();
                changeBackground();
                closeMenu();
                changeStatus();
                if (mOnFabItemClickListener != null) {
                    mOnFabItemClickListener.onFabItemClick(child, pos);
                }
            }
        });

        child.setOnFabClickListener(new ZhihuMenuLayout.OnFabClickListener() {
            @Override
            public void onFabClick() {
                rotateFloatingButton();
                changeBackground();
                closeMenu();
                changeStatus();
                if (mOnFabItemClickListener != null) {
                    mOnFabItemClickListener.onFabItemClick(child, pos);
                }
            }
        });
    }

    private void prepareAnim(ZhihuMenuLayout child) {
        switch (mAnimationMode) {
            case ANI_BOUNCE:
                child.setTranslationY(50);
                break;
            case ANI_SCALE:
                child.setScaleX(0f);
                child.setScaleY(0f);
                break;
            default:
        }
    }

    /**
     * 旋转主fab按钮动画
     */
    private void rotateFloatingButton() {
        ObjectAnimator animator = isMenuOpen
                ? ObjectAnimator.ofFloat(mSwitchFab, "rotation", 135F, -10F, 0F)
                : ObjectAnimator.ofFloat(mSwitchFab, "rotation", 0F, 145F, 135F);
        animator.setDuration(mAnimationDuration);
        animator.setInterpolator(new LinearInterpolator());
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                inFabRotationAni = true;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                inFabRotationAni = false;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                inFabRotationAni = false;
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animator.start();
    }

    /**
     * 背景显示隐藏动画
     */
    private void changeBackground() {
        int centerX = getMeasuredWidth() - dp2px(16) - mSwitchFab.getMeasuredWidth() / 2;
        int centerY = getMeasuredHeight() - dp2px(16) - mSwitchFab.getMeasuredHeight() / 2;
        int maxRadius = (int) Math.sqrt(getMeasuredHeight() * getMeasuredHeight() + getMeasuredWidth() * getMeasuredWidth());
        int minRadius = Math.min(mSwitchFab.getMeasuredHeight() / 2, mSwitchFab.getMeasuredWidth() / 2);
//        if (isMenuOpen) {
//            hide(mBackgroundView, centerX, centerY, maxRadius, minRadius, mAnimationDuration);
//        } else {
//            show(mBackgroundView, centerX, centerY, minRadius, maxRadius, mAnimationDuration);
//        }
        doChangeBackgroundAni(mBackgroundView, centerX, centerY, minRadius, maxRadius, mAnimationDuration);
    }

    /**
     * 背景扩散动画
     *
     * @param view      背景View
     * @param centerX   扩散中心X坐标
     * @param centerY   扩散中心Y坐标
     * @param minRadius 最小扩散半径
     * @param maxRadius 最大扩散半径
     * @param duration  动画时长
     */
    public void doChangeBackgroundAni(final View view,
                                      int centerX, int centerY, float minRadius, float maxRadius, int duration) {
        ObjectAnimator alpha = isMenuOpen
                ? ObjectAnimator.ofFloat(view, "alpha", 0.9F, 0F)
                : ObjectAnimator.ofFloat(view, "alpha", 0F, 0.9F);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            alpha.setInterpolator(new LinearInterpolator());
            alpha.setDuration(duration);
            alpha.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    inBackgroundAni = true;
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    inBackgroundAni = false;
                    view.setVisibility(VISIBLE);
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
            alpha.start();
            return;
        }

        Animator circleAni = isMenuOpen
                ? ViewAnimationUtils.createCircularReveal(view, centerX, centerY, maxRadius, minRadius)
                : ViewAnimationUtils.createCircularReveal(view, centerX, centerY, minRadius, maxRadius);
        AnimatorSet set = new AnimatorSet();
        set.playTogether(alpha, circleAni);
        set.setDuration(duration);
        set.setInterpolator(new LinearInterpolator());
        set.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                inBackgroundAni = true;
                view.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                inBackgroundAni = false;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                inBackgroundAni = false;
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        set.start();
    }

    /**
     * 改变menu打开状态
     */
    private void changeStatus() {
        isMenuOpen = !isMenuOpen;
    }

    /**
     * 打开菜单
     */
    private void openMenu() {
        switch (mAnimationMode) {
            case ANI_BOUNCE:
                bounceToShow();
                break;
            case ANI_SCALE:
                scaleToShow();
            default:
        }
    }

    /**
     * 打开菜单bounce动画
     */
    private void bounceToShow() {
        for (int i = 2; i < getChildCount(); i++) {
            final View view = getChildAt(i);

            ObjectAnimator trans = ObjectAnimator.ofFloat(view, "translationY", 50F, 0F);
            ObjectAnimator show = ObjectAnimator.ofFloat(view, "alpha", 0F, 1F);
            AnimatorSet set = new AnimatorSet();
            set.play(trans).with(show);
            set.setDuration(mAnimationDuration);
            set.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    inFabMenuAni = true;
                    view.setVisibility(VISIBLE);
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    inFabMenuAni = false;
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    inFabMenuAni = false;
                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
            set.setInterpolator(new BounceInterpolator());
            set.start();

        }
    }

    /**
     * 打开菜单缩放动画
     */
    private void scaleToShow() {
        for (int i = 2; i < getChildCount(); i++) {
            View view = getChildAt(i);
            view.setVisibility(VISIBLE);
            view.setAlpha(0);
            ObjectAnimator scaleX = ObjectAnimator.ofFloat(view, "scaleX", 0F, 1F);
            ObjectAnimator scaleY = ObjectAnimator.ofFloat(view, "scaleY", 0F, 1F);
            ObjectAnimator alpha = ObjectAnimator.ofFloat(view, "alpha", 0F, 1F);
            AnimatorSet set = new AnimatorSet();
            set.playTogether(scaleX, scaleY, alpha);
            set.setDuration(mAnimationDuration);
            set.start();
        }
    }

    /**
     * 关闭菜单动画
     */
    private void closeMenu() {
        for (int i = 2; i < getChildCount(); i++) {
            final View view = getChildAt(i);
            ObjectAnimator alpha = ObjectAnimator.ofFloat(view, "alpha", 1F, 0F);
            alpha.setDuration(mAnimationDuration);
            alpha.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    view.setVisibility(INVISIBLE);
                }
            });
            alpha.start();
        }
    }

    private int dp2px(int value) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP
                , value, getResources().getDisplayMetrics());

    }

    public void setAnimationDuration(int duration) {
        mAnimationDuration = duration;
    }

    public void setTagBackgroundColor(int color) {
        for (int i = 2; i < getChildCount(); i++) {
            ZhihuMenuLayout tagFabLayout = (ZhihuMenuLayout) getChildAt(i);
            tagFabLayout.setBackgroundColor(color);
        }
    }

    public void setTextColor(@ColorRes int color) {
        int intColor = ContextCompat.getColor(getContext(), color);
        for (int i = 2; i < getChildCount(); i++) {
            ZhihuMenuLayout tagFabLayout = (ZhihuMenuLayout) getChildAt(i);
            tagFabLayout.setTextColor(intColor);
        }
    }

    public void setTextSize(float size) {
        for (int i = 2; i < getChildCount(); i++) {
            ZhihuMenuLayout tagFabLayout = (ZhihuMenuLayout) getChildAt(i);
            tagFabLayout.setTextSize(size);
        }
    }

    public void setBackgroundColor(int color) {
        mBackgroundColor = color;
        mBackgroundView.setBackgroundColor(color);
    }

    public void setSwitchFabIcon(@DrawableRes int icon) {
        mSwitchFab.setIcon(icon);
    }

    public boolean getButtonState() {
        return isMenuOpen;
    }

}