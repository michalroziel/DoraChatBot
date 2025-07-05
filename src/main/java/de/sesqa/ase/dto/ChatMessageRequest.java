package de.sesqa.ase.dto;

/**
 * Represents the request object for sending a chat message. This DTO carries the content of the
 * message and the ID of the conversation it belongs to.
 */
public class ChatMessageRequest {
  /** The text content of the chat message. */
  private String content;

  /** The unique identifier of the conversation to which the message belongs. */
  private Long conversationId;

  /**
   * Gets the content of the chat message.
   *
   * @return The message content.
   */
  public String getContent() {
    return content;
  }

  /**
   * Sets the content of the chat message.
   *
   * @param content The message content to set.
   */
  public void setContent(String content) {
    this.content = content;
  }

  /**
   * Gets the ID of the conversation.
   *
   * @return The conversation ID.
   */
  public Long getConversationId() {
    return conversationId;
  }

  /**
   * Sets the ID of the conversation.
   *
   * @param conversationId The conversation ID to set.
   */
  public void setConversationId(Long conversationId) {
    this.conversationId = conversationId;
  }
}
