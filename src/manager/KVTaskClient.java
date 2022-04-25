package manager;

import Data.Task;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class KVTaskClient {
    private static final String REQUEST_TEMPLATE = "%s/%s/%s?API_KEY=%s";
    private final HttpClient client = HttpClient.newHttpClient();
    private final String url;
    private String apiKey;
    Gson gson = new Gson();

    private String registerUrl() {
        return String.format("%s/register", this.url);
    }

    private String saveUrl(String key) {
        if (apiKey == null) {
            throw new IllegalStateException("Call KVTaskClient.register() before using save()");
        }
        return String.format(REQUEST_TEMPLATE, this.url, "save", key, apiKey);
    }

    private String loadUrl(String key) {
        if (apiKey == null) {
            throw new IllegalStateException("Call KVTaskClient.register() before using load()");
        }
        return String.format(REQUEST_TEMPLATE, this.url, "load", key, apiKey);
    }

    public KVTaskClient(String url) {
        this.url = url;
    }

    public void register() {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(registerUrl()))
                .GET()
                .build();
        try {
            String apiKey = client.send(request, HttpResponse.BodyHandlers.ofString()).body();
            System.out.println(apiKey);
            if (apiKey == null) {
                throw new IllegalStateException("Can't register to kv storage!");
            }
            this.apiKey = apiKey;
        } catch (IOException | InterruptedException e) {
            throw new IllegalStateException("Can't register to kv storage");
        }
    }

    public void put(String key, String value) {
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(value);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(saveUrl(key)))
                .POST(body)
                .build();
        try {
            client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            throw new IllegalArgumentException("Can't write to DB");
        }
    }

    public Map<Integer, Task> load(String key) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(loadUrl(key)))
                .GET()
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            Type listType = new TypeToken<HashMap<Integer, Task>>() {
            }.getType();
            return gson.fromJson(response.body(), listType);
//            return gson.fromJson(response.body(), Map.class);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Task> loadHistory(String key) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(loadUrl(key)))
                .GET()
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            Type listType = new TypeToken<List<Task>>(){}.getType();
            return gson.fromJson(response.body(), listType);
//            return gson.fromJson(response.body(), Map.class);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
