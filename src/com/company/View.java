package com.company;

import com.company.event.MyEvent;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;

import java.util.List;

public class View {

    private static final String X_LABEL = "Czas";
    private static final String Y_LABEL = "Pasmo/bufor";
    private static final String STAGE_TITLE = "Adaptive Streaming";
    private static final String TITLE = "Symulacja Adaptive Streaming";

    public static final String BUFFER_SERIES_NAME = "Buffer";
    public static final String BANDWIDTH_SERIES_NAME = "Bandwidth";
    public static final String BITRATE_SERIES_NAME = "Bitrate";

    private Stage stage;
    private LineChart<Number,Number> lineChart;

    public View(Stage primaryStage) {
        this.stage = primaryStage;
    }

    public void initView() {

        stage.setTitle(STAGE_TITLE);

        Scene scene  = new Scene(lineChart,800,600);

        stage.setScene(scene);
        stage.show();

    }

    public void initLineChart() {

        final NumberAxis xAxis = new NumberAxis();
        final NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel(X_LABEL);
        yAxis.setLabel(Y_LABEL);

        lineChart = new LineChart<Number,Number>(xAxis,yAxis);
        lineChart.setTitle(TITLE);
        lineChart.setCreateSymbols(false);
    }

    public void addNewSeries(List<MyEvent> events, String seriesName) {

        XYChart.Series series = new XYChart.Series();
        series.setName(seriesName);

        events.forEach(e -> series.getData().add(e.getChartData()));

        lineChart.getData().add(series);
    }

    public void addNewSeries(XYChart.Series series, String seriesName) {
        series.setName(seriesName);
        lineChart.getData().add(series);
    }

}
