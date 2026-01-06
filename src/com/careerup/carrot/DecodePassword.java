package com.careerup.carrot;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 * Decodes passwords from a list of string blocks.
 * Input Format: 
 * Flattened list of strings where blocks are separated by empty lines. 
 * Block Format: 
 *  - Line 1: Index (integer) 
 *  - Line 2: Coordinates "[x, y]"
 * where x is column (0-based from left) and y is row (0-based from bottom) 
 *  - Lines 3+: Grid of characters
 */
public class DecodePassword {

    /**
     * Represents a single parsed block containing its metadata and grid.
     */
    static class Block {
        int index;
        int x, y;
        List<String> grid;

        public Block(List<String> rawLines) {
            try {
                this.index = Integer.parseInt(rawLines.get(0).trim());
                int[] coords = parseCoord(rawLines.get(1));
                this.x = coords[0];
                this.y = coords[1];
                this.grid = rawLines.subList(2, rawLines.size());
            } catch (Exception e) {
                // In a production app, we would handle malformed blocks more gracefully
                // For this exercise, we'll mark them as invalid.
                this.index = -1;
                this.x = -1;
                this.y = -1;
                this.grid = new ArrayList<>();
            }
        }

        private int[] parseCoord(String s) {
            s = s.replaceAll("[\\[\\]]", "");
            String[] parts = s.split(",");
            return new int[] { Integer.parseInt(parts[0].trim()), Integer.parseInt(parts[1].trim()) };
        }

        /**
         * Decodes the character at the (x, y) coordinate. The y-coordinate is given
         * from the BOTTOM (standard Cartesian), but the grid list is stored from
         * Top-to-Bottom.
         */
        public char decode() {
            if (grid.isEmpty())
                return '\0';

            int rows = grid.size();
            // Convert bottom-left origin (Cartesian) to top-left origin (Matrix/List index)
            // Example: If rows=4, y=0 (bottom) -> actualRow=3 (last index)
            int actualRow = rows - 1 - y;

            if (actualRow < 0 || actualRow >= rows)
                return '\0';

            String rowStr = grid.get(actualRow);
            if (x < 0 || x >= rowStr.length())
                return '\0';

            return rowStr.charAt(x);
        }
    }

    /**
     * Part 2: Decodes a password by combining characters from all blocks. Blocks
     * are sorted by their index.
     *
     * @param rawBlocks List of raw string lists, where each list represents a
     *                  block.
     * @return The decoded password string.
     */
    public static String decodePassword(List<List<String>> rawBlocks) {
        StringBuilder password = new StringBuilder();
        // Use TreeMap to automatically sort encoded characters by their block index
        Map<Integer, Character> charMap = new TreeMap<>();

        for (List<String> rawBlock : rawBlocks) {
            Block block = new Block(rawBlock);
            if (block.index != -1) { // Skip malformed blocks
                charMap.put(block.index, block.decode());
            }
        }

        for (char c : charMap.values()) {
            if (c != '\0') { // Filter out characters that couldn't be decoded (e.g., out of bounds)
                password.append(c);
            }
        }
        return password.toString();
    }

    /**
     * Part 3: Similar to Part 2, but returns only the FIRST password sequence
     * found. A new sequence is implied if an index repeats.
     */
    public static String decodeFirstPassword(List<List<String>> rawBlocks) {
        StringBuilder password = new StringBuilder();
        Map<Integer, Character> charMap = new TreeMap<>();
        Set<Integer> seenIndices = new HashSet<>();

        for (List<String> rawBlock : rawBlocks) {
            Block block = new Block(rawBlock);
            // If we encounter an index we've already seen, the first password sequence is
            // complete
            if (seenIndices.contains(block.index)) {
                break;
            }
            seenIndices.add(block.index);
            if (block.index != -1) {
                charMap.put(block.index, block.decode());
            }
        }

        for (char c : charMap.values()) {
            if (c != '\0') { // Filter out characters that couldn't be decoded
                password.append(c);
            }
        }
        return password.toString();
    }

    /**
     * Helper to group lines into blocks separated by empty lines.
     */
    public static List<List<String>> parseBlocks(List<String> lines) {
        List<List<String>> blocks = new ArrayList<>();
        List<String> currentBlock = new ArrayList<>();

        for (String line : lines) {
            if (line.trim().isEmpty()) {
                if (!currentBlock.isEmpty()) {
                    blocks.add(new ArrayList<>(currentBlock));
                    currentBlock.clear();
                }
            } else {
                currentBlock.add(line);
            }
        }
        // Important: Add the last block if it's not empty
        if (!currentBlock.isEmpty()) {
            blocks.add(currentBlock);
        }
        return blocks;
    }

    // ==========================================
    // TEST CASES
    // ==========================================

    public static void main(String[] args) throws IOException {
        System.out.println("Running DecodePassword Tests...\n");

        testOriginalCase();
        testOutOfBounds();
        testMixedOrder();
        testMalformedBlocks();
        testEmptyInput();
    }

    private static void testOriginalCase() throws IOException {
        System.out.println("[Test] Original Case");
        List<String> input = Arrays.asList("1", "[2,4]", "S3KDA4", "4ASDSD", "ACEEDS", "ASDEED", "RTRYYU", "", "0",
                "[0,0]", "I3KDA4", "XTRYYU", "", "1", "[0,0]", "L3BDA4" // This repeat index is for Part 3 check
        );
        List<List<String>> blocks = parseBlocks(input);

        String part2 = decodePassword(blocks.subList(0, 2)); // Only first two blocks for Part 2
        assertResult("Part 2 (0,1)", "XK", part2);

        String part3 = decodeFirstPassword(blocks); // All blocks for Part 3, stops at repeated index 1
        assertResult("Part 3 (First PW)", "XK", part3);

        List<List<String>> fileBlock = parseBlocks(Files.readAllLines(Path.of("/Users/tqgynn/Desktop/test.txt")));
        String filePart2 = decodePassword(fileBlock.subList(0, 2));
        assertResult("Part 2 (File)", "XK", filePart2);

        String filePart3 = decodeFirstPassword(fileBlock);
        assertResult("Part 3 (File)", "XK", filePart3);
    }

    private static void testOutOfBounds() {
        System.out.println("\n[Test] Out of Bounds & Invalid Coordinates");
        List<String> input = Arrays.asList("0", "[100,0]", "ABC", "", // X out of bounds
                "1", "[0,100]", "ABC", "", // Y out of bounds
                "2", "[0,0]", "Z" // Valid
        );
        List<List<String>> blocks = parseBlocks(input);

        // Expect only 'Z' because others return '\0' which we filter/skip
        String result = decodePassword(blocks);
        assertResult("Out of bounds filtering", "Z", result);
    }

    private static void testMixedOrder() {
        System.out.println("\n[Test] Mixed Order Decoding");
        List<String> input = Arrays.asList("2", "[0,0]", "C", "", "0", "[0,0]", "A", "", "1", "[0,0]", "B");
        List<List<String>> blocks = parseBlocks(input);

        // Should sort by index: 0->A, 1->B, 2->C
        String result = decodePassword(blocks);
        assertResult("Mixed order sort", "ABC", result);
    }

    private static void testMalformedBlocks() {
        System.out.println("\n[Test] Malformed Blocks");
        List<String> input = Arrays.asList("0", "[0,0]", "A", "", "invalid_index", "[0,0]", "B", "", // Malformed index
                "2", "invalid_coords", "C", "", // Malformed coordinates
                "3", "[0,0]" // Incomplete block (missing grid line)
        );
        List<List<String>> blocks = parseBlocks(input);

        // Only block 0 should be successfully decoded. Others will be skipped due to
        // index -1.
        String result = decodePassword(blocks);
        assertResult("Malformed blocks handling", "A", result);
    }

    private static void testEmptyInput() {
        System.out.println("\n[Test] Empty Input");
        List<String> emptyInput = Collections.emptyList();
        List<List<String>> blocks = parseBlocks(emptyInput);
        String result = decodePassword(blocks);
        assertResult("Empty input", "", result);

        List<String> emptyBlocks = Arrays.asList("", "", "");
        blocks = parseBlocks(emptyBlocks);
        result = decodePassword(blocks);
        assertResult("Empty blocks list", "", result);
    }

    private static void assertResult(String name, String expected, String actual) {
        if (expected.equals(actual)) {
            System.out.println("  ✅ " + name + ": PASSED (Got '" + actual + "')");
        } else {
            System.out.println("  ❌ " + name + ": FAILED");
            System.out.println("     Expected: '" + expected + "'");
            System.out.println("     Got:      '" + actual + "'");
        }
    }
}

/**
 * If we wanted to turn the DecodePassword problem into a real production‑ready
 * service, we’d need to evolve it far beyond “parse a grid and return a
 * character.” A production system must be fast, reliable, observable, scalable,
 * secure, and easy to operate.
 * 
 * Below is a staff‑level, end‑to‑end plan for how to build this as a real
 * Instacart‑grade service.
 * 
 * 🧱 1. Clarify the Real‑World Use Case
 * 
 * Before designing the system, we define what this service actually does in
 * production:
 * 
 * It receives blocks of encoded password data (from files, streams, or APIs).
 * 
 * Each block contains:
 * 
 * An index
 * 
 * A coordinate
 * 
 * A grid
 * 
 * It must decode characters and assemble passwords.
 * 
 * It must detect:
 * 
 * Invalid coordinates
 * 
 * Missing rows
 * 
 * Cycles (for multi‑password logic)
 * 
 * Duplicate indices (password boundary)
 * 
 * It must return:
 * 
 * A single character
 * 
 * A full password
 * 
 * The first password among multiple sequences
 * 
 * This becomes a DecodePassword Service.
 * 
 * 🚀 2. API Design
 * 
 * REST API Code
 * 
 * POST /decode/character POST /decode/password POST /decode/passwords/first
 * 
 * Example request json { "blocks": [ { "index": 1, "coord": [2,4], "grid":
 * ["S3KDA4","4ASDSD","ACEEDS","ASDEED","RTRYYU"] }, { "index": 0, "coord":
 * [0,0], "grid": ["I3KDA4","XTRYYU"] } ] } Example response json { "password":
 * "XK" }
 * 
 * ⚙️ 3. Core Service Architecture
 * 
 * Code Client → API Gateway → DecodePassword Service → Cache / Storage →
 * Observability Components DecodePassword Service
 * 
 * Parses blocks
 * 
 * Validates input
 * 
 * Computes characters
 * 
 * Detects cycles / duplicates
 * 
 * Assembles passwords
 * 
 * Caching Layer (Redis)
 * 
 * Cache decoded characters for repeated grids
 * 
 * Cache parsed blocks (hash of block → character)
 * 
 * Storage Layer
 * 
 * Optional: store historical blocks or decoded passwords
 * 
 * Useful for auditing or debugging
 * 
 * Observability
 * 
 * Metrics
 * 
 * Logging
 * 
 * Tracing
 * 
 * ⚡ 4. Performance Improvements
 * 
 * 1. Cache decoded characters Many grids repeat across requests.
 * 
 * Cache key:
 * 
 * Code hash(index + coord + grid) Value:
 * 
 * Code decoded character 2. Pre‑parse grids Instead of re‑parsing strings every
 * time:
 * 
 * Convert grid into a 2D char array
 * 
 * Store in memory or Redis
 * 
 * 3. Batch decoding If a request contains many blocks:
 * 
 * Decode in parallel using thread pool
 * 
 * Combine results at the end
 * 
 * 🧩 5. Reliability Improvements
 * 
 * 1. Input validation Reject malformed blocks:
 * 
 * Missing index
 * 
 * Invalid coordinate format
 * 
 * Empty grid
 * 
 * Non‑uppercase characters
 * 
 * Out‑of‑bounds coordinates
 * 
 * 2. Error handling Return structured errors:
 * 
 * json { "error": { "code": "INVALID_COORDINATE", "message": "Coordinate [3,10]
 * is outside the grid" } } 3. Cycle detection For follow‑up #3:
 * 
 * Maintain a set of seen indices
 * 
 * If index repeats → password boundary
 * 
 * If cycle in dependency → return "IMPOSSIBLE"
 * 
 * 📡 6. Observability Metrics
 * 
 * decode_request_count
 * 
 * decode_latency_ms
 * 
 * decode_cache_hit_ratio
 * 
 * decode_invalid_block_count
 * 
 * decode_cycle_detected_count
 * 
 * Tracing Each block decode = one span
 * 
 * Each password assembly = parent span
 * 
 * Logging Structured logs with:
 * 
 * requestId
 * 
 * block count
 * 
 * indices
 * 
 * errors
 * 
 * 📈 7. Scalability
 * 
 * 1. Stateless service All state (cache, storage) externalized → service can
 * scale horizontally.
 * 
 * 2. Load balancing Use round‑robin or least‑connections.
 * 
 * 3. Sharding If storing blocks:
 * 
 * Shard by hash(block index)
 * 
 * Or by password ID
 * 
 * 4. Async processing If decoding large files:
 * 
 * Upload file → async job
 * 
 * Client polls for result
 * 
 * 🔐 8. Security
 * 
 * 1. Input sanitization Reject:
 * 
 * Non‑ASCII characters
 * 
 * Extremely large grids
 * 
 * Negative coordinates
 * 
 * 2. Rate limiting Prevent abuse:
 * 
 * Per‑IP limit
 * 
 * Per‑user limit
 * 
 * 3. Authentication If used internally:
 * 
 * Service‑to‑service auth (mTLS or OAuth)
 * 
 * 🧪 9. Testing Strategy
 * 
 * Unit tests Coordinate conversion
 * 
 * Grid parsing
 * 
 * Duplicate index detection
 * 
 * Cycle detection
 * 
 * Integration tests Multi‑block decoding
 * 
 * Multi‑password decoding
 * 
 * Load tests 10k blocks per request
 * 
 * 1000 requests per second
 * 
 * Chaos tests Random malformed blocks
 * 
 * Random missing rows
 * 
 * Random cycles
 * 
 * 🎯 10. Summary: What Makes It Production‑Ready? Area Improvements Performance
 * Caching, pre‑parsing, batching Reliability Validation, error handling, cycle
 * detection Observability Metrics, logs, tracing Scalability Stateless service,
 * horizontal scaling Security Input sanitization, rate limiting Product
 * features Clear APIs, structured responses Testing Unit, integration, load,
 * chaos
 */
