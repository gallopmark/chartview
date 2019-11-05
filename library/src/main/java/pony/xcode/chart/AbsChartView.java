package pony.xcode.chart;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.LinearInterpolator;
import android.widget.Scroller;

import androidx.annotation.Nullable;

/*超出屏幕宽度部分可左右滚动*/
abstract class AbsChartView extends View {
    protected Context mContext;
    protected int mWidth, mHeight;
    protected int mNeedWidth;

    //scroll
    private Scroller mScroller;
    private int mMinimumVelocity;
    private int mMaximumVelocity;
    private VelocityTracker mVelocityTracker;
    private float mLastTouchX;

    public AbsChartView(Context context) {
        this(context, null);
    }

    public AbsChartView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AbsChartView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        initScroller();
    }

    private void initScroller() {
        mScroller = new Scroller(mContext, new LinearInterpolator());
        ViewConfiguration configuration = ViewConfiguration.get(mContext);
        mMinimumVelocity = configuration.getScaledMinimumFlingVelocity();
        mMaximumVelocity = configuration.getScaledMaximumFlingVelocity();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = getMeasuredWidth();
        mHeight = getMeasuredHeight();
    }

    /**
     * 判断是否可以水平滑动
     *
     * @param direction 标识滑动方向  正数：右滑(手指从右至左移动)；负数：左滑(手指由左向右移动)
     */
    @Override
    public boolean canScrollHorizontally(int direction) {
        if (direction > 0) {
            return mNeedWidth - getScrollX() - mWidth > 0;
        } else {
            return getScrollX() > 0;
        }
    }

    /**
     * 根据滑动方向获取最大可滑动距离
     *
     * @param direction 标识滑动方向  正数：右滑(手指从右至左移动)；负数：左滑(手指由左向右移动)
     *                  您可参考ScaollView或HorizontalScrollView理解滑动方向
     */
    private int getMaxCanScrollX(int direction) {
        if (direction > 0) {
            return mNeedWidth - getScrollX() - mWidth > 0 ? mNeedWidth - getScrollX() - mWidth : 0;
        } else if (direction < 0) {
            return getScrollX();
        }
        return 0;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        initVelocityTrackerIfNotExists();
        mVelocityTracker.addMovement(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (!mScroller.isFinished()) {
                    mScroller.abortAnimation();
                }
                mLastTouchX = event.getX();
                return true;
            case MotionEvent.ACTION_MOVE:
                int deltaX = (int) (event.getX() - mLastTouchX);
                mLastTouchX = event.getX();
                // 滑动处理
                if (deltaX > 0 && canScrollHorizontally(-1)) {
                    scrollBy(-Math.min(getMaxCanScrollX(-1), deltaX), 0);
                } else if (deltaX < 0 && canScrollHorizontally(1)) {
                    scrollBy(Math.min(getMaxCanScrollX(1), -deltaX), 0);
                }
                break;
            case MotionEvent.ACTION_UP:
                mVelocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
                int velocityX = (int) mVelocityTracker.getXVelocity();
                fling(velocityX);
                recycleVelocityTracker();
                break;
            case MotionEvent.ACTION_CANCEL:
                recycleVelocityTracker();
                break;
        }
        return super.onTouchEvent(event);
    }

    private void initVelocityTrackerIfNotExists() {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
    }

    private void recycleVelocityTracker() {
        if (mVelocityTracker != null) {
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
    }

    // ACTION_UP事件触发
    private void fling(int velocityX) {
        if (Math.abs(velocityX) > mMinimumVelocity) {
            if (Math.abs(velocityX) > mMaximumVelocity) {
                velocityX = mMaximumVelocity * velocityX / Math.abs(velocityX);
            }
            mScroller.fling(getScrollX(), getScrollY(), -velocityX, 0, 0, mNeedWidth - mWidth, 0, 0);
        }
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), 0);
        }
    }

    void resetInitialStatus() {
        recycleVelocityTracker();
        mScroller.forceFinished(true);
        mScroller.startScroll(0, 0, 0, 0);
    }
}
