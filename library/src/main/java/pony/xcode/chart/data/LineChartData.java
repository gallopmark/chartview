package pony.xcode.chart.data;

/*折线图数据实体类*/
public class LineChartData extends AbsChartData {

    public LineChartData(double value) {
        super(null, value);
    }

    public LineChartData(String xAxisText, double value) {
        super(xAxisText, value);
    }

    public LineChartData(String xAxisText, double value, String description) {
        super(xAxisText, value, description);
    }

    public LineChartData(String xAxisText, double value, boolean isClickable) {
        super(xAxisText, value, isClickable);
    }

    public LineChartData(String xAxisText, double value, String description, boolean isClickable) {
        super(xAxisText, value, description, isClickable);
    }
}
