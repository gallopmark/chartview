package pony.xcode.chart.data;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/*条形图数据实体类*/
public class BarChartData extends AbsChartData {

    public BarChartData(double value) {
        super("", value);
    }

    public BarChartData(@NonNull String xAxisText, double value) {
        super(xAxisText, value);
    }


    public BarChartData(@NonNull String xAxisText, double value, @Nullable String description) {
        super(xAxisText, value, description);
    }

    public BarChartData(@NonNull String xAxisText, double value, boolean isClickable) {
        super(xAxisText, value, null, isClickable);
    }

    public BarChartData(@NonNull String xAxisText, double value, @Nullable String description, boolean isClickable) {
        super(xAxisText, value, description, isClickable);
    }
}
