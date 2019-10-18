package pony.xcode.chart.data;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/*折线图数据实体类*/
public class LineChartData extends AbsChartData {

    public LineChartData(double value) {
        super("", value);
    }

    public LineChartData(@NonNull String xAxisText, double value) {
        super(xAxisText, value);
    }

    public LineChartData(@NonNull String xAxisText, double value, @Nullable String description) {
        super(xAxisText, value, description);
    }

    public LineChartData(@NonNull String xAxisText, double value, boolean isClickable) {
        super(xAxisText, value, isClickable);
    }

    public LineChartData(@NonNull String xAxisText, double value, @Nullable String description, boolean isClickable) {
        super(xAxisText, value, description, isClickable);
    }
}
