package com.dl7.textview.animtext;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.TextView;

/**
 * Created by long on 2016/7/5.
 */
public class TestTextView extends TextView implements ITextAnimation {

    private static final int MAX_TEXT_LENGTH = 100;
    // 每个字符动画时间
    private static final int CALC_TIME = 400;
    // 每个字符动画启动的间隔延迟时间，递增
    private static final int EACH_CHAR_DELAY = 20;

    // 是否为动画Text
    private boolean mIsAnimationText;
    private Paint mPaint;
    // 要绘制的字符串
    private CharSequence mText;
    // 字体大小
    private float mTextSize;
    // 字符个数
    private int mTextCount;
    // 动画
    private ValueAnimator mValueAnimator;
    // 动画时间
    private int mDuration;
    // 动画进度
    private int mProgress;
    // 字符的X坐标
    private float[] mCharOffset;


    public TestTextView(Context context) {
        this(context, null);
    }

    public TestTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TestTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (!mIsAnimationText) {
            super.onDraw(canvas);
            return;
        }
        for (int i = 0; i < mTextCount; i++) {
            // 字符动画启动的时间
            int startTime = i * EACH_CHAR_DELAY;
            // 动画进行的进度百分比，0→1
            float percent = (mProgress - startTime) * 1.0f / CALC_TIME;
            if (percent > 1.0f) {
                percent = 1;
            } else if (percent < 0) {
                percent = 0;
            }
            // 透明度
            int alpha = (int) (255 * percent);
            // 大小
            float size = mTextSize * percent;
            mPaint.setAlpha(alpha);
            mPaint.setTextSize(size);
            // 绘制字符
            canvas.drawText(String.valueOf(mText.charAt(i)), 0, 1, mCharOffset[i], getBaseline(), mPaint);
        }
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        super.setText(text, type);
        mIsAnimationText = false;
        _initVariables();
    }

    /**
     * @param text
     */
    public void setAnimationText(CharSequence text) {
        setText(text);
        mIsAnimationText = true;
        _calcCharOffset();
        _initAnimator();
    }

    /**
     * 初始化变量
     */
    private void _initVariables() {
        if (mPaint == null) {
            mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        }
        mPaint.setColor(getCurrentTextColor());
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setTextSize(getTextSize());
        mText = getText();
        mTextSize = getTextSize();
        mTextCount = mText.length();
    }

    /**
     * 初始化动画
     */
    private void _initAnimator() {
        mDuration = CALC_TIME + (mTextCount - 1) * EACH_CHAR_DELAY;
        mValueAnimator = ValueAnimator.ofInt(0, mDuration);
        mValueAnimator.setDuration(mDuration);
        mValueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mProgress = (int) animation.getAnimatedValue();
                invalidate();
            }
        });
        // 直接启动动画
        start();
    }

    /**
     * 计算字符的X坐标
     */
    private void _calcCharOffset() {
        if (mCharOffset == null) {
            mCharOffset = new float[MAX_TEXT_LENGTH];
        }
        int offset = 0;
        mCharOffset[0] = 0;
        for (int i = 0; i < mTextCount - 1; i++) {
            offset += mPaint.measureText(String.valueOf(mText.charAt(i)));
            mCharOffset[i + 1] = offset;
        }
    }

    /***************************************************************************/

    @Override
    public void start() {
        if (isRunning()) {
            stop();
        }
        mValueAnimator.start();
    }

    @Override
    public void stop() {
        mValueAnimator.end();
    }

    @Override
    public boolean isRunning() {
        return mValueAnimator != null || mValueAnimator.isRunning();
    }
}
