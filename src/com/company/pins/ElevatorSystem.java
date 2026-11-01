package com.careerup.pins;

import java.util.*;

/**
 * Elevator System - Manages multiple elevators and assigns requests to the most appropriate elevator.
 * 
 * <p>
 * Problem Overview:
 * This system simulates an elevator control system that manages multiple elevators
 * and handles passenger requests. The system must intelligently assign each request
 * to an elevator based on the elevator's current state (floor, direction, targets).
 * 
 * <p>
 * Key Features:
 * - Multiple elevators: System can manage N elevators simultaneously
 * - Time-based simulation: Tracks elevator movement over time with timestamps
 * - Request assignment: Assigns requests to the most suitable elevator
 * - Direction-aware: Considers elevator direction when determining eligibility
 * 
 * <p>
 * Elevator Assignment Rules:
 * An elevator is eligible to handle a request if:
 * 1. The elevator is idle (not moving) - always eligible
 * 2. The elevator is moving UP and:
 *    - Request direction is UP
 *    - Request floor is at or above the elevator's current floor
 * 3. The elevator is moving DOWN and:
 *    - Request direction is DOWN
 *    - Request floor is at or below the elevator's current floor
 * 
 * Among all eligible elevators, the system chooses the one closest to the request floor.
 * 
 * <p>
 * Two Modes:
 * - Time-aware mode: {@link #handleRequest(int, int, String)} - considers timestamps and simulates movement
 * - Simple mode: {@link #handleRequest(int, String)} - basic assignment without time simulation
 */
public class ElevatorSystem {
    
    /**
     * Represents a single elevator in the system.
     * Each elevator tracks its current position, direction, and queue of target floors.
     */
    static class Elevator {
        int id;                          // Unique identifier for this elevator
        int currentFloor;                // Current floor where the elevator is located
        String direction;                // Current direction: "up", "down", or "idle"
        Queue<Integer> targets;          // Queue of target floors to visit
        int lastUpdateTime;               // Last timestamp when elevator position was updated

        /**
         * Creates a new elevator at the specified floor.
         * 
         * @param id unique identifier for this elevator
         * @param floor initial floor where the elevator starts
         */
        Elevator(int id, int floor) {
            this.id = id;
            this.currentFloor = floor;
            this.direction = "idle";
            this.targets = new ArrayDeque<>();
            this.lastUpdateTime = 0;
        }

        /**
         * Updates elevator position to the given timestamp.
         * Simulates elevator movement by moving one floor per time unit toward targets.
         * 
         * @param timestamp current time to update elevator position to
         */
        void updatePosition(int timestamp) {
            // Calculate time elapsed since last update
            int delta = timestamp - lastUpdateTime;
            lastUpdateTime = timestamp; // Sync time immediately

            if (delta <= 0) return; // No time passed since last update

            // Process movement until time runs out or we have no more targets
            while (delta > 0 && !targets.isEmpty()) {
                int target = targets.peek();
                
                // Case 1: Already at the target floor
                if (currentFloor == target) {
                    targets.poll();     // Remove served target
                    updateDirection();  // Re-evaluate direction based on next targets
                    continue;           // Check next target without consuming time
                }

                // Case 2: Move towards target
                // Calculate distance to next target
                int dist = Math.abs(target - currentFloor);
                
                // Determine how far we can move: either all the way to target, or as far as time allows
                int move = Math.min(dist, delta);

                // Update physical position
                if (target > currentFloor) 
                    currentFloor += move;
                else 
                    currentFloor -= move;

                // Deduct the time consumed by this move
                delta -= move;
            }
        }

        /**
         * Updates the elevator's direction based on the next target.
         * Direction is "idle" if no targets, "up" if next target is above,
         * "down" if next target is below.
         */
        private void updateDirection() {
            if (targets.isEmpty()) {
                direction = "idle";
            } else {
                int next = targets.peek();
                if (next > currentFloor) {
                    direction = "up";
                } else if (next < currentFloor) {
                    direction = "down";
                }
                // If next == currentFloor, direction remains unchanged (will be handled in updateToTime)
            }
        }

        /**
         * Adds a new target floor to the elevator's queue.
         * The elevator will visit this floor after completing current targets.
         * 
         * @param floor target floor to add
         */
        void addTarget(int floor) {
            // If already at the requested floor, consider it served (or logic to open doors)
            // Do not add to queue to avoid leaving and coming back looping behavior
            if (floor == currentFloor) {
                return;
            }
            targets.offer(floor);
            updateDirection();
        }

        @Override
        public String toString() {
            return "Elevator " + id + " @ floor " + currentFloor + " dir=" + direction;
        }
    }

    // -----------------------------
    // Elevator System
    // -----------------------------
    
    /** List of all elevators managed by this system */
    private final List<Elevator> elevators = new ArrayList<>();

    /**
     * Creates a new elevator system with the specified number of elevators.
     * All elevators start at the same initial floor.
     * 
     * @param numElevators number of elevators in the system
     * @param initialFloor starting floor for all elevators
     */
    public ElevatorSystem(int numElevators, int initialFloor) {
        for (int i = 0; i < numElevators; i++) {
            elevators.add(new Elevator(i, initialFloor));
        }
    }

    /**
     * Simple version: Handle a request without considering time.
     * This method assigns the request to the closest eligible elevator based on
     * current positions and directions, without simulating movement over time.
     * 
     * @param requestFloor floor where the request originates
     * @param direction direction of the request: "up" or "down"
     * @return the elevator assigned to handle this request, or null if no eligible elevator found
     */
    public Elevator handleRequest(int requestFloor, String direction) {
        return handleRequest(-1, requestFloor, direction);
    }

    /**
     * Time-aware version: Handle a new request arriving at a specific timestamp.
     * This method first updates all elevators to the current time (simulating movement),
     * then assigns the request to the closest eligible elevator.
     * 
     * @param time timestamp when the request arrives
     * @param requestFloor floor where the request originates
     * @param direction direction of the request: "up" or "down"
     * @return the elevator assigned to handle this request, or null if no eligible elevator found
     */
    public Elevator handleRequest(int time, int requestFloor, String direction) {
        // Step 1: Update all elevators to current time (simulate movement)
        if (time >= 0) {
            for (Elevator e : elevators) {
                e.updatePosition(time);
            }
        }

        // Step 2: Choose the closest eligible elevator
        // Use Stream API: filter eligible elevators and pick the one with minimum absolute distance to the request floor. 
        // If none are eligible return null.
        Elevator best = elevators.stream()
            .filter(e -> isEligible(e, requestFloor, direction))
            .min(Comparator.comparingInt(e -> Math.abs(e.currentFloor - requestFloor)))
            .orElse(null);
            
        // Step 3: Assign the request to the chosen elevator
        if (best != null) {
            best.addTarget(requestFloor);
        }

        return best;
    }

    /**
     * Determines if an elevator is eligible to handle a request based on its current state.
     * 
     * Eligibility rules:
     * - Idle elevators are always eligible
     * - Up-moving elevators are eligible if request is UP and request floor is at or above current floor
     * - Down-moving elevators are eligible if request is DOWN and request floor is at or below current floor
     * 
     * @param e the elevator to check
     * @param reqFloor the floor where the request originates
     * @param reqDir the direction of the request ("up" or "down")
     * @return true if the elevator is eligible, false otherwise
     */
    private boolean isEligible(Elevator e, int reqFloor, String reqDir) {
        // Idle elevators can always handle requests
        if (e.direction.equals("idle"))
            return true;
        // Up-moving elevator can handle up requests if request is at or above current floor
        if (e.direction.equals("up") && reqDir.equals("up") && reqFloor >= e.currentFloor)
            return true;
        // Down-moving elevator can handle down requests if request is at or below current floor
        if (e.direction.equals("down") && reqDir.equals("down") && reqFloor <= e.currentFloor)
            return true;
        return false;
    }

    // -----------------------------
    // Test Cases
    // -----------------------------

    public static void main(String[] args) {
        System.out.println("Running Elevator System Tests...");
        int passed = 0;
        int failed = 0;

        // Test 1: Simple Proximity
        ElevatorSystem sys1 = new ElevatorSystem(2, 0);
        Elevator e1 = sys1.handleRequest(0, 5, "up");
        if (e1 != null && e1.id == 0) passed++; else { System.out.println("Test 1 Failed"); failed++; }

        // Test 2: Direction Eligibility (UP elevator shouldn't take DOWN request)
        sys1.handleRequest(0, 10, "up"); // Elevator 0 is now UP
        Elevator e2 = sys1.handleRequest(0, 5, "down");
        // Elevator 0 is UP at 0. Elevator 1 is IDLE at 0. Should pick Elevator 1.
        if (e2 != null && e2.id == 1) passed++; else { System.out.println("Test 2 Failed: Expected E1"); failed++; }

        // Test 3: Time Simulation
        // Reset system
        ElevatorSystem sys2 = new ElevatorSystem(1, 0);
        sys2.handleRequest(0, 10, "up"); // E0 assigned, moves towards 10
        // At T=5, E0 should be at floor 5
        Elevator e3 = sys2.handleRequest(5, 6, "up"); 
        if (e3 != null && e3.currentFloor == 5) passed++; else { System.out.println("Test 3 Failed: Expected floor 5, got " + (e3==null?"null":e3.currentFloor)); failed++; }

        // Test 4: Idle Priority
        ElevatorSystem sys3 = new ElevatorSystem(2, 5); // Both at 5
        sys3.handleRequest(0, 10, "up"); // E0 becomes UP
        // Req at 2, DOWN. E0 (UP) invalid. E1 (IDLE) valid.
        Elevator e4 = sys3.handleRequest(0, 2, "down");
        if (e4 != null && e4.id == 1) passed++; else { System.out.println("Test 4 Failed: Expected E1 (Idle)"); failed++; }

        System.out.println("Tests Completed. Passed: " + passed + ", Failed: " + failed);
    }
}
