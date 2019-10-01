package pony.xcode.chart.data;

/*条形图数据实体类*/
public class BarChartData extends AbsChartData {

    @SuppressWarnings("unused")
    public BarChartData(String xAxisText, double value) {
        super(xAxisText, value);
    }

    @SuppressWarnings("unused")
    public BarChartData(String xAxisText, double value, String description) {
        super(xAxisText, value, description);
    }

    @SuppressWarnings("unused")
    public BarChartData(String xAxisText, double value, boolean isClickable) {
        super(xAxisText, value, null, isClickable);
    }

    @SuppressWarnings("unused")
    public BarChartData(String xAxisText, double value, String description, boolean isClickable) {
        super(xAxisText, value, description, isClickable);
    }
}
