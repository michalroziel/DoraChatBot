package de.sesqa.ase.services;

import de.sesqa.ase.api.ApiWrapper;
import de.sesqa.ase.dto.ChatMessageRequest;
import de.sesqa.ase.dto.ChatMessageResponse;
import de.sesqa.ase.entities.Conversation;
import de.sesqa.ase.entities.Message;
import de.sesqa.ase.metrics.CollectdClient;
import de.sesqa.ase.repositories.ConversationRepository;
import de.sesqa.ase.repositories.MessageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.IOException;

/**
 * Service class responsible for handling chat messages, managing conversations, persisting
 * messages, and interacting with external APIs and metrics systems.
 */
@Service
public class MessageService {

    /**
     * Logger instance for logging events and errors.
     */
    private static final Logger logger = LoggerFactory.getLogger(MessageService.class);

    /**
     * Repository for accessing and managing Message entities.
     */
    private final MessageRepository messageRepository;

    /**
     * Repository for accessing and managing Conversation entities.
     */
    private final ConversationRepository conversationRepository;

    /**
     * Client for sending metrics to Collectd.
     */
    private final CollectdClient collectdClient;

    /**
     * Constructs the MessageService with necessary repositories.
     *
     * @param messageRepository      Repository for message data access.
     * @param conversationRepository Repository for conversation data access.
     */
    public MessageService(
            MessageRepository messageRepository, ConversationRepository conversationRepository) {
        this.messageRepository = messageRepository;
        this.conversationRepository = conversationRepository;
        this.collectdClient = new CollectdClient();

        logger.info("MessageService initialized with repositories and metrics client.");
    }

    /**
     * Handles incoming chat messages from the user. It finds or creates a conversation, saves the
     * user message, queries an external API for a response, saves the bot's response, and returns it
     * to the client.
     *
     * @param request The chat message request from the client.
     * @return A {@link ChatMessageResponse} containing the bot's reply and the conversation ID.
     */
    public ChatMessageResponse handleMessage(@RequestBody ChatMessageRequest request) {
        if (request == null) {
            logger.error("Received null ChatMessageRequest");

            return new ChatMessageResponse("Request cannot be null.", null);
        }
        logger.info(
                """
                        Received chat message request:\
                        
                        conversationId: {}
                        content: {}""",
                request.getConversationId(),
                request.getContent());

        try {
            Conversation conversation = getOrCreateConversation(request.getConversationId());

            return processChatInteraction(conversation, request);
        } catch (Exception e) {
            logger.error("Error processing message", e);
            Long conversationId = request.getConversationId();

            return new ChatMessageResponse("Error processing message: " + e.getMessage(), conversationId);
        }
    }

    /**
     * Processes a full chat interaction consisting of a user message and the generated bot response.
     *
     * @param conversation A conversation between the user and the bot.
     * @param request      The chat message request from the client.
     * @return {@link ChatMessageResponse} containing the bot's reply and the conversation ID.
     * @throws InterruptedException if the thread is interrupted during processing.
     * @throws IOException          if an I/O error occurs during API interaction.
     */
    private ChatMessageResponse processChatInteraction(
            Conversation conversation, ChatMessageRequest request)
            throws InterruptedException, IOException {
        sendReceivedMetrics();

        Message userMsg = saveUserMessage(conversation, request.getContent());
        Message responseMessage = ApiWrapper.query(userMsg);
        logger.info("Received response from APIWrapper: '{}'", responseMessage.getContent());

        if (responseMessage.isEmpty()) {
            logger.info("Response message is empty");

            return new ChatMessageResponse("No response from the AI model.", conversation.getId());
        }

        Message botMessage =
                new Message(Message.MessageType.BOT, conversation, responseMessage.getContent());
        messageRepository.save(botMessage);
        logger.info("Bot message saved for conversation with id {}", conversation.getId());

        return new ChatMessageResponse(botMessage.getContent(), conversation.getId());
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
     * Checks whether a conversation already exists under the ID and if so, it is loaded. If not, a
     * new one is created.
     *
     * @param conversationId The ID of a conversation.
     * @return The newly created or a loaded {@link Conversation}.
     */
    private Conversation getOrCreateConversation(Long conversationId) {
        if (conversationId != null) {
            return conversationRepository.findById(conversationId).orElseGet(this::createConversation);
        } else {
            Conversation conversation = createConversation();
            logger.info("Creating new conversation with id: {}", conversation.getId());

            return conversation;
        }
    }

    /**
     * Sends metrics about received messages to Collectd.
     */
    private void sendReceivedMetrics() {
        long count = messageRepository.count();
        collectdClient.sendMetric("received", CollectdClient.CollectdType.COUNTER, count);
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
