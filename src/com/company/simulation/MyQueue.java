package com.company.simulation;

import com.company.event.EventType;
import com.company.event.MyEvent;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class MyQueue {

    private MyComparator myComparator = new MyComparator();
    private List<MyEvent> list = new ArrayList<>();

    public void add(MyEvent e) {
        list.add(e);
        list.sort(myComparator);
    }

    public MyEvent poll() {
        MyEvent e = list.get(0);
        list.remove(0);
        return e;
    }

    public void removeEventsByType(EventType type) {
        list.removeIf(e -> e.type == type);
    }

    public boolean isEmpty() {
        return list.isEmpty();
    }

    public void clear() {
        list.clear();
    }

    private class MyComparator implements Comparator<MyEvent> {
        @Override
        public int compare(MyEvent e1, MyEvent e2) {
            if (e1.time == e2.time)
                return 0;
            if (e1.time < e2.time)
                return -1;
            return 1;
        }
    }

}
