package de.sesqa.ase.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

public class ChatMessageResponseTest {
    @Test
    @DisplayName("constructor sets content and conversationId correctly")
    void constructorSetsContentAndConversationIdCorrectly() {
        ChatMessageResponse response = new ChatMessageResponse("Hallo", 123L);
        assertThat(response.getContent()).isEqualTo("Hallo");
        assertThat(response.getConversationId()).isEqualTo(123L);
    }

    @Test
    @DisplayName("setContent updates content field")
    void setContentUpdatesContentField() {
        ChatMessageResponse response = new ChatMessageResponse("alt", 1L);
        response.setContent("neu");
        assertThat(response.getContent()).isEqualTo("neu");
    }

    @Test
    @DisplayName("setConversationId updates conversationId field")
    void setConversationIdUpdatesConversationIdField() {
        ChatMessageResponse response = new ChatMessageResponse("msg", 1L);
        response.setConversationId(99L);
        assertThat(response.getConversationId()).isEqualTo(99L);
    }

    @Test
    @DisplayName("allows null content and conversationId")
    void allowsNullContentAndConversationId() {
        ChatMessageResponse response = new ChatMessageResponse(null, null);
        assertThat(response.getContent()).isNull();
        assertThat(response.getConversationId()).isNull();
    }
}
