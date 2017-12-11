package com.company;

/**
 * Created by Admin on 2017-12-10.
 */
public enum EventType {
    DownloadFinished(1),
    SegmentPlayFinished(2),
    BandwidthChange(3);

    int id;

    EventType(int id) {
        this.id = id;
    }
}
