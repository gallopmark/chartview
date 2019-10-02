package pony.xcode.chart.data;

/*条形图数据实体类*/
public class BarChartData extends AbsChartData {

    public BarChartData(double value) {
        super(null, value);
    }

    public BarChartData(String xAxisText, double value) {
        super(xAxisText, value);
    }

    public BarChartData(String xAxisText, double value, String description) {
        super(xAxisText, value, description);
    }

    public BarChartData(String xAxisText, double value, boolean isClickable) {
        super(xAxisText, value, null, isClickable);
    }

    public BarChartData(String xAxisText, double value, String description, boolean isClickable) {
        super(xAxisText, value, description, isClickable);
    }
}
