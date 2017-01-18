package com.zyt.tx.myapplication;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;


/**
 * Created by MJS on 2017/1/18.
 */

public class LyricView extends View {

    private static final int MSG_PLAYER_HIDE = 0x158;
    private static final int MSG_PLAYER_SLIDE = 0x157;
    private int maximumFlingVelocity;
    private Paint mTextPaint;


    /**
     * 渐变过渡的距离
     */
    private float mShaderWidth;


    private LyricInfo mLyricInfo;
    private String mDefaultHint = "LyricView";
    /**
     * 行数
     */
    private int mLineCount;
    /**
     * 行高
     */
    private float mLineHeight;
    /**
     * 当前播放位置对应的行数
     */
    private int mCurrentPlayLine = 0;

    private int mHighLightColor = Color.parseColor("#4FC5C7");//当前播放位置颜色

    private boolean mIndicatorShow = false;
    /**
     * 当前拖动位置对应的行数
     */
    private int mCurrentShowLine = 0;

    /**
     * 当前拖动位置的颜色
     */
    private int mCurrentShowColor = Color.parseColor("#AAAAAA");
    /**
     * 默认字体颜色
     */
    private int mDefaultColor = Color.parseColor("#FFFFFF");
    /**
     * 提示语颜色
     */
    private int mHintColor = Color.parseColor("#FFFFFF");
    /**
     * 行间距
     */
    private float mLineSpace = 0;
    /**
     * 纵轴偏移量
     */
    private float mScrollY = 0;
    private VelocityTracker mVelocityTracker;

    /**
     * 判断当前用户是否触摸
     */
    private boolean mUserTouch = false;

    private boolean mSliding = false;

    public LyricView(Context context) {
        this(context, null);
    }

    public LyricView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LyricView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initMyView(context);
    }

    private void initMyView(Context context) {
        /*最大滑动速度，以像素为单位*/
        maximumFlingVelocity = ViewConfiguration.get(context).getScaledMaximumFlingVelocity();
        initAllPaints();
        initAllBounds();
    }

    private void initAllBounds() {

    }

    private void initAllPaints() {
        mTextPaint = new Paint();

        /*用于setFlags（）的辅助程序，设置或清除DITHER_FLAG位抖动会影响如何对比设备精度更高的颜色进行下采样。
        * 没有抖动通常更快，但更高精度的颜色只是被截断（例如8888 - > 565）
        * 。抖动尝试分发此过程中固有的错误，以减少视觉伪影。*/
        mTextPaint.setDither(true);
        mTextPaint.setAntiAlias(true);
        mTextPaint.setTextAlign(Paint.Align.CENTER);

        Paint mIndicatorPaint = new Paint();
        mIndicatorPaint.setDither(true);
        mIndicatorPaint.setAntiAlias(true);
        mIndicatorPaint.setTextSize(getRawSize(TypedValue.COMPLEX_UNIT_SP, 12));
        mIndicatorPaint.setTextAlign(Paint.Align.CENTER);
    }

    private float getRawSize(int unit, int size) {
        Context context = getContext();
        Resources resources;
        if (context == null) {
            resources = Resources.getSystem();
        } else {
            resources = context.getResources();
        }
        return TypedValue.applyDimension(unit, size, resources.getDisplayMetrics());
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mShaderWidth = getMeasuredHeight() * 0.3f;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mLyricInfo != null && mLyricInfo.songLines != null && mLyricInfo.songLines.size() > 0) {
            for (int i = 0; i < mLineCount; i++) {
                float x = getMeasuredWidth() * 0.5f;
                float y = getMeasuredHeight() * 0.5f + (i + 0.5f) * mLineHeight - 6 - mLineSpace * 0.5f - mScrollY;
                if (y + mLineHeight * 0.5f < 0) {
                    continue;
                }
                if (y - mLineHeight * 0.5f > getMeasuredHeight()) {
                    break;
                }
                if (i == mCurrentPlayLine - 1) {
                    mTextPaint.setColor(mHighLightColor);
                } else {
                    if (mIndicatorShow && i == mCurrentShowLine - 1) {
                        mTextPaint.setColor(mCurrentShowColor);
                    } else {
                        mTextPaint.setColor(mDefaultColor);
                    }
                }

                if (y > getMeasuredHeight() - mShaderWidth || y < mShaderWidth) {
                    //控制透明度
                    if (y < mShaderWidth) {
                        mTextPaint.setAlpha(26 + (int) (23000.0f * y / mShaderWidth * 0.01f));
                    } else {
                        mTextPaint.setAlpha(26 + (int) (23000.0f * (getMeasuredHeight() - y) / mShaderWidth * 0.01f));
                    }
                } else {
                    /*0-255 0完全透明，255完全显示*/
                    mTextPaint.setAlpha(255);
                }
                canvas.drawText(mLyricInfo.songLines.get(i).content, x, y, mTextPaint);
            }
        } else {
            mTextPaint.setColor(mHintColor);
            canvas.drawText(mDefaultHint, getMeasuredWidth() / 2, (getMeasuredHeight() + mLineHeight - 6) / 2, mTextPaint);
        }

        /*滑动提示部分内容绘制*/
//        if (mIndicatorShow && scrollable()) {
//
//        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_CANCEL:
//                actionCancel(event);
                break;

            case MotionEvent.ACTION_DOWN:
                actionDown(event);
                break;


        }

        return super.onTouchEvent(event);
    }

    private void actionDown(MotionEvent event) {


    }


    Handler postman = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_PLAYER_HIDE:
                    sendEmptyMessageDelayed(MSG_PLAYER_SLIDE, 1200);
                    mIndicatorShow = false;
                    invalidateView();
                    break;

                case MSG_PLAYER_SLIDE:
                    smoothScrollTo(measureCurrentScrollY(mCurrentPlayLine));
                    break;

            }
        }
    };

    /**
     * 从当前位置滑动到指定位置
     *
     * @param toY 指定纵坐标位置
     */
    private void smoothScrollTo(float toY) {
        final ValueAnimator animator = ValueAnimator.ofFloat(mScrollY, toY);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if (mUserTouch) {
                    animator.cancel();
                    return;
                }
                mScrollY = (float) animation.getAnimatedValue();
                invalidateView();
            }
        });

        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                mSliding = true;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mSliding = false;
                //TODO measureCurrentLine()
                invalidateView();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

    }

    /**
     * 输入当前显示行号来测量当前scroll的y坐标
     *
     * @param line 当前指定行号
     * @return
     */
    private float measureCurrentScrollY(int line) {
        return (line - 1) * mLineHeight;
    }

    /**
     * 设置歌词文件
     *
     * @param file        歌词文件
     * @param charsetName 解析字符集
     */
    public void setLyricFile(File file, String charsetName) {
        if (file != null && file.exists()) {
            try {
                setupLyricResource(new FileInputStream(file), charsetName);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            mDefaultHint = "暂无歌词";
            invalidateView();
        }
    }

    private void setupLyricResource(FileInputStream inputStream, String charsetName) {
        if (inputStream != null) {
            try {
                LyricInfo lyricInfo = new LyricInfo();
                lyricInfo.songLines = new ArrayList<>();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, charsetName);
                BufferedReader reader = new BufferedReader(inputStreamReader);
                String line = null;
                while ((line = reader.readLine()) != null) {
                    analyzeLyric(lyricInfo, line);
                }

                reader.close();
                inputStream.close();
                inputStreamReader.close();

                mLyricInfo = lyricInfo;
                mLineCount = mLyricInfo.songLines.size();
                invalidateView();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void analyzeLyric(LyricInfo lyricInfo, String line) {
        if (line == null) {
            return;
        }
        int index = line.lastIndexOf("]");
        if (line.startsWith("[offset:")) {
            lyricInfo.offset = Long.parseLong(line.substring(8, index).trim());
            return;
        }
        if (line.startsWith("[ti:")) {
            lyricInfo.song_title = line.substring(4, index).trim();
            return;
        }
        if (line.startsWith("[ar:")) {
            lyricInfo.song_artist = line.substring(4, index).trim();
            return;
        }
        if (line.startsWith("al:")) {
            lyricInfo.song_album = line.substring(4, index).trim();
            return;
        }
        if (index == 9 && line.trim().length() > 10) {
            LineInfo lineInfo = new LineInfo();
            lineInfo.content = line.substring(10, line.length());
            lineInfo.start = measureStartTimeMills(line.substring(0, 10));
            lyricInfo.songLines.add(lineInfo);
        }
    }

    private long measureStartTimeMills(String str) {
        long minute = Long.parseLong(str.substring(1, 3));
        long second = Long.parseLong(str.substring(4, 6));
        long millSecond = Long.parseLong(str.substring(7, 9));
        return millSecond + second * 1000 + minute * 60 * 1000;
    }


    /**
     * 刷新view
     */
    private void invalidateView() {
        if (Looper.getMainLooper() == Looper.myLooper()) {
            //当前线程是主线程，直接刷新
            invalidate();
        } else {
            //当前线程是非主线程，post刷新
            postInvalidate();
        }
    }
}
