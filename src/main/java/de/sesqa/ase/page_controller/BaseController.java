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