package net.reldo.taskstracker.data.reldo;

import com.google.gson.Gson;
import net.reldo.taskstracker.HttpUtil;
import net.reldo.taskstracker.TasksTrackerConfig;
import net.reldo.taskstracker.TasksTrackerPlugin;
import net.runelite.client.config.ConfigManager;

import java.util.concurrent.CompletableFuture;

public class ReldoExport {

    private final Gson gson;
    private final TasksTrackerConfig config;
    private final ReldoAuth reldoAuth;

    public ReldoExport(ConfigManager configManager, TasksTrackerConfig config, Gson gson, ReldoAuth reldoAuth) {
        this.gson = gson;
        this.config = config;
        this.reldoAuth = reldoAuth;
    }

    public void exportData(String latestExportedJson) {
        try {
            if (latestExportedJson == null || latestExportedJson.isEmpty()) {
                throw new IllegalStateException("No exported data available. Please copy data to the clipboard first.");
            }

            // Use ReldoAuth to get the token
            String token = reldoAuth.getToken();
            String username = config.username();
            String apiUrl = config.serverUrl() + "/api/plugin-sync";

            System.out.println("Exported JSON: " + latestExportedJson);

            CompletableFuture.runAsync(() -> {
                boolean success = HttpUtil.sendPayloadToApi(apiUrl, gson, username, token, latestExportedJson);
                if (success) {
                    System.out.println("Exported data successfully.");
                } else {
                    System.err.println("Failed to export data.");
                }
            });
        } catch (IllegalStateException e) {
            System.err.println(e.getMessage());
        }
    }
}
