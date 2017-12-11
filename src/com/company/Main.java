package com.company;

import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    @Override public void start(Stage stage) {

        //TODO Tutaj symulacja
        StreamingSimulation simulation = new StreamingSimulation();
        simulation.simulate();

        View view = new View(stage);
        view.initLineChart();
        view.initView();

        //przykładowe dane
        view.addNewSeries(MyEvent.sampleListOfEventsSeries(), View.BUFFER_SERIES_NAME);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
