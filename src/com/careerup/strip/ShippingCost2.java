package com.careerup.strip;

import java.util.*;

/**
 * -----------------------------------------------------------------------------
 *  Shipping Cost Calculator (Stripe / Pinterest-style)
 * -----------------------------------------------------------------------------
 * Order:
 *   - country: "US", "CA", ...
 *   - items: [{ product, quantity }]
 *
 * Shipping cost config per country, per product, with tiers:
 *
 * PART 1:
 *   Single per-unit cost:
 *     mouse: cost = 550
 *   → total = quantity * cost
 *
 * PART 2 (incremental tiers):
 *   Each product has quantity tiers:
 *     laptop:
 *       [0, 2]  -> 1000
 *       [3, ∞)  ->  900
 *
 *   Pricing is PIECEWISE over quantity:
 *     quantity = 5 → 2 * 1000 + 3 * 900
 *
 * PART 3 (mixed types):
 *   Each tier has:
 *     type:
 *       "incremental" → pay per unit in this tier
 *       "fixed"       → one flat fee if you use ANY units in this tier
 *
 *   Example (laptop, qty=5):
 *     [0, 2], type=fixed, cost=1000   → pay 1000 (once)
 *     [3, ∞), type=incremental, 900   → 3 * 900
 *
 * Goal:
 *   Implement calculateShippingCost(country, items, shippingCostConfig)
 *   that supports all three parts by how we build the config.
 * -----------------------------------------------------------------------------
 */
/**
 * Stripe Interview Problem: Calculate Shipping Cost
 * 
 * Problem Description:
 * Given an order with items and quantities, and a shipping cost structure for different countries,
 * calculate the total shipping cost. The problem has three parts with increasing complexity:
 * 
 * Part 1: Simple fixed cost per product
 * - Each product has one cost, multiply by quantity
 * 
 * Part 2: Tiered pricing based on quantity ranges (incremental pricing)
 * - Different quantity ranges have different per-unit costs
 * - Example: laptops cost 1000 for quantities 0-2, then 900 for quantities 3+
 * 
 * Part 3: Mixed pricing types
 * - "fixed": flat rate for entire quantity range (pay once regardless of quantity in range)
 * - "incremental": price per unit (like Part 2)
 * 
 * Test Cases:
 * 
 * Part 1:
 * Order US: 20 mice + 5 laptops = 20*550 + 5*1000 = 16000
 * Order CA: 20 mice + 5 laptops = 20*750 + 5*1100 = 20500
 * 
 * Part 2:
 * Order US: 20 mice (all at 550) + 5 laptops (2 at 1000, 3 at 900)
 * = 20*550 + 2*1000 + 3*900 = 11000 + 2000 + 2700 = 15700
 * 
 * Order CA: 20 mice (all at 750) + 5 laptops (2 at 1100, 3 at 1000)
 * = 20*750 + 2*1100 + 3*1000 = 15000 + 2200 + 3000 = 20200
 * 
 * Part 3:
 * Order US: 20 mice (incremental 550) + laptops (fixed 1000 for 0-2, then 3*900 incremental)
 * = 20*550 + 1000 + 3*900 = 11000 + 1000 + 2700 = 14700
 * 
 * Order CA: 20 mice (incremental 750) + laptops (fixed 1100 for 0-2, then 3*1000 incremental)
 * = 20*750 + 1100 + 3*1000 = 15000 + 1100 + 3000 = 19100
 */
public class ShippingCost2 {
    
    /**
     * Unified method to calculate shipping cost for all three parts
     * Automatically detects the pricing structure and applies the correct calculation
     */
    public static int calculateShippingCost(Map<String, Object> order, 
                                           Map<String, Map<String, Map<String, Object>>> shippingCost) {
        // Extract country and items from order
        String country = (String) order.get("country");
        List<Map<String, Object>> items = (List<Map<String, Object>>) order.get("items");
        
        // Get shipping costs for this country
        Map<String, Map<String, Object>> countryCosts = shippingCost.get(country);
        
        int totalCost = 0;
        
        // Calculate cost for each item in the order
        for (Map<String, Object> item : items) {
            String product = (String) item.get("product");
            int quantity = (int) item.get("quantity");
            
            // Find matching product in shipping costs and calculate
            int itemCost = calculateItemCost(product, quantity, countryCosts);
            totalCost += itemCost;
        }
        
        return totalCost;
    }
    
    /**
     * Helper method: Calculate cost for a single item
     * Handles all three parts based on the structure of cost data
     */
    private static int calculateItemCost(String product, int quantity, 
                                        Map<String, Map<String, Object>> countryCosts) {
        // Find the product in shipping costs
        if (countryCosts == null || !countryCosts.containsKey(product)) {
            return 0; // Product not found
        }
        
        Map<String, Object> costInfo = countryCosts.get(product);
        
        // Part 1: Simple fixed cost (has "cost" field directly)
        if (costInfo.containsKey("cost")) {
            int cost = (int) costInfo.get("cost");
            return quantity * cost;
        }
        
        // Part 2 & 3: Tiered pricing (has "costs" list)
        if (costInfo.containsKey("costs")) {
            List<Map<String, Object>> costs = (List<Map<String, Object>>) costInfo.get("costs");
            return calculateTieredCost(quantity, costs);
        }
        
        return 0;
    }
    
    /**
     * Helper method: Calculate cost with tiered pricing
     * Handles both Part 2 (incremental only) and Part 3 (fixed + incremental)
     */
    private static int calculateTieredCost(int quantity, List<Map<String, Object>> costs) {
        int totalCost = 0;
        
        // Process each tier
        for (Map<String, Object> tier : costs) {
            int minQty = (int) tier.get("minQuantity");
            Integer maxQty = (Integer) tier.get("maxQuantity");
            int cost = (int) tier.get("cost");
            
            // Get pricing type (Part 3), default to "incremental" (Part 2)
            String type = tier.containsKey("type") ? (String) tier.get("type") : "incremental";
            
            // Check if this tier applies to our quantity
            if (quantity > minQty) {
                // Calculate upper bound for this tier
                int upperBound = (maxQty == null) ? quantity : Math.min(quantity, maxQty);
                
                if ("fixed".equals(type)) {
                    // Part 3: Fixed pricing - pay flat rate once for this tier
                    totalCost += cost;
                } else {
                    // Part 2 & 3: Incremental pricing - pay per unit
                    int unitsInTier = upperBound - minQty;
                    totalCost += unitsInTier * cost;
                }
            }
        }
        
        return totalCost;
    }
    
    // Test cases for all three parts
    public static void main(String[] args) {
        testPart1();
        testPart2();
        testPart3();
    }
    
    /**
     * Test Part 1: Simple fixed pricing
     */
    private static void testPart1() {
        System.out.println("=== Part 1: Simple Fixed Pricing ===");
        
        // Create order
        Map<String, Object> orderUS = createOrder("US", 20, 5);
        Map<String, Object> orderCA = createOrder("CA", 20, 5);
        
        // Create shipping cost structure for Part 1
        Map<String, Map<String, Map<String, Object>>> shippingCost = new HashMap<>();
        
        Map<String, Map<String, Object>> usCosts = new HashMap<>();
        usCosts.put("mouse", createSimpleCost("mouse", 550));
        usCosts.put("laptop", createSimpleCost("laptop", 1000));
        shippingCost.put("US", usCosts);
        
        Map<String, Map<String, Object>> caCosts = new HashMap<>();
        caCosts.put("mouse", createSimpleCost("mouse", 750));
        caCosts.put("laptop", createSimpleCost("laptop", 1100));
        shippingCost.put("CA", caCosts);
        
        // Calculate and verify
        int resultUS = calculateShippingCost(orderUS, shippingCost);
        int resultCA = calculateShippingCost(orderCA, shippingCost);
        
        System.out.println("US Order: " + resultUS + " (Expected: 16000)");
        System.out.println("CA Order: " + resultCA + " (Expected: 20500)");
        System.out.println();
    }
    
    /**
     * Test Part 2: Tiered incremental pricing
     */
    private static void testPart2() {
        System.out.println("=== Part 2: Tiered Incremental Pricing ===");
        
        // Create order
        Map<String, Object> orderUS = createOrder("US", 20, 5);
        Map<String, Object> orderCA = createOrder("CA", 20, 5);
        
        // Create shipping cost structure for Part 2
        Map<String, Map<String, Map<String, Object>>> shippingCost = new HashMap<>();
        
        Map<String, Map<String, Object>> usCosts = new HashMap<>();
        usCosts.put("mouse", createTieredProduct("mouse", 
            Arrays.asList(createTier(0, null, 550, null))));
        usCosts.put("laptop", createTieredProduct("laptop", 
            Arrays.asList(createTier(0, 2, 1000, null), createTier(2, null, 900, null))));
        shippingCost.put("US", usCosts);
        
        Map<String, Map<String, Object>> caCosts = new HashMap<>();
        caCosts.put("mouse", createTieredProduct("mouse", 
            Arrays.asList(createTier(0, null, 750, null))));
        caCosts.put("laptop", createTieredProduct("laptop", 
            Arrays.asList(createTier(0, 2, 1100, null), createTier(2, null, 1000, null))));
        shippingCost.put("CA", caCosts);
        
        // Calculate and verify
        int resultUS = calculateShippingCost(orderUS, shippingCost);
        int resultCA = calculateShippingCost(orderCA, shippingCost);
        
        System.out.println("US Order: " + resultUS + " (Expected: 15700)");
        System.out.println("CA Order: " + resultCA + " (Expected: 20200)");
        System.out.println();
    }
    
    /**
     * Test Part 3: Mixed pricing types (fixed + incremental)
     */
    private static void testPart3() {
        System.out.println("=== Part 3: Mixed Pricing Types ===");
        
        // Create order
        Map<String, Object> orderUS = createOrder("US", 20, 5);
        Map<String, Object> orderCA = createOrder("CA", 20, 5);
        
        // Create shipping cost structure for Part 3
        Map<String, Map<String, Map<String, Object>>> shippingCost = new HashMap<>();
        
        Map<String, Map<String, Object>> usCosts = new HashMap<>();
        usCosts.put("mouse", createTieredProduct("mouse", 
            Arrays.asList(createTier(0, null, 550, "incremental"))));
        usCosts.put("laptop", createTieredProduct("laptop", 
            Arrays.asList(createTier(0, 2, 1000, "fixed"), createTier(2, null, 900, "incremental"))));
        shippingCost.put("US", usCosts);
        
        Map<String, Map<String, Object>> caCosts = new HashMap<>();
        caCosts.put("mouse", createTieredProduct("mouse", 
            Arrays.asList(createTier(0, null, 750, "incremental"))));
        caCosts.put("laptop", createTieredProduct("laptop", 
            Arrays.asList(createTier(0, 2, 1100, "fixed"), createTier(2, null, 1000, "incremental"))));
        shippingCost.put("CA", caCosts);
        
        // Calculate and verify
        int resultUS = calculateShippingCost(orderUS, shippingCost);
        int resultCA = calculateShippingCost(orderCA, shippingCost);
        
        System.out.println("US Order: " + resultUS + " (Expected: 14700)");
        System.out.println("CA Order: " + resultCA + " (Expected: 19100)");
        System.out.println();
    }
    
    // Helper methods to create test data
    
    private static Map<String, Object> createOrder(String country, int mouseQty, int laptopQty) {
        Map<String, Object> order = new HashMap<>();
        order.put("country", country);
        
        List<Map<String, Object>> items = new ArrayList<>();
        
        Map<String, Object> mouse = new HashMap<>();
        mouse.put("product", "mouse");
        mouse.put("quantity", mouseQty);
        items.add(mouse);
        
        Map<String, Object> laptop = new HashMap<>();
        laptop.put("product", "laptop");
        laptop.put("quantity", laptopQty);
        items.add(laptop);
        
        order.put("items", items);
        return order;
    }
    
    private static Map<String, Object> createSimpleCost(String product, int cost) {
        Map<String, Object> costInfo = new HashMap<>();
        costInfo.put("product", product);
        costInfo.put("cost", cost);
        return costInfo;
    }
    
    private static Map<String, Object> createTieredProduct(String product, List<Map<String, Object>> costs) {
        Map<String, Object> costInfo = new HashMap<>();
        costInfo.put("product", product);
        costInfo.put("costs", costs);
        return costInfo;
    }
    
    private static Map<String, Object> createTier(int minQty, Integer maxQty, int cost, String type) {
        Map<String, Object> tier = new HashMap<>();
        tier.put("minQuantity", minQty);
        tier.put("maxQuantity", maxQty);
        tier.put("cost", cost);
        if (type != null) {
            tier.put("type", type);
        }
        return tier;
    }
}