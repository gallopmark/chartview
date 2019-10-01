package pony.xcode.chart.data;

/*折线图数据实体类*/
public class LineChartData extends AbsChartData {

    @SuppressWarnings("unused")
    public LineChartData(String xAxisText, double value) {
        super(xAxisText, value);
    }

    @SuppressWarnings("unused")
    public LineChartData(String xAxisText, double value, String description) {
        super(xAxisText, value, description);
    }

    @SuppressWarnings("unused")
    public LineChartData(String xAxisText, double value, boolean isClickable) {
        super(xAxisText, value, isClickable);
    }

    @SuppressWarnings("unused")
    public LineChartData(String xAxisText, double value, String description, boolean isClickable) {
        super(xAxisText, value, description, isClickable);
    }
}
