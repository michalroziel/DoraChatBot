package de.sesqa.ase.services;

import de.sesqa.ase.dto.ConversationSummaryResponse;
import de.sesqa.ase.dto.MessageResponse;
import de.sesqa.ase.entities.Message;
import de.sesqa.ase.repositories.ConversationRepository;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Service class for managing chat conversations and their messages.
 *
 * <p>Provides methods to retrieve conversation summaries and messages for a specific conversation.
 */
@Service
public class ConversationService {

  private final ConversationRepository conversationRepository;

  /**
   * Constructs the ConversationService with the necessary repository.
   *
   * @param conversationRepository The repository for conversation data access.
   */
  public ConversationService(ConversationRepository conversationRepository) {
    this.conversationRepository = conversationRepository;
  }

  /**
   * Retrieves a summary of all chat conversations.
   *
   * @return A list of {@link ConversationSummaryResponse} objects, each containing the ID and a
   *     title (first message content or a default title) for a conversation.
   */
  public List<ConversationSummaryResponse> getHistory() {
    return conversationRepository.findAll().stream()
        .map(
            conversation -> {
              String title =
                  conversation.getMessages().stream()
                      .findFirst()
                      .map(Message::getContent)
                      .orElse("Conversation " + conversation.getId());

              if (title.length() > 35) {
                title = title.substring(0, 30) + "...";
              }

              return new ConversationSummaryResponse(conversation.getId(), title);
            })
        .collect(Collectors.toList());
  }

  /**
   * Retrieves all messages for a specific conversation.
   *
   * @param id The ID of the conversation.
   * @return A list of {@link MessageResponse} objects for the given conversation, or an empty list
   *     if the conversation is not found.
   */
  public List<MessageResponse> getConversationMessages(@PathVariable Long id) {
    return conversationRepository
        .findById(id)
        .map(
            conversation ->
                conversation.getMessages().stream()
                    .map(
                        message ->
                            new MessageResponse(
                                message.getContent(), message.getMessageType().name()))
                    .collect(Collectors.toList()))
        .orElse(List.of()); // Return an empty list if conversation not found
  }
}
