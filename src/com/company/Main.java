package com.company;

import com.company.event.EventLineChartMapper;
import com.company.event.MyEvent;
import com.company.simulation.StreamingSimulation;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    @Override public void start(Stage stage) {

        EventLineChartMapper mapper = new EventLineChartMapper();
        StreamingSimulation simulation = new StreamingSimulation(mapper);
        simulation.simulate();

        View view = new View(stage);
        view.initLineChart();
        view.initView();

        //dodaj punkty wykresu: czas, bufor, bandwidth, bitrate
        view.addNewSeries(mapper.getBandwidthSeries(), View.BANDWIDTH_SERIES_NAME);
        view.addNewSeries(mapper.getBufferSeries(), View.BUFFER_SERIES_NAME);
        view.addNewSeries(mapper.getBitrateSeries(), View.BITRATE_SERIES_NAME);
    }

    public static void addSampleSeries(View view) {
        view.addNewSeries(MyEvent.sampleListOfEventsSeries(), View.BUFFER_SERIES_NAME);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
