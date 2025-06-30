package de.sesqa.ase.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.sesqa.ase.entities.Message;
import io.github.cdimascio.dotenv.Dotenv;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class APIWrapper {


    public static Message query(Message message) throws IOException, InterruptedException {

        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
        String apiKey = dotenv.get("API_KEY");

        String body = String.format("{\"model\": \"gpt-4.1-nano\", \"messages\": [{\"role\": \"user\", \"content\": \"%s\"}]}", message.getContent());

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.openai.com/v1/chat/completions"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + apiKey)
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        String messageContent;
        try (HttpClient client = HttpClient.newHttpClient()) {

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            String responseBody = response.body();


            // Parse response body to extract content inside message
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(responseBody);
            messageContent = rootNode.path("choices").get(0).path("message").path("content").asText();
        } finally {
            System.out.println("Query completed.\n");
        }

        return new Message(Message.MessageType.BOT, message.getConversation(), messageContent);

    }


}
