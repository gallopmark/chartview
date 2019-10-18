package pony.xcode.chart.data;

import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;

//饼状图数据实体类
public class PieChartData {

    private String type;

    private float value;

    private int colorId;

    private float percent; //百分占比

    private float pieStart; //开始角度

    private float pieSweep; //扫过的弧度

    public PieChartData(float value, @ColorInt int colorId) {
        this.value = value;
        this.colorId = colorId;
    }

    public PieChartData(@Nullable String type, float value, @ColorInt int colorId) {
        this.type = type;
        this.value = value;
        this.colorId = colorId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setValue(float value) {
        this.value = value;
    }

    public float getValue() {
        return value;
    }

    public int getColorId() {
        return colorId;
    }

    public void setColorId(int colorId) {
        this.colorId = colorId;
    }

    public float getPercent() {
        return percent;
    }

    public void setPercent(float percent) {
        this.percent = percent;
    }

    public float getPieStart() {
        return pieStart;
    }

    public void setPieStart(float pieStart) {
        this.pieStart = pieStart;
    }

    public float getPieSweep() {
        return pieSweep;
    }

    public void setPieSweep(float pieSweep) {
        this.pieSweep = pieSweep;
    }
}
