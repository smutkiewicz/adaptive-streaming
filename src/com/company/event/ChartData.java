package com.company.event;


public class ChartData {

    public double time;
    public double buffer;
    public double bandwidth;
    public double bitrate;

    public ChartData(double time, double buffer,
                     double bandwidth, double bitrate) {
        this.time = time;
        this.buffer = buffer;
        this.bitrate = bitrate;
        this.bandwidth = bandwidth;
    }

}
