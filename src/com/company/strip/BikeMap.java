package com.careerup.strip;

import com.google.gson.*;
import java.net.URI;
import java.net.http.*;
import java.nio.file.*;

public class BikeMap {

    public static void main(String[] args) throws Exception {

        Gson gson = new GsonBuilder().create();

        // Read JSON
        String jsonStr = Files.readString(Paths.get("data/ride-simple.json"));
        JsonObject ride = JsonParser.parseString(jsonStr).getAsJsonObject();

        // Build markers
        JsonArray markers = new JsonArray();

        JsonObject start = ride.getAsJsonObject("start");
        JsonObject end = ride.getAsJsonObject("end");
        JsonArray waypoints = ride.getAsJsonArray("waypoints");

        markers.add(makeMarker(start, "green"));
        markers.add(makeMarker(end, "red"));

        for (JsonElement wp : waypoints) {
            markers.add(makeMarker(wp.getAsJsonObject(), "blue"));
        }

        // Build path points
        JsonArray points = new JsonArray();
        points.add(start);
        waypoints.forEach(points::add);
        points.add(end);

        JsonObject path = new JsonObject();
        path.add("points", points);
        path.addProperty("color", "black");

        JsonObject payload = new JsonObject();
        payload.add("markers", markers);
        payload.add("paths", new JsonArray());
        payload.getAsJsonArray("paths").add(path);

        // POST request
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create("https://example.com/map/render"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(payload)))
                .build();

        HttpResponse<byte[]> resp =
                client.send(req, HttpResponse.BodyHandlers.ofByteArray());

        // Save image
        Files.write(Paths.get("output/map.png"), resp.body());
    }

    private static JsonObject makeMarker(JsonObject point, String color) {
        JsonObject m = new JsonObject();
        m.addProperty("lat", point.get("lat").getAsDouble());
        m.addProperty("lng", point.get("lng").getAsDouble());
        m.addProperty("color", color);
        return m;
    }
}

