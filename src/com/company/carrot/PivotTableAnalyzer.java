package com.careerup.carrot;

import java.util.*;

/**
 * You are given a 2D array of strings representing a retail sales table.
 * 
 * prodId | sales | cost | state | timestamp 
 * A1 | 10 | 5 | CA | 2024-01-01 
 * A2 | 20 | 8 | NY | 2024-01-01 
 * A1 | 15 | 7 | CA | 2024-01-02
 * 
 * Input: salesTable → String[][] Row 0 = header Rows 1..n = data All values are
 * strings
 * 
 * Columns always include: prodId sales cost state (or country) timestamp
 * (YYYY‑MM‑DD)
 * 
 * pivotColumn → String Examples: "sales" "cost" "state" "timestamp"
 * 
 * Tasks 1. 
 * Compute total sum of a numeric column 
 * Example: sum("sales") → 10 + 20 + 15 = 45
 * 
 * 2. Compute sum of a numeric column grouped by a pivot column Return a map.
 * Example: sum("sales") grouped by "state" →
 * CA → 10 + 15 = 25
 * NY → 20
 * 
 * Edge Cases
 * Column may not exist → throw exception
 * Numeric columns stored as strings → must parse
 * Extra columns may exist
 * pivotColumn may be:
 * categorical (state)
 * date (timestamp)
 * product
 * Empty table → return empty map
 */
public class PivotTableAnalyzer {

    // Get column index by name
    private static int getColIndex(String[] header, String colName) {
        for (int i = 0; i < header.length; i++) {
            if (header[i].equals(colName)) return i;
        }
        throw new IllegalArgumentException("Column not found: " + colName);
    }

    // 1. Total sum of a numeric column
    public static double totalSum(String[][] table, String colName) {
        String[] header = table[0];
        int col = getColIndex(header, colName);

        double sum = 0;
        for (int i = 1; i < table.length; i++) {
            sum += Double.parseDouble(table[i][col]);
        }
        return sum;
    }

    // 2. Sum of a numeric column grouped by pivot column
    public static Map<String, Double> sumByPivot(String[][] table, String valueCol, String pivotCol) {
        String[] header = table[0];
        int valIdx = getColIndex(header, valueCol);
        int pivotIdx = getColIndex(header, pivotCol);

        Map<String, Double> result = new HashMap<>();

        for (int i = 1; i < table.length; i++) {
            String pivotValue = table[i][pivotIdx];
            double val = Double.parseDouble(table[i][valIdx]);

            result.put(pivotValue, result.getOrDefault(pivotValue, 0.0) + val);
        }

        return result;
    }

    // 3. Profit grouped by pivot column
    public static Map<String, Double> profitByPivot(String[][] table, String pivotCol) {
        String[] header = table[0];
        int salesIdx = getColIndex(header, "sales");
        int costIdx = getColIndex(header, "cost");
        int pivotIdx = getColIndex(header, pivotCol);

        Map<String, Double> profitMap = new HashMap<>();

        for (int i = 1; i < table.length; i++) {
            String pivotValue = table[i][pivotIdx];
            double sales = Double.parseDouble(table[i][salesIdx]);
            double cost = Double.parseDouble(table[i][costIdx]);

            profitMap.put(pivotValue,
                profitMap.getOrDefault(pivotValue, 0.0) + (sales - cost));
        }

        return profitMap;
    }

    // 4. Aggregate by timestamp and return the timestamp with the max sum 
    public static String maxByTimestamp(String[][] table, String valueCol) { 
        String[] header = table[0]; 
        int timestampIdx = getColIndex(header, "timestamp"); 
        int valueIdx = -1; 
        boolean isProfit = valueCol.equals("profit"); 
        int salesIdx = -1, costIdx = -1;
        if (isProfit) { 
            salesIdx = getColIndex(header, "sales"); 
            costIdx = getColIndex(header, "cost"); 
        } else { 
            valueIdx = getColIndex(header, valueCol); 
        } 
        Map<String, Double> agg = new HashMap<>(); 
        for (int i = 1; i < table.length; i++) { 
            String ts = table[i][timestampIdx]; 
            double val; 
            if (isProfit) { 
                double sales = Double.parseDouble(table[i][salesIdx]); 
                double cost = Double.parseDouble(table[i][costIdx]); 
                val = sales - cost; 
            } else { 
                val = Double.parseDouble(table[i][valueIdx]); 
            } 
            agg.put(ts, agg.getOrDefault(ts, 0.0) + val); 
        } 
        // Find timestamp with max aggregated value 
        String bestTs = null; 
        double bestVal = Double.NEGATIVE_INFINITY; 
        for (Map.Entry<String, Double> e : agg.entrySet()) { 
            if (e.getValue() > bestVal) { 
                bestVal = e.getValue(); 
                bestTs = e.getKey(); 
            } 
        } 
        // streaming manner
        return agg.entrySet().stream().max(Map.Entry.comparingByValue()).get().getKey();
        // return bestTs;
    }

    // Example usage
    public static void main(String[] args) {
        String[][] table = {
            {"prodId", "sales", "cost", "state", "timestamp"},
            {"A1", "10", "5", "CA", "2024-01-01"}, 
            {"A2", "20", "8", "NY", "2024-01-01"}, 
            {"A1", "15", "7", "CA", "2024-01-02"}, 
            {"A3", "30", "10", "TX", "2024-01-02"}
        };

        System.out.println("Total sales = " + totalSum(table, "sales"));
        System.out.println("Sales by state = " + sumByPivot(table, "sales", "state"));
        System.out.println("Profit by state = " + profitByPivot(table, "state"));
        System.out.println("Max sales timestamp = " + maxByTimestamp(table, "sales")); 
        System.out.println("Max profit timestamp = " + maxByTimestamp(table, "profit"));
    }
}

/**
 * If you wanted to take your Pivot Table Profit Analyzer from “interview code”
 * to a real production‑grade service, you’d need to evolve it from a simple
 * in‑memory calculator into a scalable, observable, reliable analytics
 * microservice. Here’s how a staff‑level engineer would frame the
 * transformation.
 * 
 * 🌐 1. Clarify the Real Production Use Case Before building anything, define
 * what the service must support:
 * 
 * Large datasets (millions of rows, not a tiny 2D array)
 * 
 * Multiple aggregations (sum, profit, max, min, avg)
 * 
 * Multiple pivot dimensions (state, date, product, etc.)
 * 
 * Filtering (WHERE clauses)
 * 
 * High concurrency (many clients calling at once)
 * 
 * Low latency (sub‑100ms responses)
 * 
 * Strong correctness guarantees
 * 
 * This shifts the service from “toy pivot table” to “real analytics engine.”
 * 
 * ⚙️ 2. Architecture Upgrade Current (interview) Everything computed in memory
 * 
 * Single request → single calculation
 * 
 * No persistence
 * 
 * No caching
 * 
 * No parallelism
 * 
 * Production A real service would look more like this:
 * 
 * Code Client → API Gateway → Pivot Analytics Service → Cache → Data Warehouse
 * ↘︎ Observability Stack Components: Pivot Analytics Service Stateless
 * microservice
 * 
 * Performs aggregations, grouping, filtering
 * 
 * Supports multiple aggregation types
 * 
 * Caching Layer (Redis) Cache results of common queries
 * 
 * Cache parsed column indices
 * 
 * Cache pre‑aggregated data (e.g., daily sales)
 * 
 * Data Warehouse BigQuery / Snowflake / Redshift / Spark
 * 
 * The service should push down heavy queries to the warehouse
 * 
 * Observability Metrics (latency, cache hit ratio, error rate)
 * 
 * Tracing (OpenTelemetry)
 * 
 * Structured logs
 * 
 * 🚀 3. Performance Improvements 1. Column Index Pre‑computation Instead of
 * scanning headers every request, pre‑compute:
 * 
 * Code Map<String, Integer> columnIndexCache 2. Vectorized Computation Process
 * columns in batches, not row‑by‑row.
 * 
 * 3. Parallel Aggregation Use parallel streams or thread pools for large
 * datasets.
 * 
 * 4. Pre‑aggregated Materialized Views For example:
 * 
 * Daily sales per state
 * 
 * Daily profit per product
 * 
 * This reduces query time from seconds → milliseconds.
 * 
 * 5. Query Caching Cache key:
 * 
 * Code hash(query parameters + date range) Value:
 * 
 * Code aggregated result 
 * 
 * 🧱 4. Reliability Improvements 
 * 
 * 1. Input Validation
 * Reject invalid:
 * 
 * Column names
 * 
 * Timestamps
 * 
 * Non‑numeric values in numeric columns
 * 
 * Empty tables
 * 
 * 2. Error Handling Return structured errors:
 * 
 * json { "error": "INVALID_COLUMN", "message": "Column 'profitz' does not
 * exist" } 3. Timeouts Prevent long‑running queries:
 * 
 * 100ms timeout for in‑memory
 * 
 * 1–3s timeout for warehouse queries
 * 
 * 4. Rate Limiting Protect the service from abuse.
 * 
 * 📡 5. Observability Metrics 
 * 
 * pivot_request_count
 * 
 * pivot_latency_ms
 * 
 * pivot_cache_hit_ratio
 * 
 * pivot_invalid_query_count
 * 
 * pivot_warehouse_query_time
 * 
 * Tracing Each aggregation step is a span
 * 
 * Warehouse calls are traced separately
 * 
 * Logging Structured logs with:
 * 
 * requestId
 * 
 * pivot column
 * 
 * aggregation type
 * 
 * row count
 * 
 * errors
 * 
 * 📈 6. Scalability 
 * 
 * 1. Stateless Microservice All state externalized → easy
 * horizontal scaling.
 * 
 * 2. Load Balancing Round‑robin or least‑connections.
 * 
 * 3. Sharding If storing pre‑aggregated data:
 * 
 * Shard by date
 * 
 * Shard by product category
 * 
 * 4. Async Batch Jobs For large datasets:
 * 
 * Client submits job
 * 
 * Service processes asynchronously
 * 
 * Client polls for result
 * 
 * 🔐 7. Security 
 * 
 * 1. Input Sanitization Prevent injection attacks:
 * 
 * Only allow alphanumeric column names
 * 
 * Validate timestamps
 * 
 * Reject overly large payloads
 * 
 * 2. Authentication OAuth2 / mTLS for internal services
 * 
 * 3. Authorization Some users may only access certain columns or states
 * 
 * 🧪 8. Testing Strategy 
 * 
 * Unit Tests Column lookup
 * 
 * Aggregation logic
 * 
 * Profit calculation
 * 
 * Timestamp parsing
 * 
 * Integration Tests Multiple pivot columns
 * 
 * Large datasets
 * 
 * Missing columns
 * 
 * Load Tests 10M rows
 * 
 * 1000 requests/sec
 * 
 * Chaos Tests Random missing columns
 * 
 * Random malformed rows
 * 
 * Random nulls
 * 
 * 🎯 9. Summary: What Makes It Production‑Ready? Area Improvements Performance
 * Caching, parallelism, pre‑aggregation Reliability Validation, error handling,
 * timeouts Observability Metrics, logs, tracing Scalability Stateless service,
 * load balancing Security Sanitization, auth, rate limiting Product Features
 * Flexible API, multiple aggregations Testing Unit, integration, load, chaos
 */