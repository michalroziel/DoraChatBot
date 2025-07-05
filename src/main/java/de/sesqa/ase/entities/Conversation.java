package de.sesqa.ase.entities;

import jakarta.persistence.*;
import java.util.Collections;
import java.util.List;

/** Represents a conversation entity. A conversation consists of a sequence of messages. */
@Entity
public class Conversation {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  /**
   * The list of messages associated with this conversation. This is a one-to-many relationship,
   * where the 'conversation' field in the Message entity owns the relationship. Operations on the
   * conversation (like persist, remove) are cascaded to its messages. Orphaned messages are
   * automatically removed.
   */
  @OneToMany(mappedBy = "conversation", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Message> messages;

  /** Default constructor. Required by JPA. */
  public Conversation() {}

  /**
   * Constructs a new Conversation with a given ID.
   *
   * @param id The identifier for the new conversation.
   */
  public Conversation(long id) {
    this.id = id;
  }

  /**
   * Returns an unmodifiable view of the messages in this conversation. If the message list is null,
   * it returns an empty list.
   *
   * @return An unmodifiable list of messages.
   */
  public List<Message> getMessages() {
    return messages == null ? Collections.emptyList() : Collections.unmodifiableList(messages);
  }

  /**
   * Adds a message to the conversation's list of messages.
   *
   * @param message The message to be added.
   */
  public void addMessages(Message message) {
    messages.add(message);
  }

  /**
   * Gets the unique identifier for this conversation.
   *
   * @return The conversation's ID.
   */
  public long getId() {
    return id;
  }
}
