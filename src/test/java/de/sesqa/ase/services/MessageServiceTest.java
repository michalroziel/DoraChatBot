package de.sesqa.ase.services;

import de.sesqa.ase.api.ApiWrapper;
import de.sesqa.ase.dto.ChatMessageRequest;
import de.sesqa.ase.dto.ChatMessageResponse;
import de.sesqa.ase.entities.Conversation;
import de.sesqa.ase.entities.Message;
import de.sesqa.ase.repositories.ConversationRepository;
import de.sesqa.ase.repositories.MessageRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.lang.reflect.Field;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MessageServiceTest {
    @Test
    @DisplayName("handleMessage returns error response when request is null")
    void handleMessageReturnsErrorResponseWhenRequestIsNull() {
        MessageRepository messageRepository = mock(MessageRepository.class);
        ConversationRepository conversationRepository = mock(ConversationRepository.class);

        MessageService service = new MessageService(messageRepository, conversationRepository);

        ChatMessageResponse response = service.handleMessage(null);

        assertThat(response.getContent()).isEqualTo("Request cannot be null.");
        assertThat(response.getConversationId()).isNull();
    }

    @Test
    @DisplayName("handleMessage creates new conversation if conversationId is null")
    void handleMessageCreatesNewConversationIfConversationIdIsNull() throws Exception {
        MessageRepository messageRepository = mock(MessageRepository.class);
        ConversationRepository conversationRepository = mock(ConversationRepository.class);

        when(conversationRepository.save(any(Conversation.class))).thenAnswer(invocation -> {
            Conversation conv = invocation.getArgument(0);
            Field idField = conv.getClass().getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(conv, 42L);
            return conv;
        });

        when(messageRepository.save(any(Message.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(messageRepository.count()).thenReturn(0L);

        ChatMessageRequest request = new ChatMessageRequest("Hallo", null);

        try (MockedStatic<ApiWrapper> apiWrapperMock = Mockito.mockStatic(ApiWrapper.class)) {
            Conversation dummyConv = new Conversation();
            Field idField = dummyConv.getClass().getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(dummyConv, 42L);

            Message apiResponse = new Message(Message.MessageType.BOT, dummyConv, "Antwort");
            apiWrapperMock.when(() -> ApiWrapper.query(any(Message.class))).thenReturn(apiResponse);

            MessageService service = new MessageService(messageRepository, conversationRepository);

            ChatMessageResponse response = service.handleMessage(request);

            assertThat(response.getContent()).isEqualTo("Antwort");
            assertThat(response.getConversationId()).isEqualTo(42L);
        }
    }

    @Test
    @DisplayName("handleMessage uses existing conversation if conversationId exists")
    void handleMessageUsesExistingConversationIfConversationIdExists() throws Exception {
        MessageRepository messageRepository = mock(MessageRepository.class);
        ConversationRepository conversationRepository = mock(ConversationRepository.class);

        Conversation existingConversation = mock(Conversation.class);
        when(existingConversation.getId()).thenReturn(99L);
        when(conversationRepository.findById(99L)).thenReturn(Optional.of(existingConversation));
        when(messageRepository.save(any(Message.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(messageRepository.count()).thenReturn(1L);

        ChatMessageRequest request = new ChatMessageRequest("Hi", 99L);

        try (MockedStatic<ApiWrapper> apiWrapperMock = Mockito.mockStatic(ApiWrapper.class)) {
            Message apiResponse = new Message(Message.MessageType.BOT, existingConversation, "BotAntwort");
            apiWrapperMock.when(() -> ApiWrapper.query(any(Message.class))).thenReturn(apiResponse);

            MessageService service = new MessageService(messageRepository, conversationRepository);

            ChatMessageResponse response = service.handleMessage(request);

            assertThat(response.getContent()).isEqualTo("BotAntwort");
            assertThat(response.getConversationId()).isEqualTo(99L);
        }
    }

    @Test
    @DisplayName("handleMessage returns error response if exception is thrown")
    void handleMessageReturnsErrorResponseIfExceptionIsThrown() {
        MessageRepository messageRepository = mock(MessageRepository.class);
        ConversationRepository conversationRepository = mock(ConversationRepository.class);

        when(conversationRepository.findById(anyLong())).thenThrow(new RuntimeException("DB down"));

        ChatMessageRequest request = new ChatMessageRequest("Fehler", 123L);

        MessageService service = new MessageService(messageRepository, conversationRepository);

        ChatMessageResponse response = service.handleMessage(request);

        assertThat(response.getContent()).contains("Error processing message: DB down");
        assertThat(response.getConversationId()).isEqualTo(123L);
    }

    @Test
    @DisplayName("handleMessage returns no response message if API returns empty message")
    void handleMessageReturnsNoResponseMessageIfApiReturnsEmptyMessage() {
        MessageRepository messageRepository = mock(MessageRepository.class);
        ConversationRepository conversationRepository = mock(ConversationRepository.class);

        Conversation conversation = mock(Conversation.class);
        when(conversation.getId()).thenReturn(7L);
        when(conversationRepository.findById(7L)).thenReturn(Optional.of(conversation));
        when(messageRepository.save(any(Message.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(messageRepository.count()).thenReturn(2L);

        ChatMessageRequest request = new ChatMessageRequest("?", 7L);

        try (MockedStatic<ApiWrapper> apiWrapperMock = Mockito.mockStatic(ApiWrapper.class)) {
            Message emptyApiResponse = mock(Message.class);
            when(emptyApiResponse.isEmpty()).thenReturn(true);
            when(emptyApiResponse.getContent()).thenReturn("");
            apiWrapperMock.when(() -> ApiWrapper.query(any(Message.class))).thenReturn(emptyApiResponse);

            MessageService service = new MessageService(messageRepository, conversationRepository);

            ChatMessageResponse response = service.handleMessage(request);

            assertThat(response.getContent()).isEqualTo("No response from the AI model.");
            assertThat(response.getConversationId()).isEqualTo(7L);
        }
    }
}