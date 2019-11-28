package pony.xcode.chart.data;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/*条形图数据实体类*/
public class BarChartData extends AbsChartData {
    private int barColor;

    public BarChartData(double value) {
        super("", value);
    }

    public BarChartData(double value, @ColorInt int barColor) {
        super("", value);
        this.barColor = barColor;
    }

    public BarChartData(@NonNull String xAxisText, double value) {
        super(xAxisText, value);
    }

    public BarChartData(@NonNull String xAxisText, double value, @ColorInt int barColor) {
        super(xAxisText, value, null);
        this.barColor = barColor;
    }

    public BarChartData(@NonNull String xAxisText, double value, @Nullable String description) {
        super(xAxisText, value, description);
    }

    public BarChartData(@NonNull String xAxisText, double value, @ColorInt int barColor, @Nullable String description) {
        super(xAxisText, value, description);
        this.barColor = barColor;
    }

    public int getBarColor() {
        return barColor;
    }
}
