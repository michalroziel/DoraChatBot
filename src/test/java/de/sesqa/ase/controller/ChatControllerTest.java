package de.sesqa.ase.controller;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import de.sesqa.ase.dto.ChatMessageRequest;
import de.sesqa.ase.dto.ChatMessageResponse;
import de.sesqa.ase.dto.ConversationSummaryResponse;
import de.sesqa.ase.dto.MessageResponse;
import de.sesqa.ase.services.ConversationService;
import de.sesqa.ase.services.MessageService;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ChatControllerTest {

  private MessageService messageService;
  private ConversationService conversationService;
  private ChatController chatController;

  @BeforeEach
  void setUp() {
    messageService = mock(MessageService.class);
    conversationService = mock(ConversationService.class);
    chatController = new ChatController(messageService, conversationService);
  }

  @Test
  @DisplayName("handleMessage returns response from messageService for valid request")
  void handleMessageReturnsResponseForValidRequest() {
    ChatMessageRequest request = new ChatMessageRequest();
    ChatMessageResponse expectedResponse = new ChatMessageResponse("Test response", 1L);
    when(messageService.handleMessage(request)).thenReturn(expectedResponse);

    ChatMessageResponse response = chatController.handleMessage(request);

    assertThat(response).isSameAs(expectedResponse);
  }

  @Test
  @DisplayName("handleMessage handles null request gracefully")
  void handleMessageHandlesNullRequest() {
    when(messageService.handleMessage(null)).thenReturn(null);

    ChatMessageResponse response = chatController.handleMessage(null);

    assertThat(response).isNull();
  }

  @Test
  @DisplayName("getHistory returns list of conversation summaries")
  void getHistoryReturnsConversationSummaries() {
    List<ConversationSummaryResponse> summaries =
        Arrays.asList(new ConversationSummaryResponse(1L, "Test Conversation"));
    when(conversationService.getHistory()).thenReturn(summaries);

    List<ConversationSummaryResponse> result = chatController.getHistory();

    assertThat(result).isEqualTo(summaries);
  }

  @Test
  @DisplayName("getHistory returns empty list when there are no conversations")
  void getHistoryReturnsEmptyListWhenNoConversations() {
    when(conversationService.getHistory()).thenReturn(Collections.emptyList());

    List<ConversationSummaryResponse> result = chatController.getHistory();

    assertThat(result).isEmpty();
  }

  @Test
  @DisplayName("getConversation returns messages for valid conversation id")
  void getConversationReturnsMessagesForValidId() {
    Long conversationId = 1L;
    List<MessageResponse> messages = Arrays.asList(new MessageResponse("Test", "Test message"));
    when(conversationService.getConversationMessages(conversationId)).thenReturn(messages);

    List<MessageResponse> result = chatController.getConversation(conversationId);

    assertThat(result).isEqualTo(messages);
  }

  @Test
  @DisplayName("getConversation returns empty list for unknown conversation id")
  void getConversationReturnsEmptyListForUnknownId() {
    Long unknownId = 999L;
    when(conversationService.getConversationMessages(unknownId))
        .thenReturn(Collections.emptyList());

    List<MessageResponse> result = chatController.getConversation(unknownId);

    assertThat(result).isEmpty();
  }
}
