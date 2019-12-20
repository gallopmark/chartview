package pony.xcode.chart;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.FloatRange;
import androidx.annotation.Nullable;

/*刻度圆盘*/
public class DiskScaleView extends View {

    private int mDiscSize;  //圆盘大小
    private int mStartAngle; //开始角度
    private int mSweepAngle; //总共扫过的角度
    private int mArrowWidth, mArrowHeight, mArrowSmallHeight;  //进度箭头的宽高
    private int mArrowColor; //箭头颜色
    private boolean mDisplayArrowByZero;
    private int mScaleWidth, mScaleHeight; //刻度的宽高
    private int mScaleColor;  //默认的刻度颜色
    private int mScaleSelectColor; //进度颜色
    private int mScaleRotateCount;  //刻度数量

    private int mRadius; //圆心
    private float mProgress;

    private boolean mAnimationEnabled; //是否显示动画
    private int mAnimationDuration; //动画持续时间
    private float mValueProgress;
    private ValueAnimator mValueAnimator;

    public DiskScaleView(Context context) {
        this(context, null);
    }

    public DiskScaleView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DiskScaleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.DiskScaleView);
        setStartAngle(ta.getInt(R.styleable.DiskScaleView_dsv_start_angle, 40));
        setSweepAngle(ta.getInt(R.styleable.DiskScaleView_dsv_sweep_angle, 280));
        setArrowWidth(ta.getDimensionPixelSize(R.styleable.DiskScaleView_dsv_arrow_width, ChartUtils.dp2px(context, 6f)));
        setArrowHeight(ta.getDimensionPixelSize(R.styleable.DiskScaleView_dsv_arrow_height, ChartUtils.dp2px(context, 10f)));
        setArrowSmallHeight(ta.getDimensionPixelSize(R.styleable.DiskScaleView_dsv_arrow_small_height, ChartUtils.dp2px(context, 4f)));
        setArrowColor(ta.getColor(R.styleable.DiskScaleView_dsv_arrow_color, Color.parseColor("#FFE400")));
        setDisplayArrowByZero(ta.getBoolean(R.styleable.DiskScaleView_dsv_arrow_display_zero, true));
        setScaleWidth(ta.getDimensionPixelSize(R.styleable.DiskScaleView_dsv_scale_width, ChartUtils.dp2px(context, 3f)));
        setScaleHeight(ta.getDimensionPixelSize(R.styleable.DiskScaleView_dsv_scale_height, ChartUtils.dp2px(context, 8f)));
        setScaleColor(ta.getColor(R.styleable.DiskScaleView_dsv_scale_color, Color.parseColor("#66FFFFFF")));
        setScaleSelectColor(ta.getColor(R.styleable.DiskScaleView_dsv_scale_color_selected, Color.parseColor("#00FF00")));
        setScaleRotateCount(ta.getInt(R.styleable.DiskScaleView_dsv_scale_rotate_count, 70));
        setAnimationEnabled(ta.getBoolean(R.styleable.DiskScaleView_dsv_animation_enabled, true));
        setAnimationDuration(ta.getInt(R.styleable.DiskScaleView_dsv_animation_duration, 2000));
        ta.recycle();
    }

    public int getStartAngle() {
        return mStartAngle;
    }

    public void setStartAngle(int startAngle) {
        this.mStartAngle = startAngle;
    }

    public int getSweepAngle() {
        return mSweepAngle;
    }

    public void setSweepAngle(int sweepAngle) {
        if (sweepAngle > 360) {  //最大角度不超过360
            sweepAngle = 360;
        }
        this.mSweepAngle = sweepAngle;
    }

    public int getArrowWidth() {
        return mArrowWidth;
    }

    public void setArrowWidth(int arrowWidth) {
        this.mArrowWidth = arrowWidth;
    }

    public int getArrowHeight() {
        return mArrowHeight;
    }

    public void setArrowHeight(int arrowHeight) {
        this.mArrowHeight = arrowHeight;
    }

    public int getArrowSmallHeight() {
        return mArrowSmallHeight;
    }

    public void setArrowSmallHeight(int arrowSmallHeight) {
        this.mArrowSmallHeight = arrowSmallHeight;
    }

    public int getArrowColor() {
        return mArrowColor;
    }

    public void setArrowColor(int arrowColor) {
        this.mArrowColor = arrowColor;
    }

    public void setDisplayArrowByZero(boolean isDisplay) {
        this.mDisplayArrowByZero = isDisplay;
    }

    public int getScaleWidth() {
        return mScaleWidth;
    }

    public void setScaleWidth(int scaleWidth) {
        this.mScaleWidth = scaleWidth;
    }

    public int getScaleHeight() {
        return mScaleHeight;
    }

    public void setScaleHeight(int scaleHeight) {
        this.mScaleHeight = scaleHeight;
    }

    public int getScaleColor() {
        return mScaleColor;
    }

    public void setScaleColor(int scaleColor) {
        this.mScaleColor = scaleColor;
    }

    public int getScaleSelectColor() {
        return mScaleSelectColor;
    }

    public void setScaleSelectColor(int scaleSelectColor) {
        this.mScaleSelectColor = scaleSelectColor;
    }

    public int getScaleRotateCount() {
        return mScaleRotateCount;
    }

    public void setScaleRotateCount(int scaleRotateCount) {
        this.mScaleRotateCount = scaleRotateCount;
    }

    public boolean isAnimationEnabled() {
        return mAnimationEnabled;
    }

    public void setAnimationEnabled(boolean animationEnabled) {
        this.mAnimationEnabled = animationEnabled;
    }

    public int getAnimationDuration() {
        return mAnimationDuration;
    }

    public void setAnimationDuration(int animationDuration) {
        this.mAnimationDuration = animationDuration;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mDiscSize = Math.max(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.getSize(heightMeasureSpec));
        mRadius = mDiscSize / 2 - mArrowHeight;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int rotateAngle = mSweepAngle / mScaleRotateCount;
        drawScaleBackground(canvas, rotateAngle);
        drawScaleProgress(canvas, rotateAngle);
    }

    private void drawScaleBackground(Canvas canvas, int rotateAngle) {
        canvas.save();
        canvas.translate(mDiscSize / 2f, mDiscSize / 2f);
        canvas.rotate(mStartAngle);
        Paint paint = newScalePaint(mScaleColor);
        canvas.drawLine(0, mRadius, 0, mRadius - mScaleHeight, paint);
        for (int i = 0; mSweepAngle >= 360 ? i <= mScaleRotateCount : i < mScaleRotateCount; i++) {
            canvas.rotate(rotateAngle);
            canvas.drawLine(0, mRadius, 0, mRadius - mScaleHeight, paint);
        }
        canvas.restore();
    }

    private void drawScaleProgress(Canvas canvas, int rotateAngle) {
        if ((!mDisplayArrowByZero && mProgress <= 0) || mProgress > 1) return;
        canvas.save();
        canvas.translate(mDiscSize / 2f, mDiscSize / 2f);
        canvas.rotate(mStartAngle);
        Paint paint = newScalePaint(mScaleSelectColor);
        canvas.drawLine(0, mRadius, 0, mRadius - mScaleHeight, paint);
        float rotateProgressNum = mProgress * mScaleRotateCount;
        for (int i = 0, size = (int) (rotateProgressNum * mValueProgress); mSweepAngle >= 360 ? i <= size : i < size; i++) {
            canvas.rotate(rotateAngle);
            canvas.drawLine(0, mRadius, 0, mRadius - mScaleHeight, paint);
        }
        //画箭头
        Path path = new Path();
        path.moveTo(mArrowWidth / 2f, mRadius + mArrowHeight);
        path.lineTo(-mArrowWidth / 2f, mRadius + mArrowHeight);
        path.lineTo(-mArrowWidth / 2f, mRadius + mArrowSmallHeight);
        path.lineTo(0, mRadius);
        path.lineTo(mArrowWidth / 2f, mRadius + mArrowSmallHeight);
        path.close();
        canvas.drawPath(path, newArrowPaint());
        canvas.restore();
    }

    private Paint newScalePaint(int color) {
        Paint paint = new Paint();
        paint.setColor(color);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(mScaleWidth);
        paint.setStrokeCap(Paint.Cap.ROUND);
        return paint;
    }

    private Paint newArrowPaint() {
        Paint paint = new Paint();
        paint.setColor(mArrowColor);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
        return paint;
    }

    public void setProgress(@FloatRange(from = 0.0f, to = 1f) float progress) {
        this.mProgress = progress;
        if (mAnimationEnabled) {
            animInvalidate();
        } else {
            invalidate();
        }
    }

    private void animInvalidate() {
        if (mValueAnimator != null) {
            mValueAnimator.end();
            mValueAnimator.cancel();
            mValueAnimator = null;
        }
        mValueAnimator = ValueAnimator.ofFloat(0f, 1f);
        mValueAnimator.setDuration(mAnimationDuration);
        mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                mValueProgress = (float) animator.getAnimatedValue();
                postInvalidate();
            }
        });
        mValueAnimator.start();
    }

    public class FluentInitializer {
        public FluentInitializer startAngle(int startAngle) {
            setStartAngle(startAngle);
            return this;
        }

        public FluentInitializer sweepAngle(int sweepAngle) {
            setSweepAngle(sweepAngle);
            return this;
        }

        public FluentInitializer arrowWidth(int arrowWidth) {
            setArrowWidth(arrowWidth);
            return this;
        }

        public FluentInitializer arrowHeight(int arrowHeight) {
            setArrowHeight(arrowHeight);
            return this;
        }

        public FluentInitializer arrowSmallHeight(int arrowSmallHeight) {
            setArrowSmallHeight(arrowSmallHeight);
            return this;
        }

        public FluentInitializer arrowColor(int arrowColor) {
            setArrowColor(arrowColor);
            return this;
        }

        public FluentInitializer scaleWidth(int scaleWidth) {
            setScaleWidth(scaleWidth);
            return this;
        }

        public FluentInitializer scaleHeight(int scaleHeight) {
            setScaleHeight(scaleHeight);
            return this;
        }

        public FluentInitializer scaleColor(int scaleColor) {
            setScaleColor(scaleColor);
            return this;
        }

        public FluentInitializer scaleSelectColor(int scaleSelectColor) {
            setScaleSelectColor(scaleSelectColor);
            return this;
        }

        public FluentInitializer scaleRotateCount(int scaleRotateCount) {
            setScaleRotateCount(scaleRotateCount);
            return this;
        }

        public FluentInitializer animationEnabled(boolean animationEnabled) {
            setAnimationEnabled(animationEnabled);
            return this;
        }

        public FluentInitializer animationDuration(int animationDuration) {
            setAnimationDuration(animationDuration);
            return this;
        }

        public void start(@FloatRange(from = 0.0f, to = 1.0f) float progress) {
            setProgress(progress);
        }
    }
}
