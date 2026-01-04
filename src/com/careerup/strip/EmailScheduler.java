package com.careerup.strip;

/**
 * -----------------------------------------------------------------------------
 * Subscription Email Notification Scheduler
 * -----------------------------------------------------------------------------
 *
 * You are given a list of users, each with:
 *   - name
 *   - plan
 *   - begin_date (integer day)
 *   - duration (number of days the plan lasts)
 *
 * For each user, we must generate email events in chronological order:
 *
 * PART 1:
 *   On begin_date:          send [Welcome]
 *   15 days before expiry:  send [Upcoming expiration]
 *   On expiry date:         send [Expired]
 *
 *   expiry_date = begin_date + duration
 *
 * PART 2:
 *   Add plan changes:
 *     changes = {name, new_plan, change_date}
 *   On change_date: send [Changed]
 *   After change, the user's plan becomes new_plan.
 *
 * PART 3:
 *   Add renewals:
 *     changes = {name, extension, change_date}
 *   On change_date: send [Renewed]
 *   After renewal, extend duration by extension.
 *
 * Output:
 *   Print events in ascending day order.
 *   Format:
 *       <day>: [EventType] <name>, subscribe in plan <plan>
 *
 * -----------------------------------------------------------------------------
 */

import java.util.*;

public class EmailScheduler {

    /** User subscription info */
    static class User {
        String name;
        String plan;
        int begin;
        int duration;

        User(String name, String plan, int begin, int duration) {
            this.name = name;
            this.plan = plan;
            this.begin = begin;
            this.duration = duration;
        }
    }

    /** Change event: either plan change or renewal */
    static class Change {
        String name;
        String newPlan;   // for plan change
        Integer extension; // for renewal
        int day;

        Change(String name, String newPlan, Integer extension, int day) {
            this.name = name;
            this.newPlan = newPlan;
            this.extension = extension;
            this.day = day;
        }
    }

    /** Event to print */
    static class Event {
        int day;
        String type;
        String name;
        String plan;

        Event(int day, String type, String name, String plan) {
            this.day = day;
            this.type = type;
            this.name = name;
            this.plan = plan;
        }
    }

    /**
     * Generate all events for users + changes (plan changes + renewals)
     */
    public static List<Event> generateEvents(List<User> users, List<Change> changes) {
        Map<String, User> map = new HashMap<>();
        for (User u : users) map.put(u.name, u);

        List<Event> events = new ArrayList<>();

        // 1. Base events (welcome, upcoming, expired)
        for (User u : users) {
            int welcomeDay = u.begin;
            int expireDay = u.begin + u.duration;
            int upcomingDay = expireDay - 15;

            events.add(new Event(welcomeDay, "Welcome", u.name, u.plan));
            events.add(new Event(upcomingDay, "Upcoming expiration", u.name, u.plan));
            events.add(new Event(expireDay, "Expired", u.name, u.plan));
        }

        // 2. Apply changes (plan change or renewal)
        for (Change c : changes) {
            User u = map.get(c.name);
            if (u == null) continue;

            if (c.newPlan != null) {
                // Plan change
                u.plan = c.newPlan;
                events.add(new Event(c.day, "Changed", u.name, u.plan));
            } else if (c.extension != null) {
                // Renewal
                u.duration += c.extension;
                events.add(new Event(c.day, "Renewed", u.name, u.plan));
            }
        }

        // 3. After changes, recalc expiration events
        events.removeIf(e -> e.type.equals("Upcoming expiration") || e.type.equals("Expired"));

        for (User u : users) {
            int expireDay = u.begin + u.duration;
            int upcomingDay = expireDay - 15;

            events.add(new Event(upcomingDay, "Upcoming expiration", u.name, u.plan));
            events.add(new Event(expireDay, "Expired", u.name, u.plan));
        }

        // 4. Sort by day
        events.sort(Comparator.comparingInt(e -> e.day));

        return events;
    }

    /** Print events in required format */
    public static void printEvents(List<Event> events) {
        for (Event e : events) {
            System.out.println(e.day + ": [" + e.type + "] " + e.name +
                    ", subscribe in plan " + e.plan);
        }
    }

    // -------------------------------------------------------------------------
    // Test Cases
    // -------------------------------------------------------------------------
    public static void main(String[] args) {

        System.out.println("=== PART 1 ===");
        List<User> users1 = List.of(
                new User("A", "X", 0, 30),
                new User("B", "Y", 1, 15)
        );
        printEvents(generateEvents(users1, List.of()));

        System.out.println("\n=== PART 2 ===");
        List<Change> changes2 = List.of(
                new Change("A", "Y", null, 5)
        );
        printEvents(generateEvents(users1, changes2));

        System.out.println("\n=== PART 3 ===");
        List<Change> changes3 = List.of(
                new Change("A", "Y", null, 5),
                new Change("B", null, 15, 3) // renewal: +15 days
        );
        printEvents(generateEvents(users1, changes3));
    }
}

