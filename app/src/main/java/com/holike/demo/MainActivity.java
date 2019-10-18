package com.holike.demo;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.widget.LinearLayout;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import pony.xcode.chart.BarChartView;
import pony.xcode.chart.LineChartView;
import pony.xcode.chart.PieChartView;
import pony.xcode.chart.data.BarChartData;
import pony.xcode.chart.data.LineChartData;
import pony.xcode.chart.data.PieChartData;

public class MainActivity extends AppCompatActivity {
    private BarChartView barChartView;
    private List<BarChartData> barChartData = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ColorRandom colorRandom = new ColorRandom(10);
        PieChartView picChart = findViewById(R.id.picChart);
        List<PieChartData> list = new ArrayList<>();
        list.add(new PieChartData("1", 100, (int) colorRandom.getColors().get(0)));
        list.add(new PieChartData("2", 200, (int) colorRandom.getColors().get(1)));
        list.add(new PieChartData("3", 300, (int) colorRandom.getColors().get(2)));
        list.add(new PieChartData("4", 400, (int) colorRandom.getColors().get(3)));
        list.add(new PieChartData("5", 500, (int) colorRandom.getColors().get(4)));
        list.add(new PieChartData("6", 600, (int) colorRandom.getColors().get(5)));
        list.add(new PieChartData("7", 700, (int) colorRandom.getColors().get(6)));
        list.add(new PieChartData("8", 820, (int) colorRandom.getColors().get(7)));
        list.add(new PieChartData("9", 900, (int) colorRandom.getColors().get(8)));
        list.add(new PieChartData("10", 1000, (int) colorRandom.getColors().get(9)));
        picChart.setPieChartDataList(list);
        LineChartView lineChartView = findViewById(R.id.lineChartView);
        List<LineChartData> data = new ArrayList<>();
//        data.add(new LineChartData(1));
//        data.add(new LineChartData(2));
//        data.add(new LineChartData(3.5));
//        data.add(new LineChartData(14));
//        data.add(new LineChartData(13));
//        data.add(new LineChartData(20));
//        data.add(new LineChartData(25));
//        data.add(new LineChartData(7.8));
//        data.add(new LineChartData(29));
//        data.add(new LineChartData(35));
        data.add(new LineChartData("1月", 0));
//        data.add(new LineChartData(0));
//        data.add(new LineChartData(0));
//        data.add(new LineChartData(0));
//        data.add(new LineChartData(0));
//        data.add(new LineChartData(0));
//        data.add(new LineChartData(0));
//        data.add(new LineChartData(0));
//        data.add(new LineChartData(0));
//        data.add(new LineChartData(0));
        lineChartView.withData(data, null).yScaleNum(5)
                .descriptionOvalSize(getResources().getDimensionPixelSize(R.dimen.lineChart_oval_size))
                .descriptionArrowSize(getResources().getDimensionPixelSize(R.dimen.lineChart_arrow_size))
                .descriptionTypeface(Typeface.defaultFromStyle(Typeface.BOLD))
                .start();
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) lineChartView.getLayoutParams();
        params.height = 600;
        barChartView = findViewById(R.id.barChartView);
        barChartData.add(new BarChartData("1月\n10", 1012.1));
        barChartData.add(new BarChartData("2月", 138.8));
//        barChartData.add(new BarChartData(3467.2));
//        barChartData.add(new BarChartData(7.9));
//        barChartData.add(new BarChartData(9.3));
//        barChartData.add(new BarChartData(4.6));
//        barChartData.add(new BarChartData(15.5));
//        barChartData.add(new BarChartData(104.3));
//        barChartData.add(new BarChartData(30.3));
//        barChartData.add(new BarChartData(7.7));
        barChartView.withData(barChartData, null)
                .withSubXAxisTextArray(new String[]{"10.01\n至\n10.31","11.01\n至\n11.30"})
                .barValueAsInt(true)
                .start();
//        runnable();
    }

    private void runnable() {
        barChartView.postDelayed(new Runnable() {
            @Override
            public void run() {
                barChartData = new ArrayList<>();
                barChartData.add(new BarChartData(1012.1));
                barChartData.add(new BarChartData(138.8));
                barChartData.add(new BarChartData(3467.2));
                barChartData.add(new BarChartData(7.9));
                barChartData.add(new BarChartData(9.3));
                barChartData.add(new BarChartData(4.6));
                barChartData.add(new BarChartData(15.5));
                barChartData.add(new BarChartData(104.3));
                barChartData.add(new BarChartData(30.3));
                barChartData.add(new BarChartData(7.7));
                barChartData.add(new BarChartData(138.8));
                barChartData.add(new BarChartData(3467.2));
                barChartData.add(new BarChartData(7.9));
                barChartData.add(new BarChartData(9.3));
                barChartData.add(new BarChartData(4.6));
                barChartData.add(new BarChartData(15.5));
                barChartData.add(new BarChartData(104.3));
                barChartView.withData(barChartData, R.array.line_chart_months)
                        .barValueAsInt(true)
                        .start();
                runnable();
            }
        }, 4000);
    }

    static class ColorRandom {

        private ArrayList<Integer> colorArrays;

        private int count;

        ColorRandom(int count) {
            colorArrays = new ArrayList<>(count);
            this.count = count;
            setColor();
        }

        private void setColor() {
            for (int i = 0; i < count; i++) {
                int color = getColor();
                colorArrays.add(color);
            }
        }

        private Integer getColor() {
            int color = Color.parseColor("#FFA500");
            while (colorArrays.contains(color) || "#FFFFFF".equals(color)) {
                color = getRandColorCode();
                if (!colorArrays.contains(color)) {
                    break;
                }
            }
            return color;
        }

        public ArrayList getColors() {
            return colorArrays;
        }

        private Integer getRandColorCode() {
            String r, g, b;
            Random random = new Random();
            r = Integer.toHexString(random.nextInt(256)).toUpperCase();
            g = Integer.toHexString(random.nextInt(256)).toUpperCase();
            b = Integer.toHexString(random.nextInt(256)).toUpperCase();

            r = r.length() == 1 ? "0" + r : r;
            g = g.length() == 1 ? "0" + g : g;
            b = b.length() == 1 ? "0" + b : b;

            return Color.parseColor("#" + r + g + b);
        }
    }
}
