package com.holike.demo.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

import com.holike.demo.R;


import java.util.List;

/**
 * Created by gallop on 2019/9/11.
 * Copyright holike possess 2019.
 */
public class PieChartView2 extends View {

    private static final String TAG = "PieChartView";

    public static class PieModel {
        float startAngle;    // 开始绘制的角度

        float sweepAngle;    // 扫过的角度

        int color;        // 显示的颜色

        float percent;    // 所占百分比

        boolean selected;    // true为选中

        public PieModel(int color, float percent) {
            this.color = color;
            this.percent = percent;
        }

        public PieModel(int color, float percent, boolean selected) {
            this.color = color;
            this.percent = percent;
            this.selected = selected;
        }
    }

    private Paint mChartPaint;

    private Paint mCirclePaint;                    // 中心圆

    private RectF mRectF;

    private int mLineWidth = 100;
    private int mRadius; //圆半径

    private List<PieModel> mPieModelList;

    private float mAnimAngle;

    private RectF mSelectedRectF = new RectF();

    public PieChartView2(Context context) {
        this(context, null);
    }

    public PieChartView2(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PieChartView2(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        obtainAttrs(context, attrs);
        init();
    }

    private void obtainAttrs(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.PieChartView2);
        mLineWidth = ta.getDimensionPixelSize(R.styleable.PieChartView2_pcv_ring_width, dp2px());
        ta.recycle();
    }

    private int dp2px() {
        return (int) (0.5f + 10 * Resources.getSystem().getDisplayMetrics().density);
    }

    private void init() {
        mChartPaint = new Paint();
        mChartPaint.setAntiAlias(true);
        mChartPaint.setDither(true);
        mChartPaint.setStrokeWidth(mLineWidth);
        mChartPaint.setStyle(Paint.Style.FILL);

        mCirclePaint = new Paint();
        mCirclePaint.setAntiAlias(true);
        mCirclePaint.setStyle(Paint.Style.FILL);
        mCirclePaint.setColor(Color.WHITE);
    }

    @SuppressWarnings("SuspiciousNameCombination")
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }

    /*优先onDraw执行*/
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mRadius = (w - mLineWidth * 4) / 2;
        float top = mLineWidth + 0f;
        mRectF = new RectF(mLineWidth, top, w - mLineWidth, w - mLineWidth);
        mSelectedRectF.set(mRectF);
        mSelectedRectF.inset(-30, -30);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Log.e(TAG, "onDraw...");
        if (mPieModelList == null || mPieModelList.isEmpty()) {
            return;
        }
        for (int i = 0; i < mPieModelList.size(); i++) {
            if (mPieModelList.get(i).percent > 0) {
                if (mAnimAngle >= mPieModelList.get(i).startAngle &&
                        mAnimAngle <= (mPieModelList.get(i).startAngle + mPieModelList.get(i).sweepAngle)) {
                    drawColor(canvas, mPieModelList.get(i).color, mPieModelList.get(i).startAngle, mAnimAngle - mPieModelList.get(i).startAngle);
                } else if (mAnimAngle >= (mPieModelList.get(i).startAngle + mPieModelList.get(i).sweepAngle)) {
                    drawColor(canvas, mPieModelList.get(i).color, mPieModelList.get(i).startAngle, mPieModelList.get(i).sweepAngle);
                }
                if (mPieModelList.get(i).selected) {
                    drawSelectedView(canvas, mPieModelList.get(i).color, mPieModelList.get(i).startAngle, mPieModelList.get(i).sweepAngle);
                }
            }
        }
        canvas.drawCircle(getMeasuredWidth() / 2f, getMeasuredWidth() / 2f, mRadius, mCirclePaint);
    }

    private void drawColor(Canvas canvas, int color, float startAngle, float sweepAngle) {
        mChartPaint.setColor(color);
        mChartPaint.setAlpha(255);
        canvas.drawArc(mRectF, startAngle, sweepAngle, true, mChartPaint);
    }

    private void drawSelectedView(Canvas canvas, int color, float startAngle, float sweepAngle) {
        mChartPaint.setColor(color);
        mChartPaint.setAlpha(150);
        canvas.drawArc(mSelectedRectF, startAngle, sweepAngle, true, mChartPaint);
    }

    public void startAnim() {
        final ValueAnimator valueAnimator = ValueAnimator.ofFloat(0f, 360f);
        valueAnimator.setDuration(1000);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mAnimAngle = (float) animation.getAnimatedValue();
                invalidate();
            }
        });
        valueAnimator.start();
    }

    public void setData(List<PieModel> pieModelList) {
        this.mPieModelList = pieModelList;
        for (int i = 0; i < mPieModelList.size(); i++) {
            PieModel model = mPieModelList.get(i);
            if (i == 0) {
                model.startAngle = 1;
            } else {
                model.startAngle = mPieModelList.get(i - 1).startAngle + mPieModelList.get(i - 1).sweepAngle + 1;
            }
            model.sweepAngle = (model.percent * 360);
        }
        startAnim();
    }
}
