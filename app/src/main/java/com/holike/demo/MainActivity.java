package com.holike.demo;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.LinearLayout;

import com.holike.demo.widget.PieChartView2;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        PieChartView2 view = findViewById(R.id.pcv);
        List<PieChartView2.PieModel> pieModelList = new ArrayList<>();
        ColorRandom colorRandom = new ColorRandom(10);
        for (int i = 0; i < 5; i++) {
            int colors = (int) colorRandom.getColors().get(i);
            if (i == 0) {
                pieModelList.add(new PieChartView2.PieModel(colors, 0.1f, true));
            } else {
                pieModelList.add(new PieChartView2.PieModel(colors, 0.3f));
            }
        }
        view.setData(pieModelList);
        PieChartView picChart = findViewById(R.id.picChart);
        List<PieChartData> list = new ArrayList<>();
        list.add(new PieChartData("1", 100, (int) colorRandom.getColors().get(0)));
        list.add(new PieChartData("2", 120, (int) colorRandom.getColors().get(1)));
        list.add(new PieChartData("3", 150, (int) colorRandom.getColors().get(2)));
        list.add(new PieChartData("4", 200, (int) colorRandom.getColors().get(3)));
        list.add(new PieChartData("5", 250, (int) colorRandom.getColors().get(4)));
        list.add(new PieChartData("6", 300, (int) colorRandom.getColors().get(5)));
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
        data.add(new LineChartData(0));
        data.add(new LineChartData(0));
        data.add(new LineChartData(0));
        data.add(new LineChartData(0));
        data.add(new LineChartData(0));
        data.add(new LineChartData(0));
        data.add(new LineChartData(0));
        data.add(new LineChartData(0));
        data.add(new LineChartData(0));
        data.add(new LineChartData(0));
        lineChartView.withData(data, getResources().getStringArray(R.array.line_chart_months), "万").yScaleNum(5)
                .descriptionOvalSize(getResources().getDimensionPixelSize(R.dimen.lineChart_oval_size))
                .descriptionArrowSize(getResources().getDimensionPixelSize(R.dimen.lineChart_arrow_size))
                .yAxisTypeface(Typeface.defaultFromStyle(Typeface.BOLD_ITALIC))
                .descriptionTypeface(Typeface.defaultFromStyle(Typeface.BOLD))
                .start();
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) lineChartView.getLayoutParams();
        params.height = 600;
        BarChartView barChartView = findViewById(R.id.barChartView);
        List<BarChartData> barChartData = new ArrayList<>();
        barChartData.add(new BarChartData(19.1));
        barChartData.add(new BarChartData(17.0));
        barChartData.add(new BarChartData(4.2));
        barChartData.add(new BarChartData(14));
        barChartData.add(new BarChartData(13));
        barChartData.add(new BarChartData(100));
        barChartView.withData(barChartData, R.array.bar_chart_months, "%").start();
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
