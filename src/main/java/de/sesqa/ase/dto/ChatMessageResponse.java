package de.sesqa.ase.dto;

public class ChatMessageResponse {
    private String content;
    private Long conversationId;

    public ChatMessageResponse(String content, Long conversationId) {
        this.content = content;
        this.conversationId = conversationId;
    }

    // Getters and setters
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getConversationId() {
        return conversationId;
    }

    public void setConversationId(Long conversationId) {
        this.conversationId = conversationId;
    }
}