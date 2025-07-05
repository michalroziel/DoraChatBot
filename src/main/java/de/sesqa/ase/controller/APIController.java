package de.sesqa.ase.controller;

import de.sesqa.ase.api.APIWrapper;
import de.sesqa.ase.dto.ChatMessageRequest;
import de.sesqa.ase.dto.ChatMessageResponse;
import de.sesqa.ase.dto.ConversationSummaryResponse;
import de.sesqa.ase.dto.MessageResponse;
import de.sesqa.ase.entities.Conversation;
import de.sesqa.ase.entities.Message;
import de.sesqa.ase.metrics.CollectdClient;
import de.sesqa.ase.repositories.ConversationRepository;
import de.sesqa.ase.repositories.MessageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class APIController {

    private static final Logger logger = LoggerFactory.getLogger(APIController.class);


    private final MessageRepository messageRepository;
    private final ConversationRepository conversationRepository;
    private final CollectdClient collectdClient;

    public APIController(MessageRepository messageRepository, ConversationRepository conversationRepository) {
        this.messageRepository = messageRepository;
        this.conversationRepository = conversationRepository;
        this.collectdClient = new CollectdClient();
    }


    /**
     * API endpoint to retrieve a summary of all chat conversations.
     *
     * @return A list of {@link ConversationSummaryResponse} objects.
     */
    @GetMapping("/api/chat/history")
    @ResponseBody
    public List<ConversationSummaryResponse> getHistory() {
        return conversationRepository.findAll().stream()
                .map(conversation -> {
                    String title = conversation.getMessages().stream()
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
     * API endpoint to retrieve all messages for a specific conversation.
     *
     * @param id The ID of the conversation.
     * @return A list of {@link MessageResponse} objects for the given conversation, or an empty list if not found.
     */
    @GetMapping("/api/chat/conversation/{id}")
    @ResponseBody
    public List<MessageResponse> getConversationMessages(@PathVariable Long id) {
        return conversationRepository.findById(id)
                .map(conversation -> conversation.getMessages().stream()
                        .map(message -> new MessageResponse(message.getContent(), message.getMessageType().name()))
                        .collect(Collectors.toList()))
                .orElse(List.of()); // Return an empty list if conversation not found
    }

    /**
     * Handles incoming chat messages from the user.
     * It finds or creates a conversation, saves the user message, queries an external API for a response,
     * saves the bot's response, and returns it to the client.
     *
     * @param request The chat message request from the client.
     * @return A {@link ChatMessageResponse} containing the bot's reply and the conversation ID.
     */
    @PostMapping("/api/chat/message")
    @ResponseBody
    public ChatMessageResponse handleMessage(@RequestBody ChatMessageRequest request) {
        if (request == null) {
            logger.error("Received null ChatMessageRequest");
            return new ChatMessageResponse("Request cannot be null.", null);
        }
        logger.info("""
                Received chat message request:\
                
                conversationId: {}
                content: {}""", request.getConversationId(), request.getContent());
        try {
            Conversation conversation;
            if (request.getConversationId() != null) {
                conversation = conversationRepository.findById(request.getConversationId())
                        .orElseGet(this::createConversation);
            } else {
                conversation = createConversation();
                logger.info("Creating new conversation with id: {}", conversation.getId());
            }

            sendReceivedMetrics();

            Message userMsg = saveUserMessage(conversation, request.getContent());
            Message responseMessage = APIWrapper.query(userMsg);
            logger.info("Received response from APIWrapper: '{}'", responseMessage.getContent());

            if (responseMessage.isEmpty()) {
                logger.info("Response message is empty");
                return new ChatMessageResponse("No response from the AI model.", conversation.getId());
            }

            Message botMessage = new Message(Message.MessageType.BOT, conversation, responseMessage.getContent());
            messageRepository.save(botMessage);
            logger.info("Bot message saved for conversation with id {}", conversation.getId());

            return new ChatMessageResponse(botMessage.getContent(), conversation.getId());
        } catch (Exception e) {
            logger.error("Error processing message", e);
            Long conversationId = request.getConversationId();
            return new ChatMessageResponse("Error processing message: " + e.getMessage(), conversationId);
        }
    }


    /**
     * Creates and persists a new Conversation entity.
     *
     * @return The newly created {@link Conversation}.
     */
    private Conversation createConversation() {
        Conversation conversation = new Conversation();
        conversationRepository.save(conversation);
        logger.info("New conversation started with ID: {}", conversation.getId());

        return conversation;
    }

    /**
     * Sends metrics about received messages to Collectd.
     */
    private void sendReceivedMetrics() {
        long count = messageRepository.count();
        collectdClient.sendMetric("received", CollectdClient.CollectdType.GAUGE, count);
        logger.info("Sending received metrics to Collectd: message count = {}", count);
    }

    /**
     * Creates and saves a user message to the database.
     *
     * @param conversation The conversation the message belongs to.
     * @param message      The content of the user's message.
     * @return The saved {@link Message} entity.
     */
    private Message saveUserMessage(Conversation conversation, String message) {
        Message userMsg = new Message(Message.MessageType.USER, conversation, message);
        messageRepository.save(userMsg);
        logger.info("User message saved...");

        return userMsg;
    }
}
