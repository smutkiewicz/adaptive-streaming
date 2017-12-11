package com.company;

import java.util.*;

public class StreamingSimulation {

    private static final double SEGMENT_SIZE = 2.0; //w sekundach
    private static final double OPTIMAL_BUFFER = 30.0; //w sekundach
    private static final double MIN_BUFFER = 10.0; //w sekundach
    private static final double HIGH = 100;
    private static final double LOW = 10;
    private static final int FACTOR = 500;
    private static final int LAMBDA = 50;

    private double time = 0.0;
    private double totalTime = 1000.0;
    private double bandwidth = 0.0;
    private double buffer = 0.0; // w sekundach

    private double bitrate = 2.0; // w Mbps
    private double segmentTime = 0.0; // ile pobrano z segmentu
    private boolean play = false;
    private boolean downloading = false;

    private MyQueue queue = new MyQueue();

    public StreamingSimulation() {
        //TODO
    }

    public void simulate() {

        insertEvents();
        time = 0.0;

        //TODO wyzeruj czas, symulacja w przedziale <time,totalTime>
        while(time < totalTime) {
            //pobierz Event z kolejki queue
            MyEvent e = queue.poll();
            e.showEventDetails();

            if(downloading) {
                double downloadTime = (e.time - time)*bandwidth/bitrate;
                buffer += downloadTime;
                segmentTime += downloadTime;
                //obsłuż proces pobierania if(download)
                //np. (timeOfAnEvent-time)*bandwidth/bitrate
                //zwiększ bufor
                //zwiększ licznik pobranego segmentu
            }

            if(play) {
                //zmniejsz bufor
                buffer -= (e.time - time);
            }

            //TODO Obsługa zdarzeń
            if (e.type == EventType.BandwidthChange) {
                //zmiana bandwidth na tę z Eventu
                bandwidth = e.value;

                if(downloading) {
                    //TODO queue.removeAll(); usuń wszystkie typu DlFinished
                    //??Usuń event DownloadFinished z queue

                    //wystartuj pobieranie
                    //TODO DownloadFinished
                    continueDownloading(e);
                }
            }

            if (e.type == EventType.DownloadFinished) {
                //zaktualizuj licznik pobranego segmentu = 0
                segmentTime = 0.0;

                //jeśli bufor >= 30 (optymalny) to nie pobieraj nic
                if(buffer >= OPTIMAL_BUFFER) {
                    downloading = false;
                } else {
                    //wystartuj pobieranie
                    //TODO DownloadFinished
                    continueDownloading(e);
                }
            }

            if (e.type == EventType.SegmentPlayFinished) {
                if (buffer >= SEGMENT_SIZE) {
                    //skończyliśmy odtwarzać segment
                    //TODO SegmentPlayFinished
                    play(e);
                }
                else {
                    stop();
                }

                if (!downloading && (buffer < OPTIMAL_BUFFER)) {
                    //wystartuj pobieranie
                    //TODO DownloadFinished
                    continueDownloading(e);
                }
            }

            if(!play) {
                //TODO co zrobić, gdy nie odtwarzamy?
                //jeśli bandwidth > bitrate oraz bufor > rozmiarSegmentu ?
                //to zaczynamy odtwarzać
                //KOŃCZYMY ODTWARZANIE WIĘC NOWY EVENT DO KOLEJKI
                //TODO SegmentPlayFinished
                //CZY TO TA SAMA KOLEJKA?
                if(bandwidth > bitrate) {
                    if(buffer >= SEGMENT_SIZE) {
                        play(e);
                    }
                }

                if((bandwidth <= bitrate) && buffer >= MIN_BUFFER) {
                    play(e);
                }

                if(!downloading) {
                    continueDownloading(e);
                }
            }

            time = e.time;
            //dodaj punkty wykresu: czas, bufor, bandwidth, bitrate

        }
    }

    private void insertEvents() {
        //TODO Queue będzie kolejką oczekujących na obsługę eventów
        double myBandwidth = LOW;
        time = 0.0;
        queue.add(new MyEvent(0.0, EventType.BandwidthChange, LOW));

        while (time < totalTime) {
            //TODO zwiększ czas o jakąś losową wartość
            //TODO wrzuć do kolejki oczekujących
            if(myBandwidth == LOW) myBandwidth = HIGH;
            else myBandwidth = LOW;

            //losuj czas
            time += expotentialDistribution(LAMBDA);
            MyEvent e = new MyEvent(time, EventType.BandwidthChange, myBandwidth);
            queue.add(e);
        }
    }

    private double expotentialDistribution(double lambda) {
        //TODO https://stackoverflow.com/questions/29020652/java-exponential-distribution
        Random rand = new Random();
        return FACTOR*Math.log(1-rand.nextDouble())/(-lambda);
    }

    private void continueDownloading(MyEvent e) {
        double videoTime = SEGMENT_SIZE - segmentTime;
        double download_dt = videoTime * bitrate / bandwidth;
        queue.add(new MyEvent(e.time + download_dt, EventType.DownloadFinished, 0.0));
        downloading = true;
    }

    private void play(MyEvent e) {
        queue.add(new MyEvent(e.time + SEGMENT_SIZE, EventType.SegmentPlayFinished, 0.0));
        play = true;
    }

    private void stop() {
        play = false;
    }

    private void changeBand() {
        if(bandwidth == HIGH) bandwidth = LOW;
        else bandwidth = HIGH;
    }

}
