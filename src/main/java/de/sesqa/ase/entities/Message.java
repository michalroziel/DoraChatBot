package de.sesqa.ase.entities;

import jakarta.persistence.*;

@Entity
public class Message {
    public Message() {}

    public enum MessageType{
        USER,
        BOT
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "conversation_id")
    private Conversation conversation;

    private MessageType messageType;
    private String content;

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

    public long getId() {
        return id;
    }
    public boolean isEmpty() {
        return content == null || content.trim().isEmpty();
    }
}
