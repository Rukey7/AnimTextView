package com.dl7.textview.animtext;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.TextView;

import com.dl7.textview.entity.RepeatChar;
import com.dl7.textview.until.RepeatCharHelper;

import java.util.List;

/**
 * Created by long on 2016/7/6.
 * 缩放动画的TextView
 */
public class ScaleTextView extends TextView implements ITextAnimation {

    private static final int MAX_TEXT_LENGTH = 100;
    // 每个字符动画时间
    private static final int CALC_TIME = 400;
    // 每个字符动画启动的间隔延迟时间，递增
    private static final int EACH_CHAR_DELAY = 20;

    // 是否为动画Text
    private boolean mIsAnimationText;
    private Paint mPaint;
    private Paint mOldPaint;
    // 要绘制的字符串
    private CharSequence mText;
    // 上一个绘制的字符串
    private CharSequence mOldText;
    // 字体大小
    private float mTextSize;
    // 上一个字体大小
    private float mOldTextSize;
    // 字符个数
    private int mTextCount;
    // 上一个字符个数
    private int mOldTextCount;
    // 字符的X坐标
    private float[] mCharOffset;
    // 上一个字符的X坐标
    private float[] mOldCharOffset;
    // 动画
    private ValueAnimator mValueAnimator;
    // 动画时间
    private int mDuration;
    // 动画进度
    private int mProgress;
    // 重复字符列表
    private List<RepeatChar> mRepeatCharList;


    public ScaleTextView(Context context) {
        this(context, null);
    }

    public ScaleTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ScaleTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }



    @Override
    protected void onDraw(Canvas canvas) {
        if (!mIsAnimationText) {
            super.onDraw(canvas);
            return;
        }
        // 变换百分比
        float percent;
        // 2倍速百分比
        float twoPercent = 0;

        // 绘制之前字符串
        for (int i = 0; i < mOldTextCount; i++) {
            percent = mProgress * 1f / mDuration;
            if (twoPercent < 1f) {
                twoPercent = percent * 2f;
                twoPercent = twoPercent > 1.0f ? 1.0f : twoPercent;
            }
            // 判断是否为重复字符
            int curIndex = RepeatCharHelper.needMove(i, mRepeatCharList);
            if (curIndex != -1) {
                // 计算偏移值
                float offset = (mCharOffset[curIndex] - mOldCharOffset[i]) * percent;
                // 计算移动中的X坐标
                float moveX = mOldCharOffset[i] + offset;
                // 计算大小
                float size = mOldTextSize + (mTextSize - mOldTextSize) * percent;
                mOldPaint.setAlpha(255);
                mOldPaint.setTextSize(size);
                // 绘制字符
                canvas.drawText(String.valueOf(mOldText.charAt(i)), 0, 1, moveX, getBaseline(), mOldPaint);
            } else {
                // 在进度的50%完成操作
                // 透明度
                int alpha = (int) (255 * (1 - twoPercent));
                // 大小
                float size = mOldTextSize * (1 - twoPercent);
                mOldPaint.setAlpha(alpha);
                mOldPaint.setTextSize(size);
                // 绘制字符
                canvas.drawText(String.valueOf(mOldText.charAt(i)), 0, 1, mOldCharOffset[i], getBaseline(), mOldPaint);
            }
        }
        // 绘制当前字符串
        for (int i = 0; i < mTextCount; i++) {
            if (!RepeatCharHelper.isNoChange(i, mRepeatCharList)) {
                // 字符动画启动的时间
                int startTime = i * EACH_CHAR_DELAY;
                // 动画进行的进度百分比，0→1
                percent = (mProgress - startTime) * 1.0f / CALC_TIME;
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
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        super.setText(text, type);
        mIsAnimationText = false;
        _initVariables();
    }

    /**
     * @param text 实现TextView文字的动态切换，可用于搜索框预选词的切换，比如360助手
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
            mPaint.setStyle(Paint.Style.FILL);
        }
        if (mOldPaint == null) {
            mOldPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mPaint.setStyle(Paint.Style.FILL);
        }
        mOldText = mText;
        mText = getText();
        mOldTextSize = mTextSize;
        mTextSize = getTextSize();
        mOldTextCount = mTextCount;
        mTextCount = mText.length();

        mOldPaint.setColor(mPaint.getColor());
        mOldPaint.setTextSize(mOldTextSize);
        mPaint.setColor(getCurrentTextColor());
        mPaint.setTextSize(getTextSize());
    }

    /**
     * 计算字符的X坐标
     */
    private void _calcCharOffset() {
        if (mCharOffset == null || mOldCharOffset == null) {
            mCharOffset = new float[MAX_TEXT_LENGTH];
            mOldCharOffset = new float[MAX_TEXT_LENGTH];
        }
        // 计算当前字符X坐标
        int offset = 0;
        mCharOffset[0] = 0;
        for (int i = 0; i < mTextCount - 1; i++) {
            offset += mPaint.measureText(String.valueOf(mText.charAt(i)));
            mCharOffset[i + 1] = offset;
        }
        // 计算旧字符X坐标
        offset = 0;
        mOldCharOffset[0] = 0;
        for (int i = 0; i < mOldTextCount - 1; i++) {
            offset += mOldPaint.measureText(String.valueOf(mOldText.charAt(i)));
            mOldCharOffset[i + 1] = offset;
        }
        // 查找重复字符
        mRepeatCharList = RepeatCharHelper.findRepeatChar(mText, mOldText);
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
