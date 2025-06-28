package de.sesqa.ase;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.cdimascio.dotenv.Dotenv;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class Application {


  public static String createQuery(String content) throws IOException, InterruptedException {

    System.out.println("Creating query...\n");

    Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
    String apiKey = dotenv.get("API_KEY");


    // one line because
    String body = String.format("{\"model\": \"gpt-4.1-nano\", \"messages\": [{\"role\": \"user\", \"content\": \"%s\"}]}", content);

    HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("https://api.openai.com/v1/chat/completions"))
            .header("Content-Type", "application/json")
            .header("Authorization","Bearer " + apiKey )
            .POST(HttpRequest.BodyPublishers.ofString(body))
            .build();

      String messageContent;
      try(HttpClient client = HttpClient.newHttpClient()) {

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        String responseBody = response.body();


        // Parse response body to extract content inside message
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(responseBody);
        messageContent = rootNode.path("choices").get(0).path("message").path("content").asText();

      }

      finally {
        System.out.println("Query completed.\n");
      }

    System.out.println(messageContent);



    return messageContent;

  }


}
