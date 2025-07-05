package de.sesqa.ase.entities;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

/** Represents a single message within a conversation. */
@Entity
public class Message {
  /** Default constructor. Required by JPA. */
  public Message() {}

  /** Defines the originator of the message. */
  public enum MessageType {
    /** A message from the user. */
    USER,
    /** A message from the bot. */
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

  /**
   * Constructs a new Message.
   *
   * @param messageType The type of the message (USER or BOT).
   * @param conversation The conversation this message belongs to.
   * @param message The text content of the message.
   */
  @SuppressFBWarnings("EI_EXPOSE_REP2")
  public Message(MessageType messageType, Conversation conversation, String message) {
    this.content = message;
    this.conversation = conversation;
    this.messageType = messageType;
  }

  /**
   * Gets the type of the message.
   *
   * @return The message type (USER or BOT).
   */
  public MessageType getMessageType() {
    return messageType;
  }

  /**
   * Gets the conversation this message is part of.
   *
   * @return The parent conversation.
   */
  @SuppressFBWarnings("EI_EXPOSE_REP")
  public Conversation getConversation() {
    return conversation;
  }

  /**
   * Gets the text content of the message.
   *
   * @return The message content.
   */
  public String getContent() {
    return content;
  }

  /**
   * Gets the unique identifier for this message.
   *
   * @return The message's ID.
   */
  public long getId() {
    return id;
  }

  /**
   * Checks if the message content is null or empty.
   *
   * @return true if the content is null or consists only of whitespace, false otherwise.
   */
  public boolean isEmpty() {
    return content == null || content.trim().isEmpty();
  }
}
