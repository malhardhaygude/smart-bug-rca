package com.smart.bugrca.llm;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

@Service
public class LlmService {

    @Value("${openai.api.key:}")
    private String apiKey;

    @Value("${llm.rca.prompt}")
    private String rcaPrompt;

    public boolean isEnabled() {
        return apiKey != null && !apiKey.isBlank();
    }


    public String generateRca(String description, String severity, String environment, List<String> symptoms) throws Exception {
        if (!isEnabled()) {
            throw new IllegalStateException("LLM disabled");
        }

        // 1. Format the prompt
        String promptText = String.format(rcaPrompt,
                description, severity, environment, String.join(", ", symptoms));

        // 2. Build Gemini Request Body
        // Structure: {"contents": [{"parts": [{"text": "..."}]}]}
        JSONObject textPart = new JSONObject().put("text", promptText);
        JSONArray partsArray = new JSONArray().put(textPart);
        JSONObject contentObject = new JSONObject().put("parts", partsArray);
        JSONArray contentsArray = new JSONArray().put(contentObject);

        JSONObject json = new JSONObject();
        json.put("contents", contentsArray);

        // 3. Prepare the HTTP Request
        // Note: The API key is passed as a header 'X-goog-api-key'
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://generativelanguage.googleapis.com/v1beta/models/gemini-flash-latest:generateContent"))
                .header("Content-Type", "application/json")
                .header("X-goog-api-key", apiKey)
                .POST(HttpRequest.BodyPublishers.ofString(json.toString()))
                .build();

        HttpResponse<String> response;
        try (HttpClient client = HttpClient.newHttpClient()) {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        }

        if (response.statusCode() != 200) {
            throw new RuntimeException("API Request failed: " + response.body());
        }

        // 4. Parse Gemini Response
        // Path: candidates[0] -> content -> parts[0] -> text
        JSONObject responseJson = new JSONObject(response.body());
        String aiText = responseJson.getJSONArray("candidates")
                .getJSONObject(0)
                .getJSONObject("content")
                .getJSONArray("parts")
                .getJSONObject(0)
                .getString("text");

        return aiText;
    }
}