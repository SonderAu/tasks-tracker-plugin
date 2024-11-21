package net.reldo.taskstracker;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class HttpUtil
{
    private static final Gson gson = new Gson();

    /**
     * Sends a structured payload to the specified API endpoint.
     *
     * @param apiUrl      The API URL to send the data to.
     * @param username    The username of the user.
     * @param token       The authentication token.
     * @param exportedData The exported data to send.
     * @return True if the request was successful, false otherwise.
     */
    public static boolean sendPayloadToApi(String apiUrl, String username, String token, Map<String, Object> exportedData)
    {
        try
        {
            // Structure the payload
            Map<String, Object> payload = Map.of(
                    "username", username,
                    "token", token,
                    "data", exportedData
            );
            String jsonPayload = gson.toJson(payload);


            int responseCode = getResponseCode(apiUrl, jsonPayload);
            if (responseCode == HttpURLConnection.HTTP_OK)
            {
                System.out.println("Data successfully sent!");
                return true;
            }
            else
            {
                System.err.println("Failed to send data. Response Code: " + responseCode);
                return false;
            }
        }
        catch (Exception e)
        {
            return false;
        }
    }

    private static int getResponseCode(String apiUrl, String jsonPayload) throws IOException {
        URL url = new URL(apiUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);

        try (OutputStream os = connection.getOutputStream())
        {
            byte[] input = jsonPayload.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        // Read the response
        return connection.getResponseCode();
    }
}
