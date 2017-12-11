package com.company.event;

import javafx.scene.chart.XYChart;

public class EventLineChartMapper {

    private XYChart.Series bandwidthSeries;
    private XYChart.Series bufferSeries;
    private XYChart.Series bitrateSeries;

    public EventLineChartMapper() {
        bandwidthSeries = new XYChart.Series();
        bufferSeries = new XYChart.Series();
        bitrateSeries = new XYChart.Series();
    }

    public void addToSeries(ChartData d) {
        //czas, bufor, bandwidth, bitrate
        bandwidthSeries.getData().add(new XYChart.Data(d.time, d.bandwidth));
        bufferSeries.getData().add(new XYChart.Data(d.time, d.buffer));
        bitrateSeries.getData().add(new XYChart.Data(d.time, d.bitrate));
    }

    public XYChart.Series getBandwidthSeries() {
        return bandwidthSeries;
    }

    public XYChart.Series getBufferSeries() {
        return bufferSeries;
    }

    public XYChart.Series getBitrateSeries() {
        return bitrateSeries;
    }

}
