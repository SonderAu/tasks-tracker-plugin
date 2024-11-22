package net.reldo.taskstracker.data.reldo;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.reldo.taskstracker.TasksTrackerConfig;
import net.runelite.client.config.ConfigManager;
import okhttp3.*;

import java.io.IOException;

public class ReldoAuth {

    private final ConfigManager configManager;
    private final TasksTrackerConfig config;
    private final OkHttpClient httpClient;
    private final String configGroupName;

    public ReldoAuth(ConfigManager configManager, TasksTrackerConfig config, OkHttpClient httpClient, String configGroupName) {
        this.configManager = configManager;
        this.config = config;
        this.httpClient = httpClient;
        this.configGroupName = configGroupName;
    }

    public void authenticateUser(String username, String password) {
        String json = String.format("{\"username\":\"%s\", \"password\":\"%s\"}", username, password);
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), json);
        Request request = new Request.Builder()
                .url(config.serverUrl() + "/api/login") // Adjust endpoint as needed
                .post(body)
                .build();

        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                System.err.println("Authentication failed: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    if (!response.isSuccessful()) {
                        throw new IOException("Unexpected code " + response);
                    }
                    String responseBody = response.body().string();
                    JsonObject jsonResponse = new Gson().fromJson(responseBody, JsonObject.class);
                    String token = jsonResponse.get("token").getAsString();

                    // Store the token in ConfigManager
                    configManager.setConfiguration(configGroupName, "authToken", token);
                    System.out.println("Authentication successful. Token stored: " + token);
                } finally {
                    response.close();
                }
            }
        });
    }

    public String getToken() {
        String token = configManager.getConfiguration(configGroupName, "authToken");
        if (token == null || token.isEmpty()) {
            throw new IllegalStateException("No token available. Please log in first.");
        }
        return token;
    }
}
