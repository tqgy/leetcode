package com.careerup.carrot;

import java.util.*;

public class ExpressionEvaluator {

    public static String compute(String target, List<String> expressions) {
        Map<String, String> map = new HashMap<>();

        // Parse expressions: "T1=1"
        for (String exp : expressions) {
            String[] parts = exp.split("=");
            map.put(parts[0].trim(), parts[1].trim());
        }

        Map<String, Integer> memo = new HashMap<>();
        Set<String> visiting = new HashSet<>();

        try {
            int result = eval(target, map, memo, visiting);
            return String.valueOf(result);
        } catch (CycleException e) {
            return "IMPOSSIBLE";
        }
    }

    private static int eval(String var, Map<String, String> map, Map<String, Integer> memo, Set<String> visiting) {

        if (memo.containsKey(var))
            return memo.get(var);

        if (visiting.contains(var)) {
            throw new CycleException();
        }

        visiting.add(var);

        String expr = map.get(var);
        int value = evaluateExpression(expr, map, memo, visiting);

        visiting.remove(var);
        memo.put(var, value);
        return value;
    }

    // Evaluate expression with at most ONE + or -
    private static int evaluateExpression(String expr, Map<String, String> map, Map<String, Integer> memo,
            Set<String> visiting) {

        expr = expr.trim();

        // Case 1: pure number
        if (expr.matches("-?\\d+")) {
            return Integer.parseInt(expr);
        }

        // Case 2: no operator → single variable
        if (!expr.contains("+") && !expr.contains("-")) {
            return eval(expr, map, memo, visiting);
        }

        // Case 3: one operator (+ or -)
        int opIndex = findOperator(expr);
        char op = expr.charAt(opIndex);

        String left = expr.substring(0, opIndex).trim();
        String right = expr.substring(opIndex + 1).trim();

        int leftVal = left.matches("-?\\d+") ? Integer.parseInt(left) : eval(left, map, memo, visiting);

        int rightVal = right.matches("-?\\d+") ? Integer.parseInt(right) : eval(right, map, memo, visiting);

        return op == '+' ? leftVal + rightVal : leftVal - rightVal;
    }

    // Find the operator index (only one + or - allowed)
    private static int findOperator(String expr) {
        // Skip leading minus for negative numbers
        for (int i = 1; i < expr.length(); i++) {
            char c = expr.charAt(i);
            if (c == '+' || c == '-')
                return i;
        }
        return -1;
    }

    static class CycleException extends RuntimeException {
    }

    // Example usage
    public static void main(String[] args) {
        List<String> expressions = Arrays.asList("T1=1", "T2=2", "T3=T4", "T4=T5", "T5=T2");

        System.out.println(compute("T3", expressions)); // 2

        // Cycle example
        List<String> expressions2 = Arrays.asList("T1=1", "T2=2", "T3=T4+T5", "T4=T5", "T5=T4");

        System.out.println(compute("T3", expressions2)); // IMPOSSIBLE
    }
}

/*
 * If we want to deploy this expression‑evaluation service (ComputeExp) into a
 * real production environment, we need to evolve it from a simple algorithm
 * into a robust, scalable, observable, and secure system. Below is a
 * staff‑level improvement plan that covers performance, reliability,
 * observability, scalability, security, product features, and testing.
 * 
 * 🚀 1. Performance Improvements 1. Memoization → Distributed Cache The current
 * implementation only caches results within a single request. In production, we
 * should:
 * 
 * Store computed variable values in Redis
 * 
 * Add TTL (e.g., 5 minutes)
 * 
 * Share cache across service instances
 * 
 * Benefits: Huge reduction in repeated computation and CPU load.
 * 
 * 2. Pre‑compiling Expressions If expressions don’t change frequently:
 * 
 * Parse expressions at startup
 * 
 * Build an AST (Abstract Syntax Tree)
 * 
 * Pre‑build the dependency graph
 * 
 * Benefits: Evaluation becomes extremely fast (microseconds instead of
 * milliseconds).
 * 
 * 🧱 2. Reliability Improvements 1. Stronger Cycle Handling Instead of just
 * returning "IMPOSSIBLE":
 * 
 * Log structured error details
 * 
 * Emit metrics (e.g., Prometheus counters)
 * 
 * Return standardized error codes (400 / 422)
 * 
 * 2. Timeout Protection Prevent deep dependency chains from blocking CPU:
 * 
 * Add request timeout (e.g., 50ms)
 * 
 * Add DFS depth limit (e.g., 1000)
 * 
 * 3. Rate Limiting Prevent malicious or accidental overload:
 * 
 * IP‑based rate limiting
 * 
 * User‑based rate limiting
 * 
 * Expression length limits
 * 
 * 📡 3. Observability Add full observability:
 * 
 * 1. Tracing (OpenTelemetry) Each variable evaluation becomes a span
 * 
 * Helps visualize dependency chains
 * 
 * 2. Metrics expression_eval_latency
 * 
 * expression_cycle_detected_count
 * 
 * expression_cache_hit_ratio
 * 
 * 3. Structured Logging Log dependency chains
 * 
 * Log evaluation failures
 * 
 * Log slow evaluations
 * 
 * This makes debugging and monitoring much easier.
 * 
 * 📈 4. Scalability 1. Horizontal Scaling Deploy multiple service instances
 * behind a load balancer.
 * 
 * 2. External Expression Storage Do not hardcode expressions in memory. Store
 * them in:
 * 
 * DynamoDB / Spanner / Postgres
 * 
 * Or a configuration service
 * 
 * Support hot reload without restarting the service.
 * 
 * 🔐 5. Security 1. Prevent Expression Injection Users might try to inject
 * malicious content like:
 * 
 * Code T1 = System.exit(0) We must enforce:
 * 
 * Variable name whitelist (e.g., only Txxx)
 * 
 * Character whitelist (digits, letters, +, -)
 * 
 * No arbitrary code execution
 * 
 * 🧠 6. Product‑Level Enhancements 1. Version Control for Expressions Support:
 * 
 * Version history
 * 
 * Rollback
 * 
 * Approval workflow
 * 
 * 2. Debugging UI A tool for PMs/ops:
 * 
 * Input a variable → show dependency chain
 * 
 * Show evaluation steps
 * 
 * Show final result
 * 
 * Example:
 * 
 * Code T3 = T4 T4 = T5 T5 = T2 T2 = 2 🧪 7. Testing Strategy You need a full
 * testing suite:
 * 
 * Unit tests (parser, evaluator, cycle detection)
 * 
 * Integration tests (dependency chains)
 * 
 * Load tests (100k variables)
 * 
 * Chaos tests (random cycles, missing links)
 * 
 * 🎯 Summary: What Needs to Improve for Production Area Improvements
 * Performance Redis caching, pre‑compiled expressions Reliability Timeouts,
 * rate limiting, error codes Observability Metrics, tracing, structured logs
 * Scalability Distributed deployment, external storage Security Input
 * validation, whitelisting Product Features Versioning, debugging UI Testing
 * Unit, integration, load, chaos
 * 
 */
