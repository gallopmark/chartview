package pony.xcode.chart;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextUtils;

import androidx.annotation.Nullable;

import java.math.BigDecimal;

class ChartUtils {

    static int getScreenWidth(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    /*获取最大梯度*/
    static int getMaxGraded(float value) {
        if (value > 10) {
            int i = 1;
            for (; value > 10; ) {
                value = value / 10;
                i = i * 10;
            }
            return (int) (i * (Math.ceil(value)));
        } else {
            return 10;
        }
    }

    static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    static int dp2px(Context context, float dpValue) {
        return (int) (0.5f + dpValue * context.getResources().getDisplayMetrics().density);
    }

    static int getTextWidth(@Nullable String text, Paint paint) {
        if (TextUtils.isEmpty(text)) {
            return 0;
        }
        Rect rect = new Rect();
        paint.getTextBounds(text, 0, text.length(), rect);
        return rect.width();
    }

    /**
     * 提供精确的除法运算方法div
     *
     * @param value1 被除数
     * @param value2 除数
     * @return 两个参数的商
     */
    static double div(double value1, double value2) {
        BigDecimal b1 = new BigDecimal(Double.toString(value1));
        BigDecimal b2 = new BigDecimal(Double.toString(value2));
        //默认保留两位会有错误，这里设置保留小数点后4位
        return b1.divide(b2, 2, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    static float div(float value1, float value2) {
        BigDecimal b1 = new BigDecimal(Float.toString(value1));
        BigDecimal b2 = new BigDecimal(Float.toString(value2));
        //默认保留两位会有错误，这里设置保留小数点后4位
        return b1.divide(b2, 2, BigDecimal.ROUND_HALF_UP).floatValue();
    }
}
