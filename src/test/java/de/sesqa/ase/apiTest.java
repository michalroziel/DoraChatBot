package de.sesqa.ase;


import io.github.cdimascio.dotenv.Dotenv;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OpenAiPingTest {

    private static String API_KEY;

    @BeforeAll
    static void setUp(){
        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
        API_KEY = dotenv.get("API_KEY");

        if (API_KEY == null) {
            throw new RuntimeException("API_KEY is not set !! ");
        }
    }

    @Test
    void testPingOpenAiAPI() throws Exception {


        // https://apipark.com/techblog/en/understanding-the-openapi-default-response-vs-200-status-code-a-comprehensive-guide/

        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.openai.com/v1/models"))
                .header("Authorization", "Bearer " + API_KEY)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println("Status Code: " + response.statusCode());
        assertEquals(200, response.statusCode(), "Expected HTTP 200 from OpenAI API");
    }
}