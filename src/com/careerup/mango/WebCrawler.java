package com.careerup.mango;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/*
    Implement a multithreaded web crawler:
        - Start from a given URL
        - Crawl only URLs under the same hostname
        - Use multiple threads
        - Avoid revisiting URLs
        - Use a provided HtmlParser interface
 */
public class WebCrawler {

    public List<String> crawl(String startUrl, HtmlParser parser) throws InterruptedException {
        String host = getHost(startUrl);

        // Thread-safe set to avoid processing the same URL multiple times concurrently
        Set<String> visited = ConcurrentHashMap.newKeySet();
        visited.add(startUrl);

        // BlockingQueue acts as the frontier for URLs to be processed.
        // It handles thread-safe producer-consumer logic.
        BlockingQueue<String> queue = new LinkedBlockingQueue<>();
        // If we need depth control, we can calculate the depth of the current URL
        // and only add it to the queue if the depth is less than the maximum depth.
        queue.offer(startUrl);

        // Create a fixed thread pool to execute crawling tasks in parallel
        int THREADS = 8;
        ExecutorService pool = Executors.newFixedThreadPool(THREADS);

        // Atomic counter to track the number of currently executing tasks.
        // This is crucial for determining when to stop the crawler.
        AtomicInteger active = new AtomicInteger(0);

        while (true) {
            // Wait for a URL to become available in the queue with a timeout.
            // The timeout allows the main thread to periodically check for termination.
            String url = queue.poll(200, TimeUnit.MILLISECONDS);

            // Termination condition:
            // 1. No URLs left in the queue (url == null)
            // 2. No active tasks running in the thread pool (active.get() == 0)
            // If both are true, it means we are completely done.
            if (url == null && active.get() == 0) {
                break;
            }

            // If timed out but there are still active tasks dealing with other URLs,
            // we just continue waiting. They might add new URLs to the queue.
            if (url == null) {
                continue;
            }

            // Increment active count BEFORE submitting the task.
            // This prevents a race condition where the task finishes/queue empties
            // before the main loop realizes there's work in flight.
            active.incrementAndGet();

            pool.submit(() -> {
                try {
                    for (String next : parser.getUrls(url)) {
                        // Only add URLs that belong to the same host and haven't been visited yet.
                        // visited.add() returns true only if the element was not already present.
                        if (getHost(next).equals(host) && visited.add(next)) {
                            // TODO: Do some processing on the new URL

                            // Add the new URL to the queue for future processing
                            queue.offer(next);
                        }
                    }
                } finally {
                    // Decrement active count when the task completes (successfully or not)
                    active.decrementAndGet();
                }
            });
        }

        // Clean shutdown of the thread pool
        pool.shutdown();
        pool.awaitTermination(10, TimeUnit.SECONDS);

        return new ArrayList<>(visited);
    }

    private String getHost(String url) {
        // Example: http://news.yahoo.com/a/b → news.yahoo.com
        return url.split("/")[2];
    }
}

interface HtmlParser {
    List<String> getUrls(String url);
}
