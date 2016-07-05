package com.dl7.textview.animtext;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by long on 2016/7/5.
 */
public class TestTextView extends TextView implements ITextAnimation {

    private static final int MAX_CHAT_LENGTH = 100;
    private static final int CALC_TIME = 400;

    private Paint mPaint;
    private CharSequence mText;
    private float mTextSize;
    private ValueAnimator mValueAnimator;
    private int mTextCount;
    private int mDuration;
    private int mProgress;
    private float[] mCharOffset;


    public TestTextView(Context context) {
        this(context, null);
    }

    public TestTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TestTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        _init();
    }

    private void _init() {
        if (mPaint == null) {
            mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        for (int i = 0; i < mTextCount; i++) {
            int startTime = i * CALC_TIME;
            float percent = (mProgress - startTime) * 1.0f / CALC_TIME;
            if (percent > 1.0f) {
                percent = 1;
            } else if (percent < 0) {
                percent = 0;
            }
            int alpha = (int) (255 * percent);
            float size = mTextSize * percent;
            mPaint.setAlpha(alpha);
            mPaint.setTextSize(size);
            canvas.drawText(String.valueOf(mText.charAt(i)), 0, 1, mCharOffset[i], getBaseline(), mPaint);
        }
//        canvas.drawText(mText, 0, mText.length() - 1, 0, getBaseline(), mPaint);
//        super.onDraw(canvas);
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        super.setText(text, type);
        if (mPaint == null) {
            mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        }
        mPaint.setColor(getCurrentTextColor());
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setTextSize(getTextSize());
        mText = getText();
        mTextSize = getTextSize();
        mTextCount = mText.length();
        _calcTextSpace();
        // 动画设置
        mDuration = mTextCount * CALC_TIME;
        mValueAnimator = ValueAnimator.ofInt(0, mDuration);
        mValueAnimator.setDuration(mDuration);
        mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mProgress = (int) animation.getAnimatedValue();
                invalidate();
            }
        });
    }

    private void _calcTextSpace() {
        if (mCharOffset == null) {
            mCharOffset = new float[MAX_CHAT_LENGTH];
        }
        int offset = 0;
        mCharOffset[0] = 0;
        for (int i = 0; i < mTextCount; i++) {
            offset += mPaint.measureText(String.valueOf(mText.charAt(i)));
            mCharOffset[i + 1] = offset;
        }
    }

    /***************************************************************************/

    @Override
    public void start() {
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
