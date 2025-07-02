package de.sesqa.ase.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.sesqa.ase.entities.Message;
import io.github.cdimascio.dotenv.Dotenv;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class APIWrapper {
    private static final Logger LOGGER = LoggerFactory.getLogger(APIWrapper.class);
    private static final Dotenv DOTENV = Dotenv.configure().ignoreIfMissing().load();
    private static final HttpClient HTTP_CLIENT = HttpClient.newHttpClient();
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public static Message query(Message message) throws IOException, InterruptedException {
        HttpRequest request = buildHttpRequest(message.getContent());
        String responseBody = "";

        try {
            HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
            responseBody = parseHttpResponse(response.body());
            
        }catch (IOException | InterruptedException e) {
            LOGGER.error("Error during API request: " + e.getMessage());
        }finally {
            LOGGER.info("Query completed.\n");
        }

        return new Message(Message.MessageType.BOT, message.getConversation(), responseBody);
    }

    private static HttpRequest buildHttpRequest(String messageContent) {
        String body = String.format("{\"model\": \"gpt-4.1-nano\", \"messages\": [{\"role\": \"user\", \"content\": \"%s\"}]}", messageContent);
        String apiURL = "https://api.openai.com/v1/chat/completions";
        String apiKey = DOTENV.get("API_KEY");

        return HttpRequest.newBuilder()
                .uri(URI.create(apiURL))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + apiKey)
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();
    }

    private static String parseHttpResponse(String responseBody) throws IOException {
        // Parse response body to extract content inside message
        JsonNode rootNode = OBJECT_MAPPER.readTree(responseBody);
        return rootNode.path("choices").get(0).path("message").path("content").asText();
    }


}
