package com.company;

import javafx.scene.chart.XYChart;

public class MyEvent {

    public double time;
    public double value;
    public EventType type;

    public MyEvent(int time, int value) {
        this.time = time;
        this.value = value;
    }

    public MyEvent(double time, EventType type, double val) {
        this.time = time;
        this.type = type;
        this.value = val;
    }

    public void showEventDetails() {
        System.out.println("time: " + time + ", val: " + value + ", type: " + type.toString());
    }

    public XYChart.Data getChartData() {
        //TODO opakowanie danych zdarzenia w obiekt XYChart.Data
        return new XYChart.Data(time, value);
    }

    public static XYChart.Series sampleListOfEventsSeries() {

        XYChart.Series series = new XYChart.Series();

        series.getData().add(new XYChart.Data(1, 23));
        series.getData().add(new XYChart.Data(2, 14));
        series.getData().add(new XYChart.Data(3, 15));
        series.getData().add(new XYChart.Data(4, 24));
        series.getData().add(new XYChart.Data(5, 34));
        series.getData().add(new XYChart.Data(6, 36));
        series.getData().add(new XYChart.Data(7, 22));
        series.getData().add(new XYChart.Data(8, 45));
        series.getData().add(new XYChart.Data(9, 43));
        series.getData().add(new XYChart.Data(10, 17));
        series.getData().add(new XYChart.Data(11, 29));
        series.getData().add(new XYChart.Data(12, 25));

        return series;
    }
}
