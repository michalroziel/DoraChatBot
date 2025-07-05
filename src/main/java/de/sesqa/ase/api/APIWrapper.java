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

/**
 * A wrapper class for interacting with an external chat completion API (e.g., OpenAI).
 * This class provides a static method to send a user's message to the API and get a response.
 */
public class APIWrapper {
    private static final Logger logger = LoggerFactory.getLogger(APIWrapper.class);
    /**
     * Loads environment variables from a .env file. Used to retrieve the API key.
     */
    private static final Dotenv DOTENV = Dotenv.configure().ignoreIfMissing().load();
    /**
     * A reusable HttpClient instance for making HTTP requests.
     */
    private static final HttpClient HTTP_CLIENT = HttpClient.newHttpClient();
    /**
     * A reusable ObjectMapper for parsing JSON responses.
     */
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    /**
     * Sends a user's message to the chat API and returns the API's response as a new Message.
     *
     * @param message The user's message to be sent to the API.
     * @return A new {@link Message} of type {@code BOT} containing the API's response.
     * @throws IOException          if an I/O error occurs when sending or receiving.
     * @throws InterruptedException if the operation is interrupted.
     */
    public static Message query(Message message) throws IOException, InterruptedException {
        HttpRequest request = buildHttpRequest(message.getContent());
        String responseBody = "";

        try {
            HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
            responseBody = parseHttpResponse(response.body());

        }catch (IOException | InterruptedException e) {
            logger.error("Error during API request: " + e.getMessage());
        }finally {
            logger.info("Query completed.\n");
        }

        return new Message(Message.MessageType.BOT, message.getConversation(), responseBody);
    }

    /**
     * Builds an HTTP POST request for the chat completion API.
     *
     * @param messageContent The text content of the user's message.
     * @return An {@link HttpRequest} object configured for the API call.
     */
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

    /**
     * Parses the JSON response body from the API to extract the message content.
     *
     * @param responseBody The JSON string received from the API.
     * @return The extracted message content as a String.
     * @throws IOException if there is a problem parsing the JSON.
     */
    private static String parseHttpResponse(String responseBody) throws IOException {
        // Parse response body to extract content inside message
        JsonNode rootNode = OBJECT_MAPPER.readTree(responseBody);
        return rootNode.path("choices").get(0).path("message").path("content").asText();
    }


}