package pony.xcode.chart;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;

import androidx.annotation.ArrayRes;
import androidx.annotation.ColorInt;
import androidx.annotation.IntRange;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import pony.xcode.chart.data.BarChartData;

/**
 * 条形图（柱状图）
 */
public class BarChartView extends AbsChartView {

    //mode
    public static final int MODE_SCROLLABLE = 0;
    public static final int MODE_FIXED = 1;
    private int mMode;
    private int mMinCount;
    //margin
    private int mLeftMargin; //左边距
    private int mRightMargin; //右边距
    private int mTopMargin;
    private int mBottomMargin;

    private int mOldTopSpace;
    //scale num
    private int mYScaleNum;
    //bar
    private int mBarColor; //条形背景颜色或图片
    private int mBarWidth; //条形宽度
    //item margin
    private int mDividerWidth; //之间的间距
    private boolean mIncludeLeftEdge;
    private boolean mIncludeRightEdge;
    //axis margin
    private int mYAxisMargin; //条形图y轴间距
    private int mXAxisMargin; //条形图x轴间距

    private int mBottomSpace; //底部腾出写x轴文字的空间
    //text x
    private TextPaint mXAxisTextPaint;
    private int mXAxisTextColor; //x轴文字颜色
    private int mXAxisTextSize; //x轴文字大小
    private int mXAxisTextLineCount; //x轴文本行数（控制sub文字显示的位置）
    //text x sub
    private TextPaint mSubXAxisTextPaint;
    private int mSubXAxisTextColor;
    private int mSubXAxisTextSize;
    private int mSubXAxisMargin;
    //text y
    private TextPaint mYAxisTextPaint;
    private int mYAxisTextColor; //y轴文字颜色
    private int mYAxisTextSize; //y轴文字大小
    //dashed
    private boolean mDashedEnabled; //是否显示虚线
    private int mDashedColor; //虚线颜色
    private DashPathEffect mDashPathEffect; //虚线效果
    //value
    private boolean mBarValueEnabled; //是否在柱状图上展示数值
    private boolean mBarValueAsInt; //数值是否强转为int
    private int mBarValueMargin; //间距
    private int mBarValueTextColor; //数值文字颜色
    private int mBarValueTextSize; //数值文字大小
    private Typeface mBarValueTypeface;

    private List<BarChartData> mBarChartDataList;
    private String[] mXAxisTextArray;
    private String[] mSubXAxisTextArray;
    private String mUnit; //单位(%等)
    private boolean mBarValueUnitEdge; //条形图上数值是否也显示单位
    private int mMaxGradient;
    private float mProgress = 1f;

    //animation
    private boolean mDisplayAnimation; //是否动画展示
    private long mAnimationDuration; //动画持续时间
    private ValueAnimator mValueAnimation;

    public BarChartView(Context context) {
        this(context, null);
    }

    public BarChartView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BarChartView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        obtainValues(attrs);
    }

    private void obtainValues(AttributeSet attrs) {
        TypedArray ta = mContext.obtainStyledAttributes(attrs, R.styleable.BarChartView);
        mMode = ta.getInt(R.styleable.BarChartView_bcv_barMode, MODE_SCROLLABLE);
        mMinCount = ta.getInt(R.styleable.BarChartView_bcv_minCount, 5);
        mLeftMargin = ta.getDimensionPixelSize(R.styleable.BarChartView_bcv_leftMargin, ChartUtils.dp2px(mContext, 16));
        mRightMargin = ta.getDimensionPixelSize(R.styleable.BarChartView_bcv_rightMargin, ChartUtils.dp2px(mContext, 16));
        mTopMargin = ta.getDimensionPixelSize(R.styleable.BarChartView_bcv_topMargin, 0);
        mOldTopSpace = mTopMargin;
        mBottomMargin = ta.getDimensionPixelSize(R.styleable.BarChartView_bcv_bottomMargin, 0);
        mYScaleNum = ta.getDimensionPixelSize(R.styleable.BarChartView_bcv_yScaleNum, 5);
        mBarColor = ta.getColor(R.styleable.BarChartView_bcv_barColor, Color.parseColor("#007afe"));
        mBarWidth = ta.getDimensionPixelSize(R.styleable.BarChartView_bcv_barWidth, ChartUtils.dp2px(mContext, 48));
        mDividerWidth = ta.getDimensionPixelSize(R.styleable.BarChartView_bcv_divider_width, ChartUtils.dp2px(mContext, 24));
        mIncludeLeftEdge = ta.getBoolean(R.styleable.BarChartView_bcv_include_leftEdge, false);
        mIncludeRightEdge = ta.getBoolean(R.styleable.BarChartView_bcv_include_rightEdge, false);
        mYAxisMargin = ta.getDimensionPixelSize(R.styleable.BarChartView_bcv_yAxisMargin, ChartUtils.dp2px(mContext, 16));
        mXAxisMargin = ta.getDimensionPixelSize(R.styleable.BarChartView_bcv_xAxisMargin, ChartUtils.dp2px(mContext, 16));
        mBottomSpace = ta.getDimensionPixelSize(R.styleable.BarChartView_bcv_bottom_space, ChartUtils.dp2px(mContext, 72));
        mXAxisTextSize = ta.getDimensionPixelSize(R.styleable.BarChartView_bcv_xAxis_textSize, ChartUtils.sp2px(mContext, 12));
        mXAxisTextLineCount = ta.getInt(R.styleable.BarChartView_bcv_xAxis_textLineCount, 1);
        mXAxisTextColor = ta.getColor(R.styleable.BarChartView_bcv_xAxis_textColor, Color.parseColor("#ffffff"));
        mSubXAxisTextSize = ta.getDimensionPixelSize(R.styleable.BarChartView_bcv_subXAxis_textSize, ChartUtils.sp2px(mContext, 12));
        mSubXAxisTextColor = ta.getColor(R.styleable.BarChartView_bcv_subXAxis_textColor, Color.parseColor("#ffffff"));
        mSubXAxisMargin = ta.getDimensionPixelSize(R.styleable.BarChartView_bvb_subXAxis_textMargin, ChartUtils.dp2px(mContext, 12));
        mYAxisTextSize = ta.getDimensionPixelSize(R.styleable.BarChartView_bcv_yAxis_textSize, ChartUtils.sp2px(mContext, 12));
        mYAxisTextColor = ta.getColor(R.styleable.BarChartView_bcv_yAxis_textColor, Color.parseColor("#ffffff"));
        mDashedEnabled = ta.getBoolean(R.styleable.BarChartView_bcv_dashed_enabled, true);
        mDashedColor = ta.getColor(R.styleable.BarChartView_bcv_dashed_color, Color.parseColor("#66ffffff"));
        mBarValueEnabled = ta.getBoolean(R.styleable.BarChartView_bcv_barValue_enabled, true);
        mBarValueUnitEdge = ta.getBoolean(R.styleable.BarChartView_bcv_barValueUnitEdge, true);
        mBarValueMargin = ta.getDimensionPixelSize(R.styleable.BarChartView_bcv_barValue_margin, ChartUtils.dp2px(mContext, 4));
        mBarValueTextSize = ta.getDimensionPixelSize(R.styleable.BarChartView_bcv_barValue_textSize, ChartUtils.sp2px(mContext, 12));
        mBarValueTextColor = ta.getColor(R.styleable.BarChartView_bcv_barValue_textColor, Color.parseColor("#ffffff"));
        mDisplayAnimation = ta.getBoolean(R.styleable.BarChartView_bcv_display_animation, true);
        mAnimationDuration = ta.getInt(R.styleable.BarChartView_bcv_animation_duration, 1000);
        //xAxis text array
        final int textArrayResId = ta.getResourceId(R.styleable.BarChartView_bcv_xAxis_textArray, -1);
        if (textArrayResId != -1) {
            mXAxisTextArray = mContext.getResources().getStringArray(textArrayResId);
        }
        int subTextArrayResId = ta.getResourceId(R.styleable.BarChartView_bcv_subXAxis_textArray, -1);
        if (subTextArrayResId != -1) {
            mSubXAxisTextArray = mContext.getResources().getStringArray(subTextArrayResId);
        }
        ta.recycle();
    }


    public void setData(@Nullable List<BarChartData> dataList) {
        setData(dataList, 0);
    }

    public void setData(@Nullable List<BarChartData> dataList, int maxGradient) {
        if (dataList == null) return;
        mBarChartDataList = new ArrayList<>(dataList);
        int maxValue = getMaxValueFromData();
        if (maxGradient == 0) {
            mMaxGradient = ChartUtils.getMaxGraded(maxValue);
        } else {
            mMaxGradient = maxGradient;
        }
        resetInitialStatus();
        validateAndUpdate();
    }

    private void validateAndUpdate() {
        initPaint();
        if (mDisplayAnimation) {
            if (mValueAnimation != null) {
                mValueAnimation.end();
                mValueAnimation.cancel();
                mValueAnimation = null;
            }
            mValueAnimation = ValueAnimator.ofFloat(0f, 1f);
            mValueAnimation.setDuration(mAnimationDuration);
            mValueAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    mProgress = (float) valueAnimator.getAnimatedValue();
                    postInvalidate();
                }
            });
            mValueAnimation.start();
        } else {
            invalidate();
        }
    }

    /*获取最大值*/
    private int getMaxValueFromData() {
        int max = 0;
        for (BarChartData data : mBarChartDataList) {
            if (data.getValue() > max) {
                max = (int) data.getValue();
            }
        }
        return max;
    }

    private void initPaint() {
        mYAxisTextPaint = getYAxisTextPaint();
        mXAxisTextPaint = getXAxisTextPaint();
        mSubXAxisTextPaint = getSubXAxisTextPaint();
    }

    private TextPaint getYAxisTextPaint() {
        TextPaint paint = new TextPaint();
        paint.setAntiAlias(true);
        paint.setTextSize(mYAxisTextSize);
        paint.setColor(mYAxisTextColor);
        return paint;
    }

    private TextPaint getXAxisTextPaint() {
        TextPaint paint = new TextPaint();
        paint.setAntiAlias(true);
        paint.setTextSize(mXAxisTextSize);
        paint.setColor(mXAxisTextColor);
        return paint;
    }

    private TextPaint getSubXAxisTextPaint() {
        TextPaint paint = new TextPaint();
        paint.setAntiAlias(true);
        paint.setTextSize(mSubXAxisTextSize);
        paint.setColor(mSubXAxisTextColor);
        return paint;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (mTopMargin == mOldTopSpace) {
            mTopMargin += getYSpace();
        }
    }

    private void initBarWidth() {
        if (mMode == MODE_FIXED) {
            mNeedWidth = ChartUtils.getScreenWidth(mContext);
            int count = getDataSize() > mMinCount ? getDataSize() : mMinCount;
            if (getEdgeSize() > 0)
                mDividerWidth = (mNeedWidth - (mLeftMargin + getTextMaxWidth() + mYAxisMargin + mBarWidth * count)) / getEdgeSize();
        } else {
            /*画条形图所需的总宽度*/
            mNeedWidth = mLeftMargin + getTextMaxWidth() + mYAxisMargin + getDataSize() * mBarWidth + getEdgeSize() * mDividerWidth + mRightMargin;
        }
    }

    private int getDataSize() {
        if (mXAxisTextArray != null && mXAxisTextArray.length > 0) {
            return mXAxisTextArray.length;
        } else {
            return mBarChartDataList.size();
        }
    }

    private int getEdgeSize() {
        int edgeSize;
        if (mXAxisTextArray != null && mXAxisTextArray.length > 0) {
            edgeSize = mXAxisTextArray.length - 1 <= 0 ? 0 : mXAxisTextArray.length - 1;
        } else {
            edgeSize = mBarChartDataList.size() - 1 <= 0 ? 0 : mBarChartDataList.size() - 1;
        }
        if (mIncludeLeftEdge && mIncludeRightEdge) {
            edgeSize += 2;
        } else if (mIncludeLeftEdge || mIncludeRightEdge) {
            edgeSize += 1;
        }
        return edgeSize;
    }

    private int getTextMaxWidth() {
        return ChartUtils.getMaxGradientTextWidth(mMaxGradient, mYAxisTextPaint, mUnit);
    }

    private int getYSpace() {
        return (mHeight - mTopMargin - mBottomSpace - mBottomMargin) / mYScaleNum;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mBarChartDataList != null) {
            initBarWidth();
            int startDy = getStartDy();
            int ySpace = getYSpace();
            drawYAxis(canvas, startDy, ySpace);
            int startDx = getStartDx();
            if (mDashedEnabled) {
                drawDashedLine(canvas, startDx, startDy, ySpace);
            }
            startDx += mIncludeLeftEdge ? mDividerWidth : 0;
            drawXAxis(canvas, startDx, startDy);
            drawSubXAxisText(canvas, startDx, startDy);
            drawBar(canvas, startDx, startDy);
        }
    }

    /*写y轴文字*/
    private void drawYAxis(Canvas canvas, int startDy, int ySpace) {
        int average = mMaxGradient / mYScaleNum;
        String unit = getUnit();
        for (int i = 0; i <= mYScaleNum; i++) {
            String text = (average * i) + unit;
            canvas.drawText(text, mLeftMargin, startDy - i * ySpace, mYAxisTextPaint);
        }
    }

    private String getUnit() {
        return TextUtils.isEmpty(mUnit) ? "" : mUnit;
    }

    /*画虚线*/
    private void drawDashedLine(Canvas canvas, int startDx, int startDy, int ySpace) {
        Path path = new Path();
        Paint paint = getDashedLinePaint();
        for (int i = 0; i <= mYScaleNum; i++) {
            path.reset();
            float y = startDy - (i * ySpace);
            path.moveTo(startDx, y); //线起点
            path.lineTo(getEndX(), y); //线终点
            canvas.drawPath(path, paint); //开始画线
        }
    }

    private Paint getDashedLinePaint() {
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(mDashedColor);
        paint.setStrokeWidth(ChartUtils.dp2px(mContext, 0.5f));
        if (mDashPathEffect == null) {
            paint.setPathEffect(new DashPathEffect(new float[]{5, 5}, 0));
        } else {
            paint.setPathEffect(mDashPathEffect);
        }
        return paint;
    }

    /*写x轴文字*/
    private void drawXAxis(Canvas canvas, int startDx, int startDy) {
        //画x轴刻度
        if (mXAxisTextArray != null && mXAxisTextArray.length > 0) {
            for (int i = 0; i < mXAxisTextArray.length; i++) {
                drawXAxisText(canvas, mXAxisTextArray[i], startDx, startDy, i);
            }
        } else {
            for (int i = 0; i < mBarChartDataList.size(); i++) {
                drawXAxisText(canvas, mBarChartDataList.get(i).getXAxisText(), startDx, startDy, i);
            }
        }
    }

    private void drawXAxisText(Canvas canvas, String text, int startDx, int startDy, int index) {
        float x = startDx + (mDividerWidth + mBarWidth) * index;
        float y = startDy + mXAxisMargin;
        /* staticLayout支持换行，它既可以为文字设置宽度上限来让文字自动换行，也会在 \n 处主动换行。
         * width 是文字区域的宽度，文字到达这个宽度后就会自动换行；
         * align 是文字的对齐方向；
         * spacingmult 是行间距的倍数，通常情况下填 1 就好；
         * spacingadd 是行间距的额外增加值，通常情况下填 0 就好；
         * includeadd 是指是否在文字上下添加额外的空间，来避免某些过高的字符的绘制出现越界。
         * */
        String source = TextUtils.isEmpty(text) ? "" : text;
        StaticLayout staticLayout = new StaticLayout(source, mXAxisTextPaint, mBarWidth, Layout.Alignment.ALIGN_CENTER, 1f, 0, false);
        canvas.save();
        canvas.translate(x, y);
        staticLayout.draw(canvas);
        canvas.restore();
    }

    private void drawSubXAxisText(Canvas canvas, int startDx, int startDy) {
        if (mSubXAxisTextArray != null && mSubXAxisTextArray.length > 0) {
            for (int i = 0; i < mSubXAxisTextArray.length; i++) {
                drawSubXAxisText(canvas, mSubXAxisTextArray[i], startDx, startDy, i);
            }
        }
    }

    private void drawSubXAxisText(Canvas canvas, String text, int startDx, int startDy, int index) {
        float x = startDx + (mDividerWidth + mBarWidth) * index;
        float y = startDy + mXAxisMargin + mXAxisTextSize * mXAxisTextLineCount + mSubXAxisMargin;
        /* staticLayout支持换行，它既可以为文字设置宽度上限来让文字自动换行，也会在 \n 处主动换行。
         * width 是文字区域的宽度，文字到达这个宽度后就会自动换行；
         * align 是文字的对齐方向；
         * spacingmult 是行间距的倍数，通常情况下填 1 就好；
         * spacingadd 是行间距的额外增加值，通常情况下填 0 就好；
         * includeadd 是指是否在文字上下添加额外的空间，来避免某些过高的字符的绘制出现越界。
         * */
        String source = TextUtils.isEmpty(text) ? "" : text;
        StaticLayout staticLayout = new StaticLayout(source, mSubXAxisTextPaint, mBarWidth, Layout.Alignment.ALIGN_CENTER, 1f, 0, false);
        canvas.save();
        canvas.translate(x, y);
        staticLayout.draw(canvas);
        canvas.restore();
    }

    /*画条形图*/
    private void drawBar(Canvas canvas, int startDx, int startDy) {
        Paint paint = getBarPaint();
        for (int i = 0; i < mBarChartDataList.size(); i++) {
            final double value = mBarChartDataList.get(i).getValue();
            float left = startDx + (mBarWidth + mDividerWidth) * i;
            float top = getPointY(value);
            float right = left + mBarWidth;
            int colorId = mBarChartDataList.get(i).getBarColor();
            if (colorId != 0) {
                paint.setColor(colorId);
            }
            canvas.drawRect(left, top, right, startDy, paint);
        }
        drawBarValue(canvas, startDx);
    }

    private Paint getBarPaint() {
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);
        paint.setColor(mBarColor);
        return paint;
    }

    private void drawBarValue(Canvas canvas, int startDx) {
        if (mBarValueEnabled) {
            Paint paint = getBarValuePaint();
            String unit = mBarValueUnitEdge ? getUnit() : "";
            for (int i = 0; i < mBarChartDataList.size(); i++) {
                String valueText;
                String description = mBarChartDataList.get(i).getDescription();
                double value = mBarChartDataList.get(i).getValue();
                if (!TextUtils.isEmpty(description)) {
                    valueText = description;
                } else {
                    if (mBarValueAsInt) {
                        valueText = String.valueOf((int) value);
                    } else {
                        valueText = String.valueOf(ChartUtils.doubleValue(value));
                    }
                }
                valueText += unit;
                float x = startDx + (mDividerWidth + mBarWidth) * i;
                float y = getPointY(value) - mBarValueMargin;
                RectF rectF = new RectF(x, y, x + mBarWidth, 0);
                canvas.drawText(valueText, rectF.centerX(), y, paint);
            }
        }
    }

    private Paint getBarValuePaint() {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(mBarValueTextColor);
        paint.setTextSize(mBarValueTextSize);
        if (mBarValueTypeface != null) {
            paint.setTypeface(mBarValueTypeface);
        }
        paint.setTextAlign(Paint.Align.CENTER);
        return paint;
    }

    /*开始画y轴刻度的位置*/
    private int getStartDy() {
        return mHeight - mBottomSpace - mBottomMargin;
    }

    /*x轴开始写文字位置*/
    private int getStartDx() {
        return mLeftMargin + getTextMaxWidth() + mYAxisMargin;
    }

    /*数值对应的y坐标*/
    private float getPointY(double value) {
        return (float) (getStartDy() - (getStartDy() - mTopMargin) * (value / mMaxGradient) * mProgress);
    }

    /*图表高度*/
    @SuppressWarnings("unused")
    public int getChartHeight() {
        return mHeight - mTopMargin - mBottomSpace - mBottomMargin;
    }

    /*x轴最右端*/
    private int getEndX() {
        final int screenWidth = mContext.getResources().getDisplayMetrics().widthPixels;
        if (mNeedWidth < screenWidth) {
            return screenWidth - mRightMargin;
        }
        return mNeedWidth - mRightMargin;
    }

    public FluentInitializer withData(@Nullable List<BarChartData> data) {
        return withData(data, null);
    }

    public FluentInitializer withData(@Nullable List<BarChartData> data, @ArrayRes int arrayId) {
        return withData(data, mContext.getResources().getStringArray(arrayId));
    }

    public FluentInitializer withData(@Nullable List<BarChartData> data, @Nullable String[] xAxisTextArray) {
        return withData(data, xAxisTextArray, null);
    }

    public FluentInitializer withData(@Nullable List<BarChartData> data, @ArrayRes int arrayId, String unit) {
        return withData(data, mContext.getResources().getStringArray(arrayId), unit);
    }

    /**
     * @param data           数据源
     * @param xAxisTextArray x轴文字刻度数组
     * @param unit           单位（y轴需要显示的单位文字）
     */
    public FluentInitializer withData(@Nullable List<BarChartData> data, @Nullable String[] xAxisTextArray, @Nullable String unit) {
        return new FluentInitializer(data, xAxisTextArray, unit);
    }

    /*用于多属性初始化*/
    public class FluentInitializer {
        List<BarChartData> mData;
        int mMaxGradient;

        FluentInitializer(@Nullable List<BarChartData> data,
                          @Nullable String[] xAxisTextArray,
                          @Nullable String unit) {
            this.mData = data;
            mXAxisTextArray = xAxisTextArray;
            mUnit = unit;
        }

        public FluentInitializer maxGradient(int maxGradient) {
            this.mMaxGradient = maxGradient;
            return this;
        }

        public FluentInitializer withMode(@IntRange(from = 0, to = 1) int mode) {
            mMode = mode;
            return this;
        }

        public FluentInitializer withMinCount(int minCount) {
            mMinCount = minCount;
            return this;
        }

        public FluentInitializer withXAxisTextArray(@ArrayRes int arrayId) {
            return withXAxisTextArray(mContext.getResources().getStringArray(arrayId));
        }

        public FluentInitializer withXAxisTextArray(@Nullable String[] xAxisTextArray) {
            mXAxisTextArray = xAxisTextArray;
            return this;
        }

        public FluentInitializer withSubXAxisTextArray(@ArrayRes int arrayId) {
            return withSubXAxisTextArray(mContext.getResources().getStringArray(arrayId));
        }

        public FluentInitializer withSubXAxisTextArray(@Nullable String[] subXAxisTextArray) {
            mSubXAxisTextArray = subXAxisTextArray;
            return this;
        }

        /*设置成能被10整除的数，否则计算不精准*/
        public FluentInitializer yScaleNum(int num) {
            mYScaleNum = num;
            return this;
        }

        /*左边距*/
        public FluentInitializer leftMargin(int leftMargin) {
            mLeftMargin = leftMargin;
            return this;
        }

        /*右边距*/
        public FluentInitializer rightMargin(int rightMargin) {
            mRightMargin = rightMargin;
            return this;
        }

        /*顶部边距*/
        public FluentInitializer topMargin(int topMargin) {
            mTopMargin = topMargin;
            return this;
        }

        /*底部边距*/
        public FluentInitializer bottomMargin(int bottomMargin) {
            mBottomMargin = bottomMargin;
            return this;
        }

        /**
         * @param left   左边距
         * @param top    顶部边距
         * @param right  右边距
         * @param bottom 底部边距
         */
        public FluentInitializer margins(int left, int top, int right, int bottom) {
            mLeftMargin = left;
            mTopMargin = top;
            mRightMargin = right;
            mBottomMargin = bottom;
            return this;
        }

        /*x轴所需空间*/
        public FluentInitializer bottomSpace(int bottomSpace) {
            mBottomSpace = bottomSpace;
            return this;
        }

        /*y轴文字颜色*/
        public FluentInitializer yAxisTextColor(int yAxisTextColor) {
            mYAxisTextColor = yAxisTextColor;
            return this;
        }

        /*y轴字体大小*/
        public FluentInitializer yAxisTextSize(int yAxisTextSize) {
            mYAxisTextSize = yAxisTextSize;
            return this;
        }

        public FluentInitializer yAxisTypeface(@Nullable Typeface typeface) {
            if (typeface != null) {
                mYAxisTextPaint.setTypeface(typeface);
            }
            return this;
        }

        public FluentInitializer xAxisTextLineCount(int lineCount) {
            mXAxisTextLineCount = lineCount;
            return this;
        }

        /*x轴文字颜色*/
        public FluentInitializer xAxisTextColor(int xAxisTextColor) {
            mXAxisTextColor = xAxisTextColor;
            return this;
        }

        /*x轴文字大小*/
        public FluentInitializer xAxisTextSize(int xAxisTextSize) {
            mXAxisTextSize = xAxisTextSize;
            return this;
        }

        /*x轴文字风格*/
        public FluentInitializer xAxisTypeface(@Nullable Typeface typeface) {
            if (typeface != null) {
                mXAxisTextPaint.setTypeface(typeface);
            }
            return this;
        }

        /*x轴文字颜色*/
        public FluentInitializer subXAxisTextColor(int subXAxisTextColor) {
            mSubXAxisTextColor = subXAxisTextColor;
            return this;
        }

        /*x轴文字大小*/
        public FluentInitializer subXAxisTextSize(int subXAxisTextSize) {
            mSubXAxisTextSize = subXAxisTextSize;
            return this;
        }

        /*x轴文字风格*/
        public FluentInitializer subXAxisTypeface(@Nullable Typeface typeface) {
            if (typeface != null) {
                mSubXAxisTextPaint.setTypeface(typeface);
            }
            return this;
        }

        /*是否展示虚线*/
        public FluentInitializer dashedEnabled(boolean isDashedEnabled) {
            mDashedEnabled = isDashedEnabled;
            return this;
        }

        /*虚线颜色*/
        public FluentInitializer dashedColor(int dashedColor) {
            mDashedColor = dashedColor;
            return this;
        }

        /*设置虚线效果*/
        public FluentInitializer setDashPathEffect(DashPathEffect dashPathEffect) {
            mDashPathEffect = dashPathEffect;
            return this;
        }

        /*虚线离x轴文字的距离*/
        public FluentInitializer yAxisMargin(int yAxisMargin) {
            mYAxisMargin = yAxisMargin;
            return this;
        }

        /*虚线离y轴文字的距离*/
        public FluentInitializer xAxisMargin(int xAxisMargin) {
            mXAxisMargin = xAxisMargin;
            return this;
        }

        public FluentInitializer barWidth(int width) {
            mBarWidth = width;
            return this;
        }

        public FluentInitializer barColor(@ColorInt int color) {
            mBarColor = color;
            return this;
        }

        public FluentInitializer dividerWidth(int width) {
            mDividerWidth = width;
            return this;
        }

        public FluentInitializer includeLeftEdge(boolean leftEdge) {
            mIncludeLeftEdge = leftEdge;
            return this;
        }

        public FluentInitializer includeRightEdge(boolean rightEdge) {
            mIncludeRightEdge = rightEdge;
            return this;
        }

        public FluentInitializer barValueEnabled(boolean isEnabled) {
            mBarValueEnabled = isEnabled;
            return this;
        }

        public FluentInitializer barValueAsInt(boolean isBarValueAsInt) {
            mBarValueAsInt = isBarValueAsInt;
            return this;
        }

        public FluentInitializer barValueMargin(int margin) {
            mBarValueMargin = margin;
            return this;
        }

        public FluentInitializer barValueTextColor(@ColorInt int color) {
            mBarValueTextColor = color;
            return this;
        }

        public FluentInitializer barValueTextSize(int textSize) {
            mBarValueTextSize = textSize;
            return this;
        }

        public FluentInitializer barValueTypeface(Typeface typeface) {
            mBarValueTypeface = typeface;
            return this;
        }

        public FluentInitializer displayAnimation(boolean isDisplayAnimation) {
            mDisplayAnimation = isDisplayAnimation;
            return this;
        }

        public FluentInitializer animationDuration(int duration) {
            mAnimationDuration = duration;
            return this;
        }

        public FluentInitializer barValueUnitEdge(boolean includeEdge) {
            mBarValueUnitEdge = includeEdge;
            return this;
        }

        public void start() {
            setData(mData, mMaxGradient);
        }
    }
}
