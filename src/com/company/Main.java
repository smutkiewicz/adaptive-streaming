package com.company;

import com.company.event.EventLineChartMapper;
import com.company.event.MyEvent;
import com.company.simulation.StreamingSimulation;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    private EventLineChartMapper mapper;
    private StreamingSimulation simulation;
    private View view;

    @Override public void start(Stage stage) {

        mapper = new EventLineChartMapper();
        simulation = new StreamingSimulation(mapper);
        simulation.setExpotentialParams(100, 2.0);
        simulation.setTotalTime(300);
        simulation.simulate();

        view = new View(stage);
        view.initLineChart();
        view.initView();

        addSimulationSeries();
    }

    public void addSimulationSeries() {
        //dodaj punkty wykresu: czas, bufor, bandwidth, bitrate
        view.addNewSeries(mapper.getBufferSeries(), View.BUFFER_SERIES_NAME);
        view.addNewSeries(mapper.getBandwidthSeries(), View.BANDWIDTH_SERIES_NAME);
        view.addNewSeries(mapper.getBitrateSeries(), View.BITRATE_SERIES_NAME);
    }

    public static void addSampleSeries(View view) {
        view.addNewSeries(MyEvent.sampleListOfEventsSeries(), View.BUFFER_SERIES_NAME);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
