package de.sesqa.ase.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import de.sesqa.ase.dto.ConversationSummaryResponse;
import de.sesqa.ase.dto.MessageResponse;
import de.sesqa.ase.entities.Conversation;
import de.sesqa.ase.entities.Message;
import de.sesqa.ase.entities.Message.MessageType;
import de.sesqa.ase.repositories.ConversationRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ConversationServiceTest {

  private ConversationRepository conversationRepository;
  private ConversationService conversationService;

  @BeforeEach
  void setUp() {
    conversationRepository = mock(ConversationRepository.class);
    conversationService = new ConversationService(conversationRepository);
  }

  @Test
  void getHistory_returnsSummaryWithFirstMessageAsTitle() {
    Conversation conversation = new Conversation(1L);
    Message message = new Message(MessageType.USER, conversation, "Hello world");
    conversation.addMessages(message);
    when(conversationRepository.findAll()).thenReturn(List.of(conversation));

    List<ConversationSummaryResponse> result = conversationService.getHistory();

    assertThat(result).hasSize(1);
    assertThat(result.get(0).getId()).isEqualTo(1L);
    assertThat(result.get(0).getTitle()).isEqualTo("Hello world");
  }

  @Test
  void getHistory_returnsDefaultTitleWhenNoMessages() {
    Conversation conversation = new Conversation(2L);
    // No messages added
    when(conversationRepository.findAll()).thenReturn(List.of(conversation));

    List<ConversationSummaryResponse> result = conversationService.getHistory();

    assertThat(result).hasSize(1);
    assertThat(result.get(0).getTitle()).isEqualTo("Conversation 2");
  }

  @Test
  void getHistory_truncatesLongTitle() {
    String longContent = "This is a very long message that should be truncated at some point";
    Conversation conversation = new Conversation(3L);
    Message message = new Message(MessageType.USER, conversation, longContent);
    conversation.addMessages(message);
    when(conversationRepository.findAll()).thenReturn(List.of(conversation));

    List<ConversationSummaryResponse> result = conversationService.getHistory();

    assertThat(result.get(0).getTitle()).startsWith(longContent.substring(0, 30));
    assertThat(result.get(0).getTitle()).endsWith("...");
    assertThat(result.get(0).getTitle().length()).isLessThanOrEqualTo(33);
  }

  @Test
  void getHistory_returnsEmptyListWhenNoConversations() {
    when(conversationRepository.findAll()).thenReturn(List.of());

    List<ConversationSummaryResponse> result = conversationService.getHistory();

    assertThat(result).isEmpty();
  }

  @Test
  void getConversationMessages_returnsMessagesForExistingConversation() {
    Conversation conversation = new Conversation(1L);
    Message message1 = new Message(MessageType.USER, conversation, "Hi");
    Message message2 = new Message(MessageType.BOT, conversation, "Hello");
    conversation.addMessages(message1);
    conversation.addMessages(message2);
    when(conversationRepository.findById(1L)).thenReturn(Optional.of(conversation));

    List<MessageResponse> result = conversationService.getConversationMessages(1L);

    assertThat(result).hasSize(2);
    assertThat(result.get(0).getContent()).isEqualTo("Hi");
    // assertThat(result.get(0).type()).isEqualTo("USER");
    assertThat(result.get(1).getContent()).isEqualTo("Hello");
    // assertThat(result.get(1).type()).isEqualTo("BOT");
  }

  @Test
  void getConversationMessages_returnsEmptyListWhenConversationNotFound() {
    when(conversationRepository.findById(99L)).thenReturn(Optional.empty());

    List<MessageResponse> result = conversationService.getConversationMessages(99L);

    assertThat(result).isEmpty();
  }

  @Test
  void getConversationMessages_returnsEmptyListWhenConversationHasNoMessages() {
    Conversation conversation = new Conversation(5L);
    // No messages added
    when(conversationRepository.findById(5L)).thenReturn(Optional.of(conversation));

    List<MessageResponse> result = conversationService.getConversationMessages(5L);

    assertThat(result).isEmpty();
  }
}
