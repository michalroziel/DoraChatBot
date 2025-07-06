package de.sesqa.ase.dto;

public class ChatMessageResponseTest {
    @org.junit.jupiter.api.Test
    @org.junit.jupiter.api.DisplayName("constructor sets content and conversationId correctly")
    void constructorSetsContentAndConversationIdCorrectly() {
        ChatMessageResponse response = new ChatMessageResponse("Hallo", 123L);
        org.assertj.core.api.Assertions.assertThat(response.getContent()).isEqualTo("Hallo");
        org.assertj.core.api.Assertions.assertThat(response.getConversationId()).isEqualTo(123L);
    }

    @org.junit.jupiter.api.Test
    @org.junit.jupiter.api.DisplayName("setContent updates content field")
    void setContentUpdatesContentField() {
        ChatMessageResponse response = new ChatMessageResponse("alt", 1L);
        response.setContent("neu");
        org.assertj.core.api.Assertions.assertThat(response.getContent()).isEqualTo("neu");
    }

    @org.junit.jupiter.api.Test
    @org.junit.jupiter.api.DisplayName("setConversationId updates conversationId field")
    void setConversationIdUpdatesConversationIdField() {
        ChatMessageResponse response = new ChatMessageResponse("msg", 1L);
        response.setConversationId(99L);
        org.assertj.core.api.Assertions.assertThat(response.getConversationId()).isEqualTo(99L);
    }

    @org.junit.jupiter.api.Test
    @org.junit.jupiter.api.DisplayName("allows null content and conversationId")
    void allowsNullContentAndConversationId() {
        ChatMessageResponse response = new ChatMessageResponse(null, null);
        org.assertj.core.api.Assertions.assertThat(response.getContent()).isNull();
        org.assertj.core.api.Assertions.assertThat(response.getConversationId()).isNull();
    }
}
