package de.sesqa.ase.page_controller;

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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class BaseController {
    private static final Logger LOGGER = LoggerFactory.getLogger(BaseController.class);

    @Value("${version:unknown}")
    private String version;

    @Value("${buildHash:unknown}")
    private String buildNumber;

    private final MessageRepository messageRepository;
    private final ConversationRepository conversationRepository;
    private final CollectdClient collectdClient;

    public BaseController(
            MessageRepository messageRepository,
            ConversationRepository conversationRepository
    ) {
        this.messageRepository = messageRepository;
        this.conversationRepository = conversationRepository;
        this.collectdClient = new CollectdClient();
    }

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("version", version);
        model.addAttribute("buildNumber", buildNumber);

        return "index";
    }

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

    @GetMapping("/api/chat/conversation/{id}")
    @ResponseBody
    public List<MessageResponse> getConversationMessages(@PathVariable Long id) {
        return conversationRepository.findById(id)
                .map(conversation -> conversation.getMessages().stream()
                        .map(message -> new MessageResponse(message.getContent(), message.getMessageType().name()))
                        .collect(Collectors.toList()))
                .orElse(List.of()); // Return an empty list if conversation not found
    }

    @GetMapping("/ping")
    @ResponseBody
    public String ping() {
        return "pong";
    }

    @PostMapping("/api/chat/message")
    @ResponseBody
    public ChatMessageResponse handleMessage(@RequestBody ChatMessageRequest request) {
        try {
            Conversation conversation;
            if (request.getConversationId() != null) {
                conversation = conversationRepository.findById(request.getConversationId())
                        .orElseGet(this::createConversation);
            } else {
                conversation = createConversation();
            }

            sendReceivedMetrics();

            Message userMsg = saveUserMessage(conversation, request.getContent());
            Message responseMessage = APIWrapper.query(userMsg);

            if (responseMessage.isEmpty()) {
                LOGGER.info("Response message is empty");
                return new ChatMessageResponse("No response from the AI model.", conversation.getId());
            }

            Message botMessage = new Message(Message.MessageType.BOT, conversation, responseMessage.getContent());
            messageRepository.save(botMessage);

            return new ChatMessageResponse(botMessage.getContent(), conversation.getId());
        } catch (Exception e) {
            LOGGER.error("Error processing message", e);
            Long conversationId = (request != null) ? request.getConversationId() : null;
            return new ChatMessageResponse("Error processing message: " + e.getMessage(), conversationId);
        }
    }

    private Conversation createConversation() {
        Conversation conversation = new Conversation();
        conversationRepository.save(conversation);
        LOGGER.info("New conversation started with ID: " + conversation.getId());

        return conversation;
    }

    private void sendReceivedMetrics() {
        long count = messageRepository.count();
        collectdClient.sendMetric("received", CollectdClient.CollectdType.GAUGE, count);
    }

    private Message saveUserMessage(Conversation conversation, String message) {
        Message userMsg = new Message(Message.MessageType.USER, conversation, message);
        messageRepository.save(userMsg);

        return userMsg;
    }
}