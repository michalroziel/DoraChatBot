package de.sesqa.ase.controller;

import de.sesqa.ase.dto.ChatMessageRequest;
import de.sesqa.ase.dto.ChatMessageResponse;
import de.sesqa.ase.dto.ConversationSummaryResponse;
import de.sesqa.ase.dto.MessageResponse;
import de.sesqa.ase.services.ConversationService;
import de.sesqa.ase.services.MessageService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for handling chat-related endpoints. Provides APIs for sending messages,
 * retrieving chat history, and fetching conversation details.
 */
@RestController
@RequestMapping("/api/chat")
public class ChatController {

  private final MessageService messageService;
  private final ConversationService conversationService;

  /**
   * Constructs a new ChatController with required services.
   *
   * @param messageService the service for handling messages
   * @param conversationService the service for handling conversations
   */
  public ChatController(MessageService messageService, ConversationService conversationService) {
    this.messageService = messageService;
    this.conversationService = conversationService;
  }

  /**
   * Handles incoming chat messages.
   *
   * @param request the chat message request payload
   * @return the response containing the processed message
   */
  @PostMapping("/message")
  public ChatMessageResponse handleMessage(@RequestBody ChatMessageRequest request) {
    return messageService.handleMessage(request);
  }

  /**
   * Retrieves a summary of all chat conversations.
   *
   * @return a list of conversation summary responses
   */
  @GetMapping("/history")
  public List<ConversationSummaryResponse> getHistory() {
    return conversationService.getHistory();
  }

  /**
   * Retrieves all messages for a specific conversation.
   *
   * @param id the ID of the conversation
   * @return a list of message responses for the conversation
   */
  @GetMapping("/conversation/{id}")
  public List<MessageResponse> getConversation(@PathVariable Long id) {
    return conversationService.getConversationMessages(id);
  }
}
