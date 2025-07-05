package de.sesqa.ase.controller;

import de.sesqa.ase.dto.ChatMessageRequest;
import de.sesqa.ase.dto.ChatMessageResponse;
import de.sesqa.ase.dto.ConversationSummaryResponse;
import de.sesqa.ase.dto.MessageResponse;
import de.sesqa.ase.services.ConversationService;
import de.sesqa.ase.services.MessageService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final MessageService messageService;
    private final ConversationService conversationService;

    public ChatController(MessageService messageService, ConversationService conversationService) {
        this.messageService = messageService;
        this.conversationService = conversationService;
    }

    @PostMapping("/message")
    public ChatMessageResponse handleMessage(@RequestBody ChatMessageRequest request) {
        return messageService.handleMessage(request);
    }

    @GetMapping("/history")
    public List<ConversationSummaryResponse> getHistory() {
        return conversationService.getHistory();
    }

    @GetMapping("/conversation/{id}")
    public List<MessageResponse> getConversation(@PathVariable Long id) {
        return conversationService.getConversationMessages(id);
    }

}
