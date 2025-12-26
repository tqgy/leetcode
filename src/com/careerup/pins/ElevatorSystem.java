package com.careerup.pinterest;

import java.util.*;

/*
 * ElevatorSystem
 *
 * - Elevator: [<floor>, <state>] where floor is a string number (e.g., "1")
 *   and state is one of "up", "down", or "idle".
 * - Request:  [<floor>, <direction>] where direction is "up" or "down".
 *
 * Assignment rules (applied in order):
 *  1) Idle elevators can accept any call.
 *  2) Elevator moving "up" is eligible if request.direction == "up" and
 *     request.floor >= elevator.floor (elevator is moving toward passenger).
 *  3) Elevator moving "down" is eligible if request.direction == "down" and
 *     request.floor <= elevator.floor.
 *  4) If multiple elevators are eligible, pick the one with smallest
 *     absolute floor difference; ties may be broken arbitrarily.
 */
public class ElevatorSystem {

    static class Elevator {
        int floor;
        String state; // "up", "down", "idle"

        Elevator(String floor, String state) {
            this.floor = Integer.parseInt(floor);
            this.state = state;
        }
    }

    static class Request {
        int floor;
        String direction; // "up" or "down"

        Request(String floor, String direction) {
            this.floor = Integer.parseInt(floor);
            this.direction = direction;
        }
    }

    /**
     * Assigns an eligible elevator to respond to the request.
     *
     * Rules:
     * - Idle elevators can answer any call.
     * - An elevator moving "up" is eligible only when the passenger requests
     *   "up" and the passenger's floor is at or above the elevator (the elevator
     *   is moving toward the passenger).
     * - Similarly for "down": eligible only when passenger requests "down" and
     *   the passenger's floor is at or below the elevator.
     * - Among eligible elevators, choose the one with the smallest absolute
     *   floor difference to the passenger; if there's a tie any closest one may
     *   be chosen.
     *
     * @param elevators list of available elevators
     * @param req passenger request
     * @return chosen Elevator, or null if none are eligible
     */
    public static Elevator assignElevator(List<Elevator> elevators, Request req) {

        // // Collect eligible elevators according to the rules.
        // List<Elevator> eligible = new ArrayList<>();
        // for (Elevator e : elevators) {
        //     if (isEligible(e, req)) {
        //         eligible.add(e);
        //     }
        // }

        // // No elevator can take the request
        // if (eligible.isEmpty())
        //     return null;

        // // Choose the closest elevator by absolute distance to the request floor
        // eligible.sort(Comparator.comparingInt(e -> Math.abs(e.floor - req.floor)));

        // return eligible.get(0);
        // Use Stream API: filter eligible elevators and pick the one with minimum
        // absolute distance to the request floor. If none are eligible return null.
        return elevators.stream()
                .filter(e -> isEligible(e, req))
                .min(Comparator.comparingInt(e -> Math.abs(e.floor - req.floor)))
                .orElse(null);
    }

    /**
     * Checks whether a single elevator is eligible to answer a given request.
     *
     * The method encodes the three eligibility rules described above. It does
     * no side-effects and simply returns whether the elevator can respond now.
     */
    private static boolean isEligible(Elevator e, Request req) {

        // Rule 1 — idle elevators can always accept the call
        if ("idle".equals(e.state))
            return true;

        // Rule 2 — elevator moving up: passenger must want to go up and be
        // located at or above the elevator's current floor
        if ("up".equals(e.state) && "up".equals(req.direction) && req.floor >= e.floor) {
            return true;
        }

        // Rule 3 — elevator moving down: passenger must want to go down and be
        // located at or below the elevator's current floor
        if ("down".equals(e.state) && "down".equals(req.direction) && req.floor <= e.floor) {
            return true;
        }

        return false;
    }

    /**
     * Runs a set of small in-file test cases and prints PASS/FAIL for each.
     */
    public static void main(String[] args) {
        Object[][] tests = new Object[][]{
            // elevators, request, expected floor (Integer) or null if none
            { Arrays.asList(new Elevator("3", "idle"), new Elevator("7", "up"), new Elevator("1", "down")), new Request("5", "up"), Integer.valueOf(3) },
            { Arrays.asList(new Elevator("2", "up"), new Elevator("6", "down")), new Request("5", "up"), Integer.valueOf(2) },
            { Arrays.asList(new Elevator("10", "down"), new Elevator("6", "down")), new Request("5", "down"), Integer.valueOf(6) },
            { Arrays.asList(new Elevator("1", "up"), new Elevator("3", "up")), new Request("2", "up"), 1 }, 
            { Arrays.asList(new Elevator("4", "down"), new Elevator("2", "down")), new Request("3", "down"), 4 }, 
            { Arrays.asList(new Elevator("5", "up"), new Elevator("7", "up")), new Request("4", "down"), null }, // none eligible
            { Arrays.asList(new Elevator("2", "idle"), new Elevator("4", "idle")), new Request("3", "down"), Arrays.asList(2,4) } // tie: any closest (2 or 4)
        };

        int failures = 0;
        int i = 0;
        for (Object[] t : tests) {
            i++;
            @SuppressWarnings("unchecked")
            List<Elevator> elevs = (List<Elevator>) t[0];
            Request req = (Request) t[1];
            Object expected = t[2];

            Elevator got = assignElevator(elevs, req);
            boolean pass;
            if (expected == null) {
                pass = (got == null);
            } else if (expected instanceof Integer) {
                pass = (got != null && got.floor == (Integer) expected);
            } else {
                @SuppressWarnings("unchecked")
                List<Integer> allowed = (List<Integer>) expected;
                pass = (got != null && allowed.contains(got.floor));
            }

            if (pass) {
                System.out.printf("PASS test %d: request=(%d,%s) -> %s%n", i, req.floor, req.direction, got == null ? "null" : (got.floor + "," + got.state));
            } else {
                System.out.printf("FAIL test %d: request=(%d,%s) -> %s (expected=%s)%n", i, req.floor, req.direction, got == null ? "null" : (got.floor + "," + got.state), expected);
                failures++;
            }
        }

        if (failures > 0) {
            System.out.printf("\n%d test(s) failed.%n", failures);
            System.exit(1);
        } else {
            System.out.println("\nAll tests passed.");
        }
    }
}
