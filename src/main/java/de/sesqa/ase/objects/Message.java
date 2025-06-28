package de.sesqa.ase.objects;

public class Message {
    public enum MessageType{
        USER,
        BOT
    }

    private final MessageType messageType;
    private final String content;

    public Message(MessageType messageType, String message) {
        this.content = message;
        this.messageType = messageType;
    }

    public MessageType getMessageType() {
        return messageType;
    }

    public String getContent() {
        return content;
    }

    public boolean isEmpty() {
        return content == null || content.trim().isEmpty();
    }
}
