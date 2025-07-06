package de.sesqa.ase.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import de.sesqa.ase.entities.Conversation;
import de.sesqa.ase.entities.Message;
import de.sesqa.ase.entities.Message.MessageType;
import io.github.cdimascio.dotenv.Dotenv;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ApiWrapperTest {

    @Test
    @DisplayName("query returns BOT message with API response content on success")
    void queryReturnsBotMessageWithApiResponseContentOnSuccess() throws Exception {
        Conversation conversation = new Conversation(1L);
        Message userMessage = new Message(MessageType.USER, conversation, "Hello?");
        HttpClient httpClient = mock(HttpClient.class);
        HttpResponse<String> httpResponse = mock(HttpResponse.class);

        String apiResponse = "{\"choices\":[{\"message\":{\"content\":\"Hi there!\"}}]}";
        when(httpResponse.body()).thenReturn(apiResponse);
        when(httpClient.send(any(), any(HttpResponse.BodyHandler.class))).thenReturn(httpResponse);

        ApiWrapper.setHttpClient(httpClient);

        Message result = ApiWrapper.query(userMessage);

        assertThat(result.getMessageType()).isEqualTo(MessageType.BOT);
        assertThat(result.getConversation()).isEqualTo(conversation);
        assertThat(result.getContent()).isEqualTo("Hi there!");
    }

    @Test
    @DisplayName("query returns BOT message with empty content if API throws IOException")
    void queryReturnsBotMessageWithEmptyContentOnIOException() throws Exception {
        Conversation conversation = new Conversation(2L);
        Message userMessage = new Message(MessageType.USER, conversation, "Hello?");
        HttpClient httpClient = mock(HttpClient.class);

        when(httpClient.send(any(), any())).thenThrow(new IOException("API error"));

        ApiWrapper.setHttpClient(httpClient);

        Message result = ApiWrapper.query(userMessage);

        assertThat(result.getMessageType()).isEqualTo(MessageType.BOT);
        assertThat(result.getConversation()).isEqualTo(conversation);
        assertThat(result.getContent()).isEmpty();
    }

    @Test
    @DisplayName("query returns BOT message with empty content if API throws InterruptedException")
    void queryReturnsBotMessageWithEmptyContentOnInterruptedException() throws Exception {
        Conversation conversation = new Conversation(3L);
        Message userMessage = new Message(MessageType.USER, conversation, "Hello?");
        HttpClient httpClient = mock(HttpClient.class);

        when(httpClient.send(any(), any())).thenThrow(new InterruptedException("Interrupted"));

        ApiWrapper.setHttpClient(httpClient);

        Message result = ApiWrapper.query(userMessage);

        assertThat(result.getMessageType()).isEqualTo(MessageType.BOT);
        assertThat(result.getConversation()).isEqualTo(conversation);
        assertThat(result.getContent()).isEmpty();
    }

    @Test
    @DisplayName("buildHttpRequest builds request with correct headers and body")
    void buildHttpRequestBuildsCorrectRequest() throws Exception {
        Dotenv dotenv = mock(Dotenv.class);
        when(dotenv.get("API_KEY")).thenReturn("test-key");

        ApiWrapper.setDotenv(dotenv);

        Method method = ApiWrapper.class.getDeclaredMethod("buildHttpRequest", String.class);
        method.setAccessible(true);
        HttpRequest request = (HttpRequest) method.invoke(null, "test message");

        assertThat(request.headers().firstValue("Content-Type")).hasValue("application/json");
        assertThat(request.headers().firstValue("Authorization")).hasValue("Bearer test-key");
        assertThat(request.uri().toString()).isEqualTo("https://api.openai.com/v1/chat/completions");
    }

    @Test
    @DisplayName("parseHttpResponse extracts content from valid API response")
    void parseHttpResponseExtractsContent() throws Exception {
        String response = "{\"choices\":[{\"message\":{\"content\":\"Hello!\"}}]}";
        Method method = ApiWrapper.class.getDeclaredMethod("parseHttpResponse", String.class);
        method.setAccessible(true);
        String content = (String) method.invoke(null, response);
        assertThat(content).isEqualTo("Hello!");
    }

    @Test
    @DisplayName("parseHttpResponse returns empty string if content is missing")
    void parseHttpResponseReturnsEmptyStringIfContentMissing() throws Exception {
        String response = "{\"choices\":[{\"message\":{}}]}";
        Method method = ApiWrapper.class.getDeclaredMethod("parseHttpResponse", String.class);
        method.setAccessible(true);
        String content = (String) method.invoke(null, response);
        assertThat(content).isEmpty();
    }
}
