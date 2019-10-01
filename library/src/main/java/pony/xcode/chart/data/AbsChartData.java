package pony.xcode.chart.data;

class AbsChartData {
    private String xAxisText; //x轴文字描述
    private double value; //数值
    private String description; //描述
    private boolean isClickable; //是否可以被点击选择

    AbsChartData(String xAxisText, double value) {
        this(xAxisText, value, true);
    }

    AbsChartData(String xAxisText, double value, boolean isClickable) {
        this(xAxisText, value, null, isClickable);
    }

    AbsChartData(String xAxisText, double value, String description) {
        this(xAxisText, value, description, true);
    }

    AbsChartData(String xAxisText, double value, String description, boolean isClickable) {
        this.xAxisText = xAxisText;
        this.value = value;
        this.description = description;
        this.isClickable = isClickable;
    }

    public String getXAxisText() {
        return xAxisText;
    }

    public double getValue() {
        return value;
    }

    public String getDescription() {
        return description;
    }

    public boolean isClickable() {
        return isClickable;
    }
}
