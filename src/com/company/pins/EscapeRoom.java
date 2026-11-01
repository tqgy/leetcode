package com.careerup.pins;

import java.util.*;

/**
 * EscapeRoom
 *
 * A virtual escape room system where participants progress through a series of
 * rooms. Each participant starts in room 0 and can advance to the next room.
 * The system tracks participants' current rooms, supports incrementing their
 * room, counting participants in specific rooms, and retrieving a leaderboard
 * of top participants.
 *
 * Key Features: 
 *      - Register participants with unique IDs. 
 *      - Increment a participant's room (if not already in the last room). 
 *      - Count how many participants are in a specific room. 
 *      - Retrieve the top K participants based on their current room and registration order.
 * 
 * This is a priority‑based real‑time leaderboard problem, and the trick is to maintain:
 * 
 *      - Room populations → fast counts 
 *      - Participant progress → room index Stable
 *      - ordering → earlier entrants win ties 
 *      - Top‑K queries → efficient retrieval
 * 
 * Data Structures: 
 *      - HashMap for participant lookup by ID. Stores each player's current room and their entry order.
 *      - Array roomCounts Tracks how many players are in each room.
 *      - TreeSet for ordered leaderboard based on room (desc), entryOrder (asc), ID (asc). 
 *          O(log n) insert
 *          O(log n) remove
 *          O(k) top‑k iteration
 */
public class EscapeRoom {

    private static class Player {
        String id;
        int room;
        long order; // earlier = smaller

        Player(String id, int room, long order) {
            this.id = id;
            this.room = room;
            this.order = order;
        }
    }

    private final int numRooms;
    private final Map<String, Player> players = new HashMap<>();
    private final int[] roomCounts;
    private long globalOrder = 0;

    // Leaderboard sorted by:
    // 1. room DESC
    // 2. order ASC
    // 3. id ASC (tie breaker)
    private final TreeSet<Player> leaderboard = new TreeSet<>(
        (a, b) -> {
            if (a.room != b.room) 
                return b.room - a.room;
            if (a.order != b.order) 
                return Long.compare(a.order, b.order);
            return a.id.compareTo(b.id);
        }
    );

    public EscapeRoom(int numRooms, int maxParticipants) {
        this.numRooms = numRooms;
        this.roomCounts = new int[numRooms];
    }

    public void registerParticipant(String participantId) {
        if (players.containsKey(participantId)) 
            return;

        Player p = new Player(participantId, 0, globalOrder++);
        players.put(participantId, p);
        leaderboard.add(p);
        roomCounts[0]++;
    }

    public boolean increment(String participantId) {
        Player p = players.get(participantId);
        if (p == null || p.room == numRooms - 1) 
            return false;

        leaderboard.remove(p);
        roomCounts[p.room]--;

        p.room++;
        p.order = globalOrder++; // entering new room → new order

        leaderboard.add(p);
        roomCounts[p.room]++;

        return true;
    }

    public int countParticipantsInRoom(int room) {
        if (room < 0 || room >= numRooms) 
            return 0;
        return roomCounts[room];
    }

    public String[] getTopParticipants(int k) {
        List<String> result = new ArrayList<>();
        for (Player p : leaderboard) {
            if (result.size() == k) 
                break;
            result.add(p.id);
        }
        return result.toArray(new String[0]);
    }

    // Simple test
    public static void main(String[] args) {
        EscapeRoom er = new EscapeRoom(5, 10);

        er.registerParticipant("A");
        er.registerParticipant("B");
        er.registerParticipant("C");

        er.increment("A"); // A -> room 1
        er.increment("A"); // A -> room 2
        er.increment("B"); // B -> room 1

        System.out.println(Arrays.toString(er.getTopParticipants(3)));
        // Expected: [A, B, C]

        System.out.println(er.countParticipantsInRoom(0)); // C still in room 0 → 1
        System.out.println(er.countParticipantsInRoom(1)); // B in room 1 → 1
        System.out.println(er.countParticipantsInRoom(2)); // A in room 2 → 1
    }
}
