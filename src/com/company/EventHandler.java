package com.company;

import java.util.ArrayList;

public class EventHandler {

    private ArrayList<MyEvent> events = new ArrayList<>();

    public void insertEvent(EventType type, int time, int value) {
        //TODO typ eventu?
        events.add(new MyEvent(time, value));
    }

}
