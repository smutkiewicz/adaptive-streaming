package com.company.simulation;

import com.company.event.ChartData;
import com.company.event.EventLineChartMapper;
import com.company.event.EventType;
import com.company.event.MyEvent;

import java.util.*;

public class StreamingSimulation {

    //stałe bufora
    //TODO
    private static final double SEGMENT_SIZE = 2.0; //w sekundach
    private static final double OPTIMAL_BUFFER = 30.0; //w sekundach
    private static final double MIN_BUFFER = 10.0; //w sekundach

    //bandwidth
    private static final double HIGH = 5.0; // Mbps
    private static final double LOW = 1.0; // Mbps
    private double bandwidth = 0.0;

    private double bitrate = 2.0; // w Mbps (stałe)

    private double time = 0.0;
    private double totalTime = 200;
    private double buffer = 0.0; // w sekundach
    private double segmentTime = 0.0; // ile pobrano z segmentu

    //rozkład ekspotencjalny
    private int factor = 50;
    private double lambda = 2.4;

    //aktualny stan
    private boolean playing = false;
    private boolean downloading = false;

    private MyQueue queue = new MyQueue();
    private EventLineChartMapper mapper;

    public StreamingSimulation(EventLineChartMapper mapper) {
        this.mapper = mapper;
    }

    public void setExpotentialParams(int factor, double lambda) {
        this.factor = factor;
        this.lambda = lambda;
    }

    public void setTotalTime(double totalTime) {
        this.totalTime = totalTime;
    }

    public void simulate() {

        insertEvents();
        time = 0.0;

        //wyzeruj czas, symulacja w przedziale <time,totalTime>
        while(time < totalTime) {

            MyEvent e = queue.poll();
            e.showEventDetails();

            if(downloading) {
                double downloadTime = (e.time - time)*bandwidth/bitrate;
                buffer += downloadTime;
                segmentTime += downloadTime;
            }

            if(playing) {
                //TODO
                buffer -= (e.time - time);
                if(buffer < 0) buffer = 0;
            }

            handleEvent(e);

            if(!playing) {
                if(bandwidth > bitrate && buffer >= SEGMENT_SIZE) {
                    play(e);
                }

                if(bandwidth <= bitrate && buffer >= MIN_BUFFER) {
                    play(e);
                }

                if(!downloading) {
                    continueDownloading(e);
                }
            }

            time = e.time;
            mapper.addToSeries(new ChartData(e.time, buffer, 5*bandwidth, bitrate));
        }
    }

    private void insertEvents() {
        double myBandwidth = LOW;
        time = 0.0;
        queue.add(new MyEvent(0.0, EventType.BandwidthChange, LOW));

        while (time < totalTime) {

            if(myBandwidth == LOW) myBandwidth = HIGH;
            else myBandwidth = LOW;

            time += expotentialDistribution(lambda); //losuj czas
            MyEvent e = new MyEvent(time, EventType.BandwidthChange, myBandwidth);
            queue.add(e);
        }
    }

    private void handleEvent(MyEvent e) {
        switch (e.type) {
            case BandwidthChange:
                bandwidth = e.value; //zmiana bandwidth na tę z Eventu

                if (downloading) {
                    queue.removeEventsByType(EventType.DownloadFinished);
                    continueDownloading(e);
                }

                break;
            case DownloadFinished:
                segmentTime = 0.0; //zaktualizuj licznik pobranego segmentu

                //jeśli bufor >= 30 (optymalny) to nie pobieraj nic
                if (buffer >= OPTIMAL_BUFFER) {
                    downloading = false;
                } else {
                    continueDownloading(e); //wystartuj pobieranie
                }

                break;
            case SegmentPlayFinished:
                if (buffer >= SEGMENT_SIZE) {
                    play(e); //skończyliśmy odtwarzać segment
                } else {
                    stop();
                }

                if (!downloading && (buffer < OPTIMAL_BUFFER)) {
                    continueDownloading(e); //wystartuj pobieranie
                }

                break;
        }
    }

    private double expotentialDistribution(double lambda) {
        Random rand = new Random();
        return factor*Math.log(1-rand.nextDouble())/(-lambda);
    }

    private void continueDownloading(MyEvent e) {
        double videoTime = SEGMENT_SIZE - segmentTime;
        double download = videoTime * bitrate / bandwidth;
        queue.add(new MyEvent(e.time + download, EventType.DownloadFinished, 0.0));
        downloading = true;
    }

    private void play(MyEvent e) {
        queue.add(new MyEvent(e.time + SEGMENT_SIZE, EventType.SegmentPlayFinished, 0.0));
        playing = true;
    }

    private void stop() {
        playing = false;
    }

}
