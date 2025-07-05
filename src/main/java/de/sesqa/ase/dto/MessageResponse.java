package de.sesqa.ase.dto;

/**
 * Represents a message response containing content and the role of the sender. This DTO is
 * typically used to represent a single message within a conversation.
 */
public class MessageResponse {
  /** The text content of the message. */
  private String content;

  /** The role of the entity that sent the message (e.g., "user", "bot"). */
  private String role;

  /**
   * Constructs a new MessageResponse with specified content and role.
   *
   * @param content The content of the message.
   * @param role The role of the sender.
   */
  public MessageResponse(String content, String role) {
    this.content = content;
    this.role = role;
  }

  /**
   * Gets the content of the message.
   *
   * @return The message content.
   */
  public String getContent() {
    return content;
  }

  /**
   * Sets the content of the message.
   *
   * @param content The message content to set.
   */
  public void setContent(String content) {
    this.content = content;
  }

  /**
   * Gets the role of the sender.
   *
   * @return The sender's role.
   */
  public String getRole() {
    return role;
  }

  /**
   * Sets the role of the sender.
   *
   * @param role The sender's role to set.
   */
  public void setRole(String role) {
    this.role = role;
  }
}
