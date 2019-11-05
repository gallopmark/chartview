package pony.xcode.chart;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Typeface;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.ArrayRes;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import pony.xcode.chart.data.LineChartData;


/*折线图*/
public class LineChartView extends AbsChartView {
    private int mItemWidth; //x轴刻度宽度

    private TextPaint mYAxisTextPaint; //y轴写文字
    private int mYAxisTextColor; //y周文字颜色
    private int mYAxisTextSize; //y轴文字大小

    private TextPaint mXAxisTextPaint; //x轴写文字
    private int mXAxisTextColor; //x轴文字颜色
    private int mXAxisTextSize; //x轴文字大小

    private boolean mDashedEnabled; //是否显示虚线
    private int mDashedColor; //虚线颜色
    private int mDashedWidth; //虚线宽度
    private int mDashedYAxisMargin; //虚线离y轴的距离
    private int mDashedXAxisMargin; //虚线离x轴的距离
    private DashPathEffect mDashPathEffect; //虚线效果

    private int mLeftMargin; //左边距
    private int mRightMargin; //右边距
    private int mTopMargin; //顶部边距
    private int mBottomMargin; //底部边距
    private int mOldTopSpace;
    private int mBottomSpace; //底部腾出多少空间来画x轴文字描述

    private int mLineNumber;  //行数
    private static final int mYScaleNum = 5;

    //PolyLine
    private boolean mPolylineEnabled; //是否展示折线
    private boolean mDisplayPolylineZero;  //当数据最大值为0时，是否展示折线图
    private int mPolylineWidth; //折线粗细
    private int mPolylineColor; //折线颜色

    //Trace 描点
    private boolean mTraceEnabled; //是否展示描点
    private int mTraceSize; //描点大小
    private int mTraceColor; //描点颜色

    //Area 区域
    private boolean mAreaEnabled; //是否展示区域
    private int mAreaColor; //区域颜色

    //Touch event
    private int mSelectPosition = -1; //选中的位置
    //Vertical line
    private int mVerticalLineColor; //画竖线颜色
    private int mVerticalLineWidth; //画竖线的宽度
    private boolean mVerticalLineDashedEffect;  //竖线虚线效果
    private DashPathEffect mVerticalLinePathEffect;
    //point
    private int mPointColor; //选中后的描点颜色
    private int mPointSize; //选中后的描点大小
    private int mPointStrokeWidth;
    private int mPointStrokeColor;

    //description
    private int mDescriptionBgColor; //描述框背景色
    private int mDescriptionHeight; //描述框高度
    private int mDescriptionArrowSize; //角标大小
    private int mDescriptionTextColor; //描述文字颜色
    private int mDescriptionTextSize; //描述文字大小
    private Typeface mDescriptionTypeface;
    private int mDescriptionPadding; //内边距
    //oval
    private int mDescriptionOvalVisibility; //圆圈可见性
    private int mDescriptionOvalMargin;  //圆圈边距（距离文字）
    private int mDescriptionOvalSize; //圆圈大小
    private int mDescriptionOvalStrokeWidth; //圆圈线粗细
    private int mDescriptionOvalStrokeColor; //圆圈颜色

    public static final int OVAL_VISIBLE = 0;
    @SuppressWarnings("unused")
    public static final int OVAL_INVISIBLE = 4;
    public static final int OVAL_GONE = 8;

    //animation
    private boolean mDisplayAnimation; //是否动画展示
    private long mAnimationDuration; //动画持续时间
    private ValueAnimator mValueAnimation;

    //your data
    private String[] mXAxisTextArray;  //x轴文字刻度数组
    private List<LineChartData> mLineChartDataList;
    private String mUnit; //单位

    private boolean mCanceledOnTouchOutside;
    private boolean mReverse; //是否反向
    //    private int mXSpace;  //平分x轴之后的距离值
    private int mMaxGradient;
    private int mAverage; //平均值
    private static final int MINIMUM_SCALE = 1;
    private float mProgress = 1f;
    //touch
    private float mDownX;

    public LineChartView(Context context) {
        this(context, null);
    }

    public LineChartView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LineChartView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        obtainValues(attrs);
    }

    private void obtainValues(@Nullable AttributeSet attrs) {
        TypedArray ta = mContext.obtainStyledAttributes(attrs, R.styleable.LineChartView);
        mItemWidth = ta.getDimensionPixelSize(R.styleable.LineChartView_lcv_itemWidth, 0);
        //margin
        mLeftMargin = ta.getDimensionPixelSize(R.styleable.LineChartView_lcv_leftMargin, ChartUtils.dp2px(mContext, 12));
        mRightMargin = ta.getDimensionPixelSize(R.styleable.LineChartView_lcv_rightMargin, ChartUtils.dp2px(mContext, 12));
        mTopMargin = ta.getDimensionPixelSize(R.styleable.LineChartView_lcv_topMargin, ChartUtils.sp2px(mContext, 0));
        mBottomMargin = ta.getDimensionPixelSize(R.styleable.LineChartView_lcv_bottomMargin, 0);
        mOldTopSpace = mTopMargin;
        mBottomSpace = ta.getDimensionPixelSize(R.styleable.LineChartView_lcv_bottom_space, ChartUtils.dp2px(mContext, 60));
        //x轴
        mYAxisTextColor = ta.getColor(R.styleable.LineChartView_lcv_yAxis_textColor, Color.parseColor("#ffffff"));
        mYAxisTextSize = ta.getDimensionPixelSize(R.styleable.LineChartView_lcv_yAxis_textSize, ChartUtils.sp2px(mContext, 12));
        mXAxisTextColor = ta.getColor(R.styleable.LineChartView_lcv_xAxis_textColor, Color.parseColor("#ffffff"));
        mXAxisTextSize = ta.getDimensionPixelSize(R.styleable.LineChartView_lcv_xAxis_textSize, ChartUtils.sp2px(mContext, 12));
        mDashedColor = ta.getColor(R.styleable.LineChartView_lcv_dashed_color, Color.parseColor("#66ffffff"));
        mDashedWidth = ta.getDimensionPixelSize(R.styleable.LineChartView_lcv_dashed_width, ChartUtils.dp2px(mContext, 0.5f));
        mDashedYAxisMargin = ta.getDimensionPixelSize(R.styleable.LineChartView_lcv_dashed_yAxisMargin, ChartUtils.dp2px(mContext, 16));
        mDashedXAxisMargin = ta.getDimensionPixelSize(R.styleable.LineChartView_lcv_dashed_xAxisMargin, ChartUtils.dp2px(mContext, 16));
        mDashedEnabled = ta.getBoolean(R.styleable.LineChartView_lcv_dashed_enabled, true);
        mLineNumber = ta.getInt(R.styleable.LineChartView_lcv_yScaleNum, mYScaleNum);
        //polyline
        mPolylineEnabled = ta.getBoolean(R.styleable.LineChartView_lcv_polyline_enabled, true);
        mDisplayPolylineZero = ta.getBoolean(R.styleable.LineChartView_lcv_polyline_display_zero, false);
        mPolylineWidth = ta.getDimensionPixelSize(R.styleable.LineChartView_lcv_polyline_width, ChartUtils.dp2px(mContext, 1));
        mPolylineColor = ta.getColor(R.styleable.LineChartView_lcv_polyline_color, Color.parseColor("#ffff00"));
        //trace
        mTraceEnabled = ta.getBoolean(R.styleable.LineChartView_lcv_trace_enable, false);
        mTraceSize = ta.getDimensionPixelSize(R.styleable.LineChartView_lcv_trace_size, ChartUtils.dp2px(mContext, 6));
        mTraceColor = ta.getColor(R.styleable.LineChartView_lcv_trace_color, Color.parseColor("#007AFF"));
        //area
        mAreaEnabled = ta.getBoolean(R.styleable.LineChartView_lcv_area_enabled, true);
        mAreaColor = ta.getColor(R.styleable.LineChartView_lcv_area_color, Color.parseColor("#66008aff"));
        //vertical line
        mVerticalLineColor = ta.getColor(R.styleable.LineChartView_lcv_vertical_lineColor, Color.parseColor("#ffffff"));
        mVerticalLineWidth = ta.getDimensionPixelSize(R.styleable.LineChartView_lcv_vertical_lineWidth, ChartUtils.dp2px(mContext, 1));
        mVerticalLineDashedEffect = ta.getBoolean(R.styleable.LineChartView_lcv_vertical_dashed_effect, false);
        //point
        mPointColor = ta.getColor(R.styleable.LineChartView_lcv_point_color, Color.parseColor("#0045A7"));
        mPointSize = ta.getDimensionPixelSize(R.styleable.LineChartView_lcv_point_size, ChartUtils.dp2px(mContext, 10));
        mPointStrokeWidth = ta.getDimensionPixelSize(R.styleable.LineChartView_lcv_point_strokeWidth, ChartUtils.dp2px(mContext, 2));
        mPointStrokeColor = ta.getColor(R.styleable.LineChartView_lcv_point_strokeColor, Color.parseColor("#FFFF00"));
        //description
        mDescriptionHeight = ta.getDimensionPixelSize(R.styleable.LineChartView_lcv_description_height, ChartUtils.dp2px(mContext, 20));
        mDescriptionArrowSize = ta.getDimensionPixelSize(R.styleable.LineChartView_lcv_description_arrow_size, ChartUtils.dp2px(mContext, 4));
        mDescriptionBgColor = ta.getColor(R.styleable.LineChartView_lcv_description_background, Color.parseColor("#ffffff"));
        mDescriptionTextColor = ta.getColor(R.styleable.LineChartView_lcv_description_textColor, Color.parseColor("#0045A7"));
        mDescriptionTextSize = ta.getDimensionPixelSize(R.styleable.LineChartView_lcv_description_textSize, ChartUtils.sp2px(mContext, 12));
        mDescriptionPadding = ta.getDimensionPixelSize(R.styleable.LineChartView_lcv_description_padding, ChartUtils.dp2px(mContext, 4));
        //oval
        mDescriptionOvalVisibility = ta.getInt(R.styleable.LineChartView_lcv_description_oval_visibility, OVAL_VISIBLE);
        mDescriptionOvalMargin = ta.getDimensionPixelSize(R.styleable.LineChartView_lcv_description_oval_margin, ChartUtils.dp2px(mContext, 4));
        mDescriptionOvalSize = ta.getDimensionPixelSize(R.styleable.LineChartView_lcv_description_oval_size, ChartUtils.dp2px(mContext, 10));
        if (mDescriptionOvalSize > mDescriptionHeight) { //圆圈高度不超过描述框高度
            mDescriptionOvalSize = mDescriptionHeight;
        }
        mDescriptionOvalStrokeWidth = ta.getDimensionPixelSize(R.styleable.LineChartView_lcv_description_oval_strokeWidth, ChartUtils.dp2px(mContext, 1));
        mDescriptionOvalStrokeColor = ta.getColor(R.styleable.LineChartView_lcv_description_oval_strokeColor, Color.parseColor("#0045A7"));
        mDisplayAnimation = ta.getBoolean(R.styleable.LineChartView_lcv_display_animation, true);
        mAnimationDuration = ta.getInt(R.styleable.LineChartView_lcv_animation_duration, 1000);
        //xAxis text array
        final int textArrayResId = ta.getResourceId(R.styleable.LineChartView_lcv_xAxis_textArray, -1);
        if (textArrayResId != -1) {
            mXAxisTextArray = mContext.getResources().getStringArray(textArrayResId);
        }
        mCanceledOnTouchOutside = ta.getBoolean(R.styleable.LineChartView_lcv_canceledOnTouchOutside, false);
        mReverse = ta.getBoolean(R.styleable.LineChartView_lcv_reverse, false);
        ta.recycle();
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (mTopMargin == mOldTopSpace) {
            mTopMargin += getYSpace();
        }
    }

    public void setData(@Nullable List<LineChartData> dataList) {
        if (dataList == null) return;
        this.mLineChartDataList = new ArrayList<>(dataList);
        int maxValue = getMaxValueFromData();
        mMaxGradient = ChartUtils.getMaxGraded(maxValue);
        mAverage = mMaxGradient / mLineNumber;
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
        for (LineChartData data : mLineChartDataList) {
            if (data.getValue() > max) {
                max = (int) data.getValue();
            }
        }
        return max;
    }

    private void initPaint() {
        mYAxisTextPaint = new TextPaint();
        mYAxisTextPaint.setAntiAlias(true);
        mYAxisTextPaint.setTextSize(mYAxisTextSize);
        mYAxisTextPaint.setColor(mYAxisTextColor);
        mYAxisTextPaint.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));

        mXAxisTextPaint = new TextPaint();
        mXAxisTextPaint.setStyle(Paint.Style.FILL);
        mXAxisTextPaint.setColor(mXAxisTextColor);
        mXAxisTextPaint.setAntiAlias(true);
        mXAxisTextPaint.setTextSize(mXAxisTextSize);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mLineChartDataList != null) {
            initItemMinWidth();
            initNeedWidth();
            if (mReverse) {
                reverse(canvas, getStartDyReverse(), getYSpace());
            } else {
                int startDy = getStartDy();
                int ySpace = getYSpace();
                drawYAxis(canvas, getStartDy(), ySpace);
                int startDx = getStartDx();
                if (mDashedEnabled) {
                    drawDashedLine(canvas, startDx, startDy, ySpace);
                }
                drawXAxis(canvas, startDx, startDy);
                if (mPolylineEnabled) {
                    drawPolyline(canvas, startDx);
                }
                if (mTraceEnabled) {
                    drawTrace(canvas, startDx);
                }
                if (mAreaEnabled) {
                    drawArea(canvas, startDx, startDy);
                }
                drawSelectCeil(canvas, startDx);
            }
        }
    }

    private void initItemMinWidth() {
        int screenWidth = ChartUtils.getScreenWidth(mContext);
        float itemMinWidth;
        if (mXAxisTextArray != null && mXAxisTextArray.length > 0) {
            int divisor = mXAxisTextArray.length - 1 <= 0 ? 1 : mXAxisTextArray.length - 1;
            itemMinWidth = (screenWidth - getStartDx() - (mRightMargin + getLastXTextWidth() / 2f)) / (float) divisor;
        } else {
            int divisor = mLineChartDataList.size() - 1 <= 0 ? 1 : mLineChartDataList.size() - 1;
            itemMinWidth = (screenWidth - getStartDx() - (mRightMargin + getLastXTextWidth() / 2f)) / (float) divisor;
        }
        if (mItemWidth < itemMinWidth) {
            mItemWidth = (int) Math.floor(itemMinWidth);
        }
    }

    private void initNeedWidth() {
        if (mXAxisTextArray != null && mXAxisTextArray.length > 0) {
            mNeedWidth = mLeftMargin + getTextMaxWidth() + mDashedYAxisMargin + mItemWidth * mXAxisTextArray.length;
        } else {
            mNeedWidth = mLeftMargin + getTextMaxWidth() + mDashedYAxisMargin + mItemWidth * mLineChartDataList.size();
        }
    }

    private void reverse(Canvas canvas, int startDy, int ySpace) {
        drawYAxisReverse(canvas, startDy, ySpace);
        int startDx = getStartDx();
        if (mDashedEnabled) {
            drawDashedLineReverse(canvas, startDx, startDy, ySpace);
        }
        drawXAxis(canvas, startDx, getStartDy());
        if (mPolylineEnabled) {
            drawPolylineReverse(canvas, startDx);
        }
        if (mTraceEnabled) {
            drawTraceReverse(canvas, startDx);
        }
        if (mAreaEnabled) {
            drawAreaReserve(canvas, startDx, startDy);
        }
        drawSelectCeilReverse(canvas, startDx);
    }

    /*画y轴刻度文字*/
    private void drawYAxis(Canvas canvas, int startDy, int ySpace) {
        String unit = TextUtils.isEmpty(mUnit) ? "" : mUnit;
        for (int i = 0; i <= mLineNumber; i++) {
            canvas.drawText((mAverage * i) + unit, mLeftMargin, startDy - i * ySpace, mYAxisTextPaint);
        }
    }

    //反向画y轴文字刻度
    private void drawYAxisReverse(Canvas canvas, int startDy, int ySpace) {
        String unit = TextUtils.isEmpty(mUnit) ? "" : mUnit;
        for (int i = 0; i <= mLineNumber; i++) {
            if (i == 0) {
                canvas.drawText(MINIMUM_SCALE + unit, mLeftMargin, startDy + i * ySpace, mYAxisTextPaint);
            } else {
                canvas.drawText((mAverage * i) + unit, mLeftMargin, startDy + i * ySpace, mYAxisTextPaint);
            }
        }
    }

    /*画虚线*/
    private void drawDashedLine(Canvas canvas, int startDx, int startDy, int ySpace) {
        Path path = new Path();
        Paint paint = getDashedLinePaint();
        for (int i = 0; i <= mLineNumber; i++) {
            path.reset();
            float y = startDy - (i * ySpace);
            path.moveTo(startDx, y); //线起点
            path.lineTo(getEndX(), y); //线终点
            canvas.drawPath(path, paint); //开始画线
        }
    }

    /*画反向虚线*/
    private void drawDashedLineReverse(Canvas canvas, int startDx, int startDy, int ySpace) {
        Path path = new Path();
        Paint paint = getDashedLinePaint();
        for (int i = 0; i <= mLineNumber; i++) {
            path.reset();
            float y = startDy + (i * ySpace);
            path.moveTo(startDx, y); //线起点
            path.lineTo(getEndX(), y); //线终点
            canvas.drawPath(path, paint); //开始画线
        }
    }

    private Paint getDashedLinePaint() {
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(mDashedColor);
        paint.setStrokeWidth(mDashedWidth);
        if (mDashPathEffect == null) {
            paint.setPathEffect(new DashPathEffect(new float[]{5, 5}, 0));
        } else {
            paint.setPathEffect(mDashPathEffect);
        }
        return paint;
    }

    /*画x轴刻度*/
    private void drawXAxis(Canvas canvas, int startX, int startDy) {
        //画x轴刻度
        if (mXAxisTextArray != null && mXAxisTextArray.length > 0) {
            for (int i = 0; i < mXAxisTextArray.length; i++) {
                drawXAxisText(canvas, mXAxisTextArray[i], startX, startDy, i);
            }
        } else {
            for (int i = 0; i < mLineChartDataList.size(); i++) {
                drawXAxisText(canvas, mLineChartDataList.get(i).getXAxisText(), startX, startDy, i);
            }
        }
    }

    private void drawXAxisText(Canvas canvas, String text, int startX, int startDy, int index) {
        int textWidth = ChartUtils.getTextWidth(text, mXAxisTextPaint);
        float x = startX + mItemWidth * index - textWidth / 2f;
        float y = startDy + mDashedXAxisMargin;
        /* staticLayout支持换行，它既可以为文字设置宽度上限来让文字自动换行，也会在 \n 处主动换行。
         * width 是文字区域的宽度，文字到达这个宽度后就会自动换行；
         * align 是文字的对齐方向；
         * spacingmult 是行间距的倍数，通常情况下填 1 就好；
         * spacingadd 是行间距的额外增加值，通常情况下填 0 就好；
         * includeadd 是指是否在文字上下添加额外的空间，来避免某些过高的字符的绘制出现越界。
         * */
        String source = TextUtils.isEmpty(text) ? "" : text;
        StaticLayout staticLayout = new StaticLayout(source, mXAxisTextPaint, textWidth, Layout.Alignment.ALIGN_CENTER, 1f, 0, false);
        canvas.save();
        canvas.translate(x, y);
        staticLayout.draw(canvas);
        canvas.restore();
    }

    /*画折线*/
    private void drawPolyline(Canvas canvas, int startDx) {
        if (!mDisplayPolylineZero && getMaxValueFromData() <= 0) return;
        Paint paint = getPolyLinePaint();
        int start = 0;
        for (int i = 0, size = mLineChartDataList.size(); i < size; i++) {
            float startX = mItemWidth * (start) + startDx;
            double value = mLineChartDataList.get(start).getValue();
            float startY = getPointY(value);
            float stopX = mItemWidth * i + startDx;
            value = mLineChartDataList.get(i).getValue();
            float stopY = getPointY(value);
            canvas.drawLine(startX, startY, stopX, stopY, paint);
            start = i;
        }
    }

    /*反向画折线*/
    private void drawPolylineReverse(Canvas canvas, int startDx) {
        if (!mDisplayPolylineZero && getMaxValueFromData() <= 0) return;
        Paint paint = getPolyLinePaint();
        int start = 0;
        for (int i = 0, size = mLineChartDataList.size(); i < size; i++) {
            float startX = mItemWidth * (start) + startDx;
            double value = mLineChartDataList.get(start).getValue();
            float startY = getPointYReverse(value);
            float stopX = mItemWidth * i + startDx;
            value = mLineChartDataList.get(i).getValue();
            float stopY = getPointYReverse(value);
            canvas.drawLine(startX, startY, stopX, stopY, paint);
            start = i;
        }
    }

    /*画折线paint*/
    private Paint getPolyLinePaint() {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStrokeWidth(mPolylineWidth);
        paint.setColor(mPolylineColor);
        return paint;
    }

    /*画描点*/
    private void drawTrace(Canvas canvas, int startDx) {
        Paint paint = getTracePaint();
        for (int i = 0, size = mLineChartDataList.size(); i < size; i++) {
            float startX = mItemWidth * i + startDx;
            float startY = getPointY(mLineChartDataList.get(i).getValue());
            canvas.drawCircle(startX, startY, mTraceSize / 2f, paint);
        }
    }

    private void drawTraceReverse(Canvas canvas, int startDx) {
        Paint paint = getTracePaint();
        for (int i = 0, size = mLineChartDataList.size(); i < size; i++) {
            float startX = mItemWidth * i + startDx;
            double value = mLineChartDataList.get(i).getValue();
            float startY = getPointYReverse(value);
            canvas.drawCircle(startX, startY, mTraceSize / 2f, paint);
        }
    }

    private Paint getTracePaint() {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(mTraceColor);
        return paint;
    }

    //画区域
    private void drawArea(Canvas canvas, int startDx, int startDy) {
        Paint paint = getAreaPaint();
        Path path = new Path();
        float start = startDx, end = startDx;
        for (int i = 0, size = mLineChartDataList.size(); i < size; i++) {
            final float x = startDx + mItemWidth * i;
            final double value = mLineChartDataList.get(i).getValue();
            if (i == 0) {
                start = x;
                path.moveTo(x, getPointY(value));
            } else {
                end = x;
                path.lineTo(end, getPointY(value));
            }
        }
        path.lineTo(end, startDy);
        path.lineTo(start, startDy);
        path.close();
        canvas.drawPath(path, paint);
    }

    private void drawAreaReserve(Canvas canvas, int startDx, int startDy) {
        Paint paint = getAreaPaint();
        Path path = new Path();
        float start = startDx, end = startDx;
        for (int i = 0, size = mLineChartDataList.size(); i < size; i++) {
            final float x = startDx + mItemWidth * i;
            double value = mLineChartDataList.get(i).getValue();
            if (i == 0) {
                start = x;
                path.moveTo(x, getPointYReverse(value));
            } else {
                end = x;
                path.lineTo(end, getPointYReverse(value));
            }
        }
        path.lineTo(end, startDy);
        path.lineTo(start, startDy);
        path.close();
        canvas.drawPath(path, paint);
    }

    private Paint getAreaPaint() {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(mAreaColor);
        return paint;
    }

    private Paint getSelectCeilPaint() {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        return paint;
    }

    /*画选中部分*/
    private void drawSelectCeil(Canvas canvas, int startDx) {
        if (mSelectPosition >= 0 && mSelectPosition < mLineChartDataList.size()) {
            Paint paint = getSelectCeilPaint();
            float dx = startDx + mItemWidth * mSelectPosition;
            drawVerticalLine(canvas, dx, mOldTopSpace + mDescriptionHeight + mDescriptionArrowSize, dx, getStartDy(), paint);
            double value = mLineChartDataList.get(mSelectPosition).getValue();
            drawPoint(canvas, dx, getPointY(value));
            drawPointDescription(canvas, startDx, mSelectPosition);
        }
    }

    private void drawSelectCeilReverse(Canvas canvas, int startDx) {
        if (mSelectPosition >= 0 && mSelectPosition < mLineChartDataList.size()) {
            Paint paint = getSelectCeilPaint();
            float dx = startDx + mItemWidth * mSelectPosition;
            drawVerticalLine(canvas, dx, mOldTopSpace + mDescriptionHeight + mDescriptionArrowSize, dx, getStartDy(), paint);
            double value = mLineChartDataList.get(mSelectPosition).getValue();
            drawPoint(canvas, dx, getPointYReverse(value));
            drawPointDescription(canvas, startDx, mSelectPosition);
        }
    }

    /*画竖线*/
    private void drawVerticalLine(Canvas canvas, float startX, float startY, float stopX, float stopY, Paint paint) {
        paint.setColor(mVerticalLineColor);
        paint.setStrokeWidth(mVerticalLineWidth);
        if (mVerticalLineDashedEffect) {
            if (mVerticalLinePathEffect == null) {
                paint.setPathEffect(new DashPathEffect(new float[]{5, 5}, 0));
            } else {
                paint.setPathEffect(mDashPathEffect);
            }
        }
        canvas.drawLine(startX, startY, stopX, stopY, paint);
    }

    /*画描点*/
    private void drawPoint(Canvas canvas, float cx, float cy) {
        Paint paint = getSelectCeilPaint();
        final float radius = mPointSize / 2f;
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(mPointColor);
        canvas.drawCircle(cx, cy, radius, paint);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(mPointStrokeWidth);
        paint.setColor(mPointStrokeColor);
        canvas.drawCircle(cx, cy, radius, paint);
    }

    /*描述*/
    private void drawPointDescription(Canvas canvas, int startDx, int position) {
        TextPaint paint = getDescriptionPaint();
        paint.setColor(mDescriptionBgColor);
        paint.setTextSize(mDescriptionTextSize);
        paint.setStyle(Paint.Style.FILL);
        String text = getDescription(position);
        /*计算文本所需宽度*/
        int ovalSize = mDescriptionOvalVisibility == OVAL_GONE ? 0 : mDescriptionOvalSize;
        int ovalMargin = 0;
        if (ovalSize > 0) {
            ovalMargin = mDescriptionOvalMargin;
        }
        if (mDescriptionTypeface != null) {
            paint.setTypeface(mDescriptionTypeface);
        }
        int rectWidth = ChartUtils.getTextWidth(text, paint) + ovalSize + ovalMargin + mDescriptionPadding * 2;
        //画箭头框
        Path path = new Path();
        final float startY = mOldTopSpace;
        int textLeft;
        if (position == 0) {
            textLeft = startDx;
            path.moveTo(startDx, startY);
            path.lineTo(startDx + rectWidth, startY);
            path.lineTo(startDx + rectWidth, startY + mDescriptionHeight);
            /*下面两步为画角标*/
            path.lineTo(startDx + mDescriptionArrowSize, startY + mDescriptionHeight);
            path.lineTo(startDx, startY + mDescriptionHeight + mDescriptionArrowSize);
            path.close();
            canvas.drawPath(path, paint);
        } else if (position == mLineChartDataList.size() - 1) {  //最后一个位置
            final int dx = startDx + position * mItemWidth;
            textLeft = dx - rectWidth;
            path.moveTo(dx - rectWidth, startY); //起始点
            path.lineTo(dx, startY); //终点
            path.lineTo(dx, startY + mDescriptionHeight + mDescriptionArrowSize); //终点向下画
            //draw arrow
            path.lineTo(dx - mDescriptionArrowSize, startY + mDescriptionHeight);
            path.lineTo(dx - rectWidth, startY + mDescriptionHeight);
            path.close();
            canvas.drawPath(path, paint);
        } else {
            final int dx = startDx + position * mItemWidth;
            textLeft = dx - rectWidth / 2;
            final float xl = dx - rectWidth / 2f;
            final float xr = dx + rectWidth / 2f;
            final float dy = startY + mDescriptionHeight;
            path.moveTo(xl, startY);
            path.lineTo(xr, startY);
            path.lineTo(xr, dy);
            path.lineTo(dx + mDescriptionArrowSize, dy);
            path.lineTo(dx, dy + mDescriptionArrowSize);
            path.lineTo(dx - mDescriptionArrowSize, dy);
            path.lineTo(xl, dy);
            path.close();
            canvas.drawPath(path, paint);
        }
        //写文字
        paint.setColor(mDescriptionTextColor);
        final float y = startY + mDescriptionHeight / 2f;
        /*写描述文字*/
        canvas.drawText(text, textLeft + mDescriptionPadding + ovalMargin + ovalSize, y + ChartUtils.div(mDescriptionTextSize, 2.5f), paint);
        //画圆圈
        drawDescriptionOval(canvas, textLeft + mDescriptionPadding + ovalSize / 2f, y, ovalSize / 2f, paint);
    }

    /*画圆圈*/
    private void drawDescriptionOval(Canvas canvas, float cx, float cy, float radius, Paint paint) {
        //画圆圈
        if (mDescriptionOvalVisibility == OVAL_VISIBLE) {
            paint.setColor(mDescriptionOvalStrokeColor);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(mDescriptionOvalStrokeWidth);
            canvas.drawCircle(cx, cy, radius, paint);
        }
    }

    private TextPaint getDescriptionPaint() {
        TextPaint paint = new TextPaint();
        paint.setAntiAlias(true);
        return paint;
    }

    /*获取描述文本*/
    private String getDescription(int position) {
        if (position >= 0 && position < mLineChartDataList.size() && !TextUtils.isEmpty(mLineChartDataList.get(position).getDescription())) {
            return mLineChartDataList.get(position).getDescription();
        } else {
            String description = "";
            if (mXAxisTextArray != null && mXAxisTextArray.length > 0) {
                if (position >= 0 && position < mXAxisTextArray.length) {
                    description += mXAxisTextArray[position];
                }
                if (position >= 0 && position < mLineChartDataList.size()) {
                    description += "：" + mLineChartDataList.get(position).getValue();
                    description += TextUtils.isEmpty(mUnit) ? "" : mUnit;
                }
            } else {
                if (position >= 0 && position < mLineChartDataList.size()) {
                    LineChartData data = mLineChartDataList.get(position);
                    String axisText = data.getXAxisText();
                    String splitText = "：" + data.getValue();
                    description = TextUtils.isEmpty(axisText) ? "" + splitText : axisText + splitText;
                    description += TextUtils.isEmpty(mUnit) ? "" : mUnit;
                    return description;
                }
            }
            return description;
        }
    }

    /*数值对应的y坐标*/
    private float getPointY(double value) {
        return (float) (getStartDy() - (getStartDy() - mTopMargin) * (value / mMaxGradient) * mProgress);
    }

    private float getPointYReverse(double value) {
        float percent = (float) (value < mAverage ? ((value - MINIMUM_SCALE) / (mMaxGradient - MINIMUM_SCALE)) : (value / mMaxGradient));
        return mTopMargin + getChartHeight() * percent * mProgress;
    }

    /*图表高度*/
    public int getChartHeight() {
        return mHeight - mTopMargin - mBottomSpace - mBottomMargin;
    }

    /*开始画x轴刻度的位置*/
    private int getStartDx() {
        return mLeftMargin + mDashedYAxisMargin + getTextMaxWidth();
    }

    /*最大值文本宽度*/
    private int getTextMaxWidth() {
        if (mMaxGradient > 0) {
            String text;
            if (!TextUtils.isEmpty(mUnit)) {
                text = mMaxGradient + mUnit;
            } else {
                text = String.valueOf(mMaxGradient);
            }
            return ChartUtils.getTextWidth(text, mYAxisTextPaint);
        }
        return 0;
    }

    /*开始画y轴刻度的位置*/
    private int getStartDy() {
        return mHeight - mBottomSpace - mBottomMargin;
    }

    private int getStartDyReverse() {
        return mTopMargin;
    }

    /*y轴平均值*/
    private int getYSpace() {
        return (mHeight - mTopMargin - mBottomSpace - mBottomMargin) / mLineNumber;
    }

    /*x轴最右端*/
    private int getEndX() {
        final int screenWidth = mContext.getResources().getDisplayMetrics().widthPixels;
        if (mNeedWidth < screenWidth) {
            return (int) (screenWidth - mRightMargin - getLastXTextWidth() / 2f);
        }
        return (int) (mNeedWidth - mRightMargin - getLastXTextWidth() / 2f);
    }

    private int getLastXTextWidth() {
        if (mXAxisTextArray != null && mXAxisTextArray.length > 0) {
            return ChartUtils.getTextWidth(mXAxisTextArray[mXAxisTextArray.length - 1], mXAxisTextPaint);
        } else {
            return ChartUtils.getTextWidth(mLineChartDataList.get(mLineChartDataList.size() - 1).getXAxisText(), mXAxisTextPaint);
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mLineChartDataList == null || mLineChartDataList.isEmpty())
            return super.onTouchEvent(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mDownX = event.getX() + getScrollX();
                break;
            case MotionEvent.ACTION_UP:
                if (event.getX() + getScrollX() == mDownX) {
                    for (int i = 0, size = mLineChartDataList.size(); i < size; i++) {
                        final int startDx = getStartDx();
                        final float dis = startDx + mItemWidth * i;
                        final float cx = event.getX() + getScrollX();
                        if (cx >= dis && cx < dis + mItemWidth && mLineChartDataList.get(i).isClickable()) {
                            setSelectPosition(i);
                            break;
                        } else {
                            if (mCanceledOnTouchOutside) {
                                setSelectPosition(-1);
                            }
                        }
                    }
                }
                break;
        }
        return super.onTouchEvent(event);
    }

    /*设置选中位置*/
    public void setSelectPosition(int position) {
        mSelectPosition = position;
        invalidate();
    }

    public FluentInitializer withData(@Nullable List<LineChartData> data) {
        return withData(data, null);
    }

    public FluentInitializer withData(@Nullable List<LineChartData> data, @ArrayRes int arrayId) {
        return withData(data, mContext.getResources().getStringArray(arrayId));
    }

    public FluentInitializer withData(@Nullable List<LineChartData> data, @Nullable String[] xAxisTextArray) {
        return withData(data, xAxisTextArray, null);
    }

    public FluentInitializer withData(@Nullable List<LineChartData> data, @ArrayRes int arrayId, String unit) {
        return withData(data, mContext.getResources().getStringArray(arrayId), unit);
    }

    /**
     * @param data           数据源
     * @param xAxisTextArray x轴文字刻度数组
     * @param unit           单位（y轴需要显示的单位文字）
     */
    public FluentInitializer withData(@Nullable List<LineChartData> data, @Nullable String[] xAxisTextArray, @Nullable String unit) {
        return new FluentInitializer(data, xAxisTextArray, unit);
    }

    /*用于多属性初始化*/
    public class FluentInitializer {
        List<LineChartData> mData;

        FluentInitializer(@Nullable List<LineChartData> data, @Nullable String[] xAxisTextArray, @Nullable String unit) {
            this.mData = data;
            mXAxisTextArray = xAxisTextArray;
            mUnit = unit;
        }

        /*设置成能被10整除的数，否则计算不精准*/
        public FluentInitializer yScaleNum(int num) {
            mLineNumber = num;
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

        public FluentInitializer yAxisTypeface(@Nullable Typeface typeface) {
            if (typeface != null) {
                mYAxisTextPaint.setTypeface(typeface);
            }
            return this;
        }

        /*y轴字体大小*/
        public FluentInitializer yAxisTextSize(int yAxisTextSize) {
            mYAxisTextSize = yAxisTextSize;
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

        public FluentInitializer xAxisTypeface(@Nullable Typeface typeface) {
            if (typeface != null) {
                mXAxisTextPaint.setTypeface(typeface);
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

        /*虚线离x轴文字的距离*/
        public FluentInitializer dashedXAxisMargin(int dashedXAxisMargin) {
            mDashedXAxisMargin = dashedXAxisMargin;
            return this;
        }

        /*虚线离y轴文字的距离*/
        public FluentInitializer dashedYAxisMargin(int dashedYAxisMargin) {
            mDashedYAxisMargin = dashedYAxisMargin;
            return this;
        }

        /*设置虚线效果*/
        public FluentInitializer setDashPathEffect(DashPathEffect dashPathEffect) {
            mDashPathEffect = dashPathEffect;
            return this;
        }

        /*是否展示折线*/
        public FluentInitializer polylineEnabled(boolean isPolylineEnabled) {
            mPolylineEnabled = isPolylineEnabled;
            return this;
        }

        /*当数据中最大值为0时是否展示折线*/
        public FluentInitializer polylineDisplayZero(boolean isDisplayPolylineZero) {
            mDisplayPolylineZero = isDisplayPolylineZero;
            return this;
        }

        /*折线颜色*/
        public FluentInitializer polylineColor(int polylineColor) {
            mPolylineColor = polylineColor;
            return this;
        }

        /*折线粗细*/
        public FluentInitializer polylineWidth(int polylineWidth) {
            mPolylineWidth = polylineWidth;
            return this;
        }

        /*是否展示折线上的描点*/
        public FluentInitializer traceEnabled(boolean isTraceEnabled) {
            mTraceEnabled = isTraceEnabled;
            return this;
        }

        /*描点大小*/
        public FluentInitializer traceSize(int traceSize) {
            mTraceSize = traceSize;
            return this;
        }

        /*描点颜色*/
        public FluentInitializer setTraceColor(int traceColor) {
            mTraceColor = traceColor;
            return this;
        }

        /*是否展示区域*/
        public FluentInitializer areaEnabled(boolean isAreaEnabled) {
            mAreaEnabled = isAreaEnabled;
            return this;
        }

        /*区域颜色*/
        public FluentInitializer areaColor(int areaColor) {
            mAreaColor = areaColor;
            return this;
        }

        /*竖线虚线效果*/
        public FluentInitializer verticalLineDashedEffect(boolean isDashedEffect) {
            mVerticalLineDashedEffect = isDashedEffect;
            return this;
        }

        public FluentInitializer verticalLinePathEffect(DashPathEffect effect) {
            mVerticalLinePathEffect = effect;
            return this;
        }

        /*竖线粗细*/
        public FluentInitializer verticalLineWidth(int verticalLineWidth) {
            mVerticalLineWidth = verticalLineWidth;
            return this;
        }

        /*竖线颜色*/
        public FluentInitializer verticalLineColor(int verticalLineColor) {
            mVerticalLineColor = verticalLineColor;
            return this;
        }

        /*选择后 对应值y点的描点大小*/
        public FluentInitializer pointSize(int pointSize) {
            mPointSize = pointSize;
            return this;
        }

        //color
        public FluentInitializer pointColor(int pointColor) {
            mPointColor = pointColor;
            return this;
        }

        public FluentInitializer pointStrokeWidth(int pointStrokeWidth) {
            mPointStrokeWidth = pointStrokeWidth;
            return this;
        }

        public FluentInitializer pointStrokeColor(int pointStrokeColor) {
            mPointStrokeColor = pointStrokeColor;
            return this;
        }

        public FluentInitializer descriptionBackground(int descriptionBackground) {
            mDescriptionBgColor = descriptionBackground;
            return this;
        }

        public FluentInitializer descriptionHeight(int descriptionHeight) {
            mDescriptionHeight = descriptionHeight;
            return this;
        }

        public FluentInitializer descriptionArrowSize(int descriptionArrowSize) {
            mDescriptionArrowSize = descriptionArrowSize;
            return this;
        }

        public FluentInitializer descriptionPadding(int descriptionPadding) {
            mDescriptionPadding = descriptionPadding;
            return this;
        }

        public FluentInitializer descriptionTextSize(int descriptionTextSize) {
            mDescriptionTextSize = descriptionTextSize;
            return this;
        }

        public FluentInitializer descriptionTextColor(int descriptionTextColor) {
            mDescriptionTextColor = descriptionTextColor;
            return this;
        }

        public FluentInitializer descriptionTypeface(Typeface typeface) {
            mDescriptionTypeface = typeface;
            return this;
        }

        /*描述框 圆圈的可见性*/
        public FluentInitializer descriptionOvalVisibility(int descriptionOvalVisibility) {
            mDescriptionOvalVisibility = descriptionOvalVisibility;
            return this;
        }

        public FluentInitializer descriptionOvalMargin(int margin) {
            mDescriptionOvalMargin = margin;
            return this;
        }

        /*圆圈大小*/
        public FluentInitializer descriptionOvalSize(int descriptionOvalSize) {
            mDescriptionOvalSize = descriptionOvalSize;
            return this;
        }

        public FluentInitializer descriptionOvalStrokeWidth(int width) {
            mDescriptionOvalStrokeWidth = width;
            return this;
        }

        public FluentInitializer descriptionOvalStrokeColor(int color) {
            mDescriptionOvalStrokeColor = color;
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

        public FluentInitializer canceledOnTouchOutside(boolean isCanceled) {
            mCanceledOnTouchOutside = isCanceled;
            return this;
        }

        public FluentInitializer reverse(boolean isReverse) {
            mReverse = isReverse;
            return this;
        }

        public void start() {
            setData(mData);
        }
    }
}
