package com.progressbarwithnumber;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.widget.ProgressBar;

/**
 * 自定义水平进度条，并且带进度显示
 * Created by yangtufa on 2016/11/1.
 */

public class HorizontalProgressBarWithNum extends ProgressBar {
    private static final int DEFAULT_TEXT_SIZE = 10;//默认字体大小
    private static final int DEFAULT_TEXT_COLOR = 0XFF7AB93B;//默认字体颜色
    private static final int DEFAULT_COLOR_UNREACHED_COLOR = 0xFFd3d6da;//默认的未完成部分进度的颜色
    private static final int DEFAULT_HEIGHT_REACHED_PROGRESS_BAR = 2;//默认的完成部分的高度
    private static final int DEFAULT_HEIGHT_UNREACHED_PROGRESS_BAR = 2;//默认未完成进度条的高度
    private static final int DEFAULT_SIZE_TEXT_OFFSET = 10;//默认的进度文本的偏移量

    /**
     * painter of all drawing things
     * 画笔
     */
    protected Paint mPaint = new Paint();
    /**
     * color of progress number
     * 进度文本颜色
     */
    protected int mTextColor = DEFAULT_TEXT_COLOR;
    /**
     * size of text (sp)
     * 文本字体大小
     */
    protected int mTextSize = sp2px(DEFAULT_TEXT_SIZE);

    /**
     * offset of draw progress
     * 进度文本偏移量
     */
    protected int mTextOffset = dp2px(DEFAULT_SIZE_TEXT_OFFSET);

    /**
     * height of reached progress bar
     * 进度条高度
     */
    protected int mReachedProgressBarHeight = dp2px(DEFAULT_HEIGHT_REACHED_PROGRESS_BAR);

    /**
     * color of reached bar
     * 完成进度的颜色
     */
    protected int mReachedBarColor = DEFAULT_TEXT_COLOR;
    /**
     * color of unreached bar
     * 未完成进度的颜色
     */
    protected int mUnReachedBarColor = DEFAULT_COLOR_UNREACHED_COLOR;
    /**
     * height of unreached progress bar
     * 未完成进度的高度
     */
    protected int mUnReachedProgressBarHeight = dp2px(DEFAULT_HEIGHT_UNREACHED_PROGRESS_BAR);
    /**
     * view width except padding
     * 不含pading的view高度
     */
    protected int mRealWidth;

    /**
     * 是否绘制进度文本
     */
    protected boolean mIfDrawText = true;

    /**
     * 进度文本是否可见
     */
    protected static final int VISIBLE = 0;

    public HorizontalProgressBarWithNum(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HorizontalProgressBarWithNum(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        setHorizontalScrollBarEnabled(true);

        obtainStyledAttributes(attrs);

        //初始化画笔的属性
        mPaint.setTextSize(mTextSize);
        mPaint.setColor(mTextColor);
    }

    /**
     * 获取自定义属性
     * get the styled attributes
     * @param attrs
     */
    private void obtainStyledAttributes(AttributeSet attrs) {
        //初始化自定义属性值
        final TypedArray attributes = getContext().obtainStyledAttributes(
                attrs, R.styleable.HorizontalProgressBarWithNumber);

        //文本颜色
        mTextColor = attributes
                .getColor(
                        R.styleable.HorizontalProgressBarWithNumber_progress_text_color,
                        DEFAULT_TEXT_COLOR);
        //文本字体大小
        mTextSize = (int) attributes.getDimension(
                R.styleable.HorizontalProgressBarWithNumber_progress_text_size,
                mTextSize);

        //完成部分进度的颜色
        mReachedBarColor = attributes
                .getColor(
                        R.styleable.HorizontalProgressBarWithNumber_progress_reached_color,
                        mTextColor);
        //未完成部分的颜色
        mUnReachedBarColor = attributes
                .getColor(
                        R.styleable.HorizontalProgressBarWithNumber_progress_unreached_color,
                        DEFAULT_COLOR_UNREACHED_COLOR);
        //完成部分的进度条高度
        mReachedProgressBarHeight = (int) attributes
                .getDimension(
                        R.styleable.HorizontalProgressBarWithNumber_progress_reached_bar_height,
                        mReachedProgressBarHeight);
        //未完成部分的进度条高度
        mUnReachedProgressBarHeight = (int) attributes
                .getDimension(
                        R.styleable.HorizontalProgressBarWithNumber_progress_unreached_bar_height,
                        mUnReachedProgressBarHeight);
        //进度文本的偏移量
        mTextOffset = (int) attributes
                .getDimension(
                        R.styleable.HorizontalProgressBarWithNumber_progress_text_offset,
                        mTextOffset);
        //进度文本是否可见
        int textVisible = attributes
                .getInt(R.styleable.HorizontalProgressBarWithNumber_progress_text_visibility,
                        VISIBLE);
        if (textVisible != VISIBLE) {
            mIfDrawText = false;
        }
        attributes.recycle();
    }

    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        if (heightMode != MeasureSpec.EXACTLY) {

            float textHeight = (mPaint.descent() + mPaint.ascent());
            int exceptHeight = (int) (getPaddingTop() + getPaddingBottom() +
                    Math.max(Math.max(mReachedProgressBarHeight, mUnReachedProgressBarHeight), Math.abs(textHeight)));

            heightMeasureSpec = MeasureSpec.makeMeasureSpec(exceptHeight,
                    MeasureSpec.EXACTLY);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    protected synchronized void onDraw(Canvas canvas) {
        canvas.save();
        //画布平移到指定paddingLeft， getHeight() / 2位置，注意以后坐标都为以此为0，0
        canvas.translate(getPaddingLeft(), getHeight()/2);

        boolean noNeedBg = false;
        //当前进度和总值的比例
        float radio = getProgress() * 1.0f / getMax();
        //已到达的宽度
        float progressPosX = (int) (mRealWidth * radio);
        Log.e("info","已经到达的进度："+progressPosX);
        //绘制的文本
        String text = getProgress() + "%";

        //拿到字体的宽度和高度
        float textWidth = mPaint.measureText(text);
        float textHeight = (mPaint.descent() + mPaint.ascent()) / 2;

        //如果到达最后，则未到达的进度条不需要绘制
        if (progressPosX + textWidth > mRealWidth) {
            progressPosX = mRealWidth - textWidth;
            noNeedBg = true;
        }

        // 绘制已到达的进度
        float endX = progressPosX - mTextOffset / 2;
        if (endX > 0) {
            mPaint.setColor(mReachedBarColor);
            mPaint.setStrokeWidth(mReachedProgressBarHeight);
            canvas.drawLine(0, 0, endX, 0, mPaint);
        }

        // 绘制文本
        if (mIfDrawText) {
            mPaint.setColor(mTextColor);
            canvas.drawText(text, progressPosX, -textHeight, mPaint);
        }

        // 绘制未到达的进度条
        if (!noNeedBg) {
            float start = progressPosX + mTextOffset / 2 + textWidth;
            mPaint.setColor(mUnReachedBarColor);
            mPaint.setStrokeWidth(mUnReachedProgressBarHeight);
            canvas.drawLine(start, 0, mRealWidth, 0, mPaint);
        }

        canvas.restore();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mRealWidth = w - getPaddingRight() - getPaddingLeft();
    }

    /**
     * dp 2 px
     * @param dpVal 需要转换的值
     */
    protected int dp2px(int dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dpVal, getResources().getDisplayMetrics());
    }

    /**
     * sp 2 px
     * @param spVal 需要转换的值
     * @return
     */
    protected int sp2px(int spVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                spVal, getResources().getDisplayMetrics());
    }
}
