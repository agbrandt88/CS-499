package com.example.brandt_cs360_projecttwo_eventtrackingapp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;

// Reference: https://www.geeksforgeeks.org/java/treeset-in-java-with-examples/
// Reference: https://www.baeldung.com/java-tree-set
// Reference: https://techutils.wordpress.com/2014/11/13/how-to-check-if-2-date-ranges-overlap-in-java/
// Reference: https://hamzeen.medium.com/locate-overlaps-in-java-date-intervals-f9a0716bb2dd

// Uses TreeSet for event ordering & HashMap to support event lookup by ID.
public class EventScheduler {

    private TreeSet<EventTable> eventTreeSet;
    private HashMap<Integer, EventTable> eventHashMap = new HashMap<>();

    public EventScheduler() {

        eventTreeSet = new TreeSet<>((previousEvent, nextEvent) -> {

            int eventCompare = Long.compare(previousEvent.eventStart, nextEvent.eventStart);

            if (eventCompare != 0) {
                return eventCompare;
            }

            // Used for when events have same start time, ID keeps events discernible for overlap check.
            return Integer.compare(previousEvent.id, nextEvent.id);

        });
    }

    // Clear then rebuild structures using current event list.
    public void setEventList(List<EventTable> eventList) {

        eventTreeSet.clear();
        eventHashMap.clear();

        for (EventTable event : eventList) {

            addEvent(event);

        }

    }

    // Converts TreeSet into List for display.
    public List<EventTable> getEventList() {

        return new ArrayList<>(eventTreeSet);
    }

    public EventTable getEvent(int eventId) {

        return eventHashMap.get(eventId);
    }

    public boolean overlaps(EventTable event) {

        EventTable currentEvent = eventHashMap.get(event.id);

        // Interim remove current event so it cannot overlap with itself upon check.
        if (currentEvent != null) {
            eventTreeSet.remove(currentEvent);
        }

        // Check surrounding events for overlap leveraging TreeSet architecture.
        EventTable previousEvent = eventTreeSet.lower(event);
        EventTable nextEvent = eventTreeSet.higher(event);

        boolean overlap = false;

        if (previousEvent != null) {

            if (previousEvent.eventEnd > event.eventStart) {
                overlap = true;
            }
        }

        if (!overlap && nextEvent !=null) {

            if (nextEvent.eventStart < event.eventEnd) {
                overlap = true;
            }
        }

        // Adds back event previously interim removed as overlap evaluation finished.
        if (currentEvent != null) {

            eventTreeSet.add(currentEvent);
        }

        return overlap;

    }

    // Adds to both structures to ensure data is the same.
    public void addEvent(EventTable event) {

        eventTreeSet.add(event);
        eventHashMap.put(event.id, event);

    }

    // Pulls event via ID and removes from the TreeSet and HashMap.
    public void removeEvent(int eventId) {

        EventTable event = eventHashMap.remove(eventId);

        if (event != null) {
            eventTreeSet.remove(event);
        }
    }

}
