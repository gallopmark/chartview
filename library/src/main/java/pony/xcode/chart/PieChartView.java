package pony.xcode.chart;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import pony.xcode.chart.data.PieChartData;

/*饼状图*/
public class PieChartView extends View {

    private Context mContext;
    private int mWidth;
    private int mHeight;
    private float mStartAngle; //开始角度
    //outer circle
    private int mOuterCircleDiameter; //外圆直径
    private int mOuterCircleColor; //外圆颜色
    //inner circle
    private int mInnerCircleColor; //内圆颜色
    private int mRingWidth; //圆环宽度
    //divider
    private boolean mDividerEnabled;
    private int mDividerWidth; //分段宽度
    private int mDividerColor; //分段颜色
    //animation
    private boolean mDisplayAnimation; //是否动画展示
    private int mAnimationDuration; //动画持续时间
    private ValueAnimator mAnimator;

    private List<PieChartData> mPieChartDataList;
    private static final int TOTAL_DEGREE = 360;

    public PieChartView(Context context) {
        this(context, null);
    }

    public PieChartView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PieChartView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        obtainValue(attrs);
    }

    private void obtainValue(AttributeSet attrs) {
        TypedArray ta = mContext.obtainStyledAttributes(attrs, R.styleable.PieChartView);
        mStartAngle = ta.getFloat(R.styleable.PieChartView_pcv_start_angle, 270);
        mOuterCircleDiameter = ta.getDimensionPixelSize(R.styleable.PieChartView_pcv_diameter, ChartUtils.dp2px(mContext, 240));
        mOuterCircleColor = ta.getColor(R.styleable.PieChartView_pcv_outer_color, Color.parseColor("#dadada"));
        mRingWidth = ta.getDimensionPixelSize(R.styleable.PieChartView_pcv_ring_width, ChartUtils.dp2px(mContext, 20));
        mInnerCircleColor = ta.getColor(R.styleable.PieChartView_pcv_inner_color, Color.parseColor("#ffffff"));
        mDividerEnabled = ta.getBoolean(R.styleable.PieChartView_pcv_divider_enabled, false);
        mDividerWidth = ta.getDimensionPixelSize(R.styleable.PieChartView_pcv_divider_width, ChartUtils.dp2px(mContext, 1));
        mDividerColor = ta.getColor(R.styleable.PieChartView_pcv_divider_color, Color.parseColor("#ffffff"));
        mDisplayAnimation = ta.getBoolean(R.styleable.PieChartView_pcv_display_animation, true);
        mAnimationDuration = ta.getInt(R.styleable.PieChartView_pcv_animation_duration, 800);
        ta.recycle();
    }

    public void setPieChartDataList(@Nullable List<PieChartData> dataList) {
        if (dataList != null) {
            this.mPieChartDataList = new ArrayList<>(dataList);
            calculation();
            if (mDisplayAnimation) {
                startAnimationUpdate();
            } else {
                invalidate();
            }
        }
    }

    private void startAnimationUpdate() {
        if (mAnimator != null) {
            mAnimator.end();
            mAnimator.cancel();
            mAnimator = null;
        }
        mAnimator = ValueAnimator.ofFloat(0, 1f);
        mAnimator.setDuration(mAnimationDuration);
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float progress = (float) valueAnimator.getAnimatedValue();
                for (PieChartData data : mPieChartDataList) {
                    data.setPieSweep(data.getPercent() * TOTAL_DEGREE * progress);
                }
                postInvalidate();
            }
        });
        mAnimator.start();
    }

    private void calculation() {
        float pieStart = mStartAngle;
        float total = getTotalCount();
        for (PieChartData data : mPieChartDataList) {
            final float percent = (data.getValue() / total);
            data.setPercent(percent);
            data.setPieStart(pieStart);
            final float pieSweep = percent * TOTAL_DEGREE;
            data.setPieSweep(pieSweep);
            pieStart += pieSweep;
        }
    }

    private float getTotalCount() {
        float total = 0;
        for (PieChartData data : mPieChartDataList) {
            total += data.getValue();
        }
        return total;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        final int screenWidth = ChartUtils.getScreenWidth(mContext);
        int measureSize = mOuterCircleDiameter;
        if (mOuterCircleDiameter > screenWidth) {  //如果外圆直径超过屏幕宽，则直径设为屏幕宽度
            measureSize = screenWidth;
            setMeasuredDimension(measureSize, measureSize);
        } else {
            setMeasuredDimension(widthMeasureSpec, measureSize);
        }
        mWidth = measureSize;
        mHeight = getMeasuredHeight();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.translate((getMeasuredWidth() - mWidth) / 2f, 0);
        Paint paint = getPiePaint();
        final float cx = mWidth / 2f;
        final float cy = mHeight / 2f;
        if (mPieChartDataList != null && !mPieChartDataList.isEmpty() && getTotalCount() > 0) {
            drawArc(canvas, paint);
        } else {
            paint.setColor(mOuterCircleColor);
            canvas.drawCircle(cx, cy, mWidth / 2f, paint);
        }
        drawInnerCircle(canvas, cx, cy, paint);
    }

    @SuppressWarnings("SuspiciousNameCombination")
    private void drawArc(Canvas canvas, Paint paint) {
        RectF rectF = new RectF(mDividerWidth, mDividerWidth, mWidth - mDividerWidth, mHeight - mDividerWidth);
        for (PieChartData data : mPieChartDataList) {
            paint.setColor(data.getColorId());
            canvas.drawArc(rectF, data.getPieStart(), data.getPieSweep(), true, paint);
        }
        drawDivider(canvas, rectF, getLinePaint());
    }

    //画分割线
    private void drawDivider(Canvas canvas, RectF rectF, Paint paint) {
        if (mDividerEnabled && mDividerWidth > 0) {
            for (PieChartData data : mPieChartDataList) {
                canvas.drawArc(rectF, data.getPieStart(), data.getPieSweep(), true, paint);
            }
        }
    }

    /*画内圆*/
    private void drawInnerCircle(Canvas canvas, float cx, float cy, Paint paint) {
        paint.setColor(mInnerCircleColor);
        canvas.drawCircle(cx, cy, (mWidth - mRingWidth * 2) / 2, paint);
    }

    private Paint getPiePaint() {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
        return paint;
    }

    private Paint getLinePaint() {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(mDividerColor);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(mDividerWidth);
        return paint;
    }

    public FluentInitializer withData(@Nullable List<PieChartData> dataList) {
        return new FluentInitializer(dataList);
    }

    public class FluentInitializer {
        List<PieChartData> mData;

        FluentInitializer(@Nullable List<PieChartData> dataList) {
            this.mData = dataList;
        }

        public FluentInitializer startAngle(float startAngle) {
            mStartAngle = startAngle;
            return this;
        }

        public FluentInitializer diameter(int diameter) {
            mOuterCircleDiameter = diameter;
            return this;
        }

        public FluentInitializer ringWidth(int width) {
            mRingWidth = width;
            return this;
        }

        public FluentInitializer outerColor(@ColorInt int color) {
            mOuterCircleColor = color;
            return this;
        }

        public FluentInitializer innerColor(@ColorInt int color) {
            mInnerCircleColor = color;
            return this;
        }

        public FluentInitializer dividerWidth(int dividerWidth) {
            mDividerWidth = dividerWidth;
            return this;
        }

        public FluentInitializer dividerColor(@ColorInt int color) {
            mDividerColor = color;
            return this;
        }

        public FluentInitializer displayAnimation(boolean isDisplayAnim) {
            mDisplayAnimation = isDisplayAnim;
            return this;
        }

        public FluentInitializer animationDuration(int duration) {
            mAnimationDuration = duration;
            return this;
        }

        public void start() {
            setPieChartDataList(mData);
        }
    }
}
