package com.company.simulation;

import com.company.event.ChartData;
import com.company.event.EventLineChartMapper;
import com.company.event.EventType;
import com.company.event.MyEvent;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class StreamingSimulation {

    //stałe bufora
    private static final double SEGMENT_SIZE = 2.0; //w sekundach
    private static final double OPTIMAL_BUFFER = 30.0; //w sekundach
    private static final double MIN_BUFFER = 10.0; //w sekundach

    //bandwidth
    private static final double HIGH = 5.0; // Mbps
    private static final double LOW = 1.0; // Mbps
    private double highRange = HIGH;
    private double lowRange = LOW;
    private double bandwidth = 0.0;

    //bitrate
    private static final double INITIAL_BITRATE = 2.0; // w Mbps (stałe)
    private ArrayList<Double> possibleBitrates;
    private double bitrate = 2.0; // w Mbps

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
        this.possibleBitrates = new ArrayList<>();
        this.mapper = mapper;
    }

    public void setPossibleBitrates(List<Double> bitrates) {
        possibleBitrates.addAll(bitrates);

        if(possibleBitrates.isEmpty()) {
            possibleBitrates.add(2.0);
            bitrate = 2.0;
        }
    }

    public void setBandwidthRange(double lowRange, double highRange) {
        this.lowRange = lowRange;
        this.highRange = highRange;
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
            mapper.addToSeries(new ChartData(e.time, buffer, bandwidth, bitrate));
        }
    }

    private void insertEvents() {
        double myBandwidth;
        time = 0.0;
        queue.add(new MyEvent(0.0, EventType.BandwidthChange, LOW));

        while (time < totalTime) {

            myBandwidth = setRandomBandwidth();

            time += expotentialDistribution(lambda); //losuj czas
            MyEvent e = new MyEvent(time, EventType.BandwidthChange, myBandwidth);
            queue.add(e);
        }
    }

    private void handleEvent(MyEvent e) {
        switch (e.type) {
            case BandwidthChange:
                bandwidth = e.value; //zmiana bandwidth na tę z Eventu
                setNewBitrate(e.value);

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

    private double setRandomBandwidth() {
        return ThreadLocalRandom.current().nextDouble(lowRange, highRange);
    }

    private void setNewBitrate(double bandwidthValue) {
        ArrayList<Double> bitrates = getPossibleBitrates();
        bitrates.sort((b1, b2) -> b1.compareTo(b2));


        for(int i = 0; i < bitrates.size(); i++) {
            double b = bitrates.get(i);
            bitrate = getInitialBitrate();

            if(b >= bandwidthValue) {
                break;
            } else {
                bitrate = b;
            }
        }
    }

    private void continueDownloading(MyEvent e) {
        double videoTime = SEGMENT_SIZE - segmentTime;
        double download = videoTime * bitrate / bandwidth;
        queue.add(new MyEvent(e.time + download, EventType.DownloadFinished, 0.0));
        downloading = true;
    }

    private ArrayList<Double> getPossibleBitrates() {
        if(possibleBitrates.isEmpty()) {
            ArrayList<Double> bitrates = new ArrayList<>();
            bitrates.add(INITIAL_BITRATE);
            return bitrates;
        } else {
            return possibleBitrates;
        }
    }

    private double getInitialBitrate() {
        if(possibleBitrates.isEmpty()) {
            return INITIAL_BITRATE;
        } else {
            return possibleBitrates.get(0);
        }
    }

    private void play(MyEvent e) {
        queue.add(new MyEvent(e.time + SEGMENT_SIZE, EventType.SegmentPlayFinished, 0.0));
        playing = true;
    }

    private void stop() {
        playing = false;
    }

}
