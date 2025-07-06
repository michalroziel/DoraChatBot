package de.sesqa.ase.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class ChatMessageRequestTest {

    @Test
    @DisplayName("constructor sets content and conversationId correctly")
    void constructorSetsContentAndConversationIdCorrectly() {
        ChatMessageRequest request = new ChatMessageRequest("Hallo", 123L);
        assertThat(request.getContent()).isEqualTo("Hallo");
        assertThat(request.getConversationId()).isEqualTo(123L);
    }

    @Test
    @DisplayName("setContent updates content field")
    void setContentUpdatesContentField() {
        ChatMessageRequest request = new ChatMessageRequest("alt", 1L);
        request.setContent("neu");
        assertThat(request.getContent()).isEqualTo("neu");
    }

    @Test
    @DisplayName("setConversationId updates conversationId field")
    void setConversationIdUpdatesConversationIdField() {
        ChatMessageRequest request = new ChatMessageRequest("msg", 1L);
        request.setConversationId(99L);
        assertThat(request.getConversationId()).isEqualTo(99L);
    }

    @Test
    @DisplayName("allows null content and conversationId")
    void allowsNullContentAndConversationId() {
        ChatMessageRequest request = new ChatMessageRequest(null, null);
        assertThat(request.getContent()).isNull();
        assertThat(request.getConversationId()).isNull();
    }
}