package com.example.brandt_cs360_projecttwo_eventtrackingapp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;

// Reference: https://www.influxdata.com/blog/java-date-comparison-tutorial-influxdb/
// Reference: https://www.geeksforgeeks.org/java/treeset-in-java-with-examples/
// Reference: https://www.baeldung.com/java-tree-set
// Reference: https://techutils.wordpress.com/2014/11/13/how-to-check-if-2-date-ranges-overlap-in-java/
// Reference: https://hamzeen.medium.com/locate-overlaps-in-java-date-intervals-f9a0716bb2dd

// Uses TreeSet for event ordering & HashMap to support event query.
public class EventScheduler {

    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("M/d/yyyy");
    private DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("H:m");
    private TreeSet<EventTable> eventTreeSet;
    private HashMap<Integer, EventTable> eventHashMap = new HashMap<>();

    public EventScheduler() {

        eventTreeSet = new TreeSet<>((previousEvent, nextEvent) -> {

            LocalDateTime previousEventStart = LocalDateTime.of(
                    LocalDate.parse(previousEvent.eventDate, dateFormatter), LocalTime.parse(previousEvent.eventTime, timeFormatter));
            LocalDateTime nextEventStart = LocalDateTime.of(
                    LocalDate.parse(nextEvent.eventDate, dateFormatter), LocalTime.parse(nextEvent.eventTime, timeFormatter));

            int eventCompare = previousEventStart.compareTo(nextEventStart);

            if (eventCompare != 0) {
                return eventCompare;
            }

            // Enforces no event overlapping via ID check.
            return Integer.compare(previousEvent.id, nextEvent.id);

        });
    }

    // Clear then reorder list for event addition.
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

        // Prevents event update from being read as overlapping itself via interim removal.
        if (currentEvent != null) {
            eventTreeSet.remove(currentEvent);
        }

        // Check surrounding events for overlap leveraging TreeSet architecture.
        EventTable previousEvent = eventTreeSet.lower(event);
        EventTable nextEvent = eventTreeSet.higher(event);

        LocalDate date = LocalDate.parse(event.eventDate, dateFormatter);
        LocalDateTime startTime = LocalDateTime.of(date, LocalTime.parse(event.eventTime, timeFormatter));
        LocalDateTime endTime = LocalDateTime.of(date, LocalTime.parse(event.eventEndTime, timeFormatter));

        boolean overlap = false;

        if (previousEvent != null) {

            LocalDateTime previousEventEnd = LocalDateTime.of(
                    LocalDate.parse(previousEvent.eventDate, dateFormatter),
                    LocalTime.parse(previousEvent.eventEndTime, timeFormatter));

            if (previousEventEnd.isAfter(startTime)) {
                overlap = true;
            }
        }

        if (!overlap && nextEvent !=null) {

            LocalDateTime nextEventStart = LocalDateTime.of(
                    LocalDate.parse(nextEvent.eventDate, dateFormatter),
                    LocalTime.parse(nextEvent.eventTime, timeFormatter));

            if (nextEventStart.isBefore(endTime)) {
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

    // Pulls event via ID and removes from each data structure.
    public void removeEvent(int eventId) {

        EventTable event = eventHashMap.remove(eventId);

        if (event != null) {
            eventTreeSet.remove(event);
        }
    }

}
