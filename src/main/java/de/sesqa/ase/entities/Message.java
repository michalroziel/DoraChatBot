package de.sesqa.ase.entities;

import jakarta.persistence.*;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;


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

    @SuppressFBWarnings("EI_EXPOSE_REP2")
    public Message(MessageType messageType, Conversation conversation, String message) {
        this.content = message;
        this.conversation = conversation;
        this.messageType = messageType;
    }

    public MessageType getMessageType() {
        return messageType;
    }

    @SuppressFBWarnings("EI_EXPOSE_REP")
    public Conversation getConversation() {
        return conversation;
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
