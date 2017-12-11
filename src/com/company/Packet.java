package com.company;

public class Packet {

    private int size;
    private int downloaded;

    public Packet() {
        //TODO rozmiary pakietów?
        this.size = 2750*2;
    }

    public int getSize() {
        return size;
    }

    public int getRemaining() {
        int remaining = size - downloaded;

        if(remaining <= 0) return 0;
        else return remaining;
    }

    public void setDownloadedValue(int bandwidth, int time) {
        downloaded -= time*bandwidth;
    }
}
