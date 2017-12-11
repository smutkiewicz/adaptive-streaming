package com.company;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

public class MyQueue {

    private MyComparator myComparator = new MyComparator();
    public List<MyEvent> list = new ArrayList<>();

    public void add(MyEvent e) {
        list.add(e);
        list.sort(myComparator);
    }
    public MyEvent poll() {
        MyEvent e = list.get(0);
        list.remove(0);
        return e;
    }

    public boolean isEmpty() {
        return list.isEmpty();
    }

    public void removeEventsByType(EventType type) {
        list.removeIf(e -> e.type == type);
    }

    public void clear() {
        list.clear();
    }

    private class MyComparator implements Comparator<MyEvent> {
        @Override
        public int compare(MyEvent a, MyEvent b) {
            if (a.time == b.time)
                return 0;
            if (a.time < b.time)
                return -1;
            return 1;
        }
    }

}
