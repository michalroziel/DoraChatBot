package de.sesqa.ase.page_controller;

import de.sesqa.ase.api.APIWrapper;
import de.sesqa.ase.entities.Conversation;
import de.sesqa.ase.entities.Message;
import de.sesqa.ase.metrics.CollectdClient;
import de.sesqa.ase.repositories.ConversationRepository;
import de.sesqa.ase.repositories.MessageRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class BaseController {
    @Value("${version:unknown}")
    private String version;

    @Value("${buildHash:unknown}")
    private String buildNumber;

    private final MessageRepository messageRepository;
    private final ConversationRepository conversationRepository;

    private final CollectdClient collectdClient;

    public BaseController(
        MessageRepository messageRepository,
        ConversationRepository conversationRepository,
        @Value("${collectd.host}") String collectdHost,
        @Value("${collectd.port}") int collectdPort
    ) {
        this.messageRepository = messageRepository;
        this.conversationRepository = conversationRepository;
        this.collectdClient = new CollectdClient(collectdHost, collectdPort);
    }

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("version", version);
        model.addAttribute("buildNumber", buildNumber);
        return "index";
    }

    @PostMapping("/message")
    @ResponseBody
    public String handleMessage(@RequestBody String message) {
        Conversation conversation = new Conversation();
        conversationRepository.save(conversation);
                    long count = messageRepository.count();
            collectdClient.sendMetric("messages", "totalcount", count);

        System.out.println("New conversation started with ID: " + conversation.getId());
        try {
            Message userMsg = new Message(Message.MessageType.USER, conversation, message);
            messageRepository.save(userMsg);

            Message resp = APIWrapper.query(userMsg);
            if (!resp.isEmpty()) {
                messageRepository.save(resp);
                return resp.getContent();
            } else {
                return "No response from the AI model.";
            }
        } catch (Exception e){
            e.printStackTrace();
            return "Error processing message: " + e.getMessage();
        }
    }
}