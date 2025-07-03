package de.sesqa.ase.page_controller;

import de.sesqa.ase.api.APIWrapper;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    public List<Map<String, Object>> getHistory() {
        return conversationRepository.findAll().stream()
                .map(conversation -> {
                    // Use the first message's content as a title, or a default if there are no messages.
                    String title = conversation.getMessages().stream()
                            .findFirst()
                            .map(Message::getContent)
                            .orElse("Conversation " + conversation.getId());

                    // Truncate long titles to keep the UI clean.
                    if (title.length() > 35) {
                        title = title.substring(0, 30) + "...";
                    }

                    Map<String, Object> summary = new HashMap<>();
                    summary.put("id", conversation.getId());
                    summary.put("title", title);
                    return summary;
                })
                .collect(Collectors.toList());
    }

    @GetMapping("/api/chat/conversation/{id}")
    @ResponseBody
    public List<Map<String, String>> getConversationMessages(@PathVariable Long id) {
        return conversationRepository.findById(id)
                .map(conversation -> conversation.getMessages().stream()
                        .map(message -> {
                            Map<String, String> messageData = new HashMap<>();
                            messageData.put("content", message.getContent());
                            // Convert the MessageType enum to a String
                            messageData.put("role", message.getMessageType().name());
                            return messageData;
                        })
                        .collect(Collectors.toList()))
                .orElse(new ArrayList<>()); // Return an empty list if conversation not found

    }

    @GetMapping("/ping")
    @ResponseBody
    public String ping() {
        return "pong";
    }

    @PostMapping("/message")
    @ResponseBody
    public String handleMessage(@RequestBody String message) {
        try {
            Conversation conversation = createConversation();
            sendReceivedMetrics();

            Message userMsg = saveUserMessage(conversation, message);
            Message responseMessage = APIWrapper.query(userMsg);

            if (responseMessage.isEmpty()) {
                LOGGER.info("Response message is empty");

                return "No response from the AI model.";
            }
            messageRepository.save(responseMessage);

            return responseMessage.getContent();
        }catch (Exception e){
            LOGGER.error("Error processing message", e);

            return "Error processing message: " + e.getMessage();
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