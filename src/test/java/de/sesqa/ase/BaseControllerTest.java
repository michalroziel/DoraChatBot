package de.sesqa.ase;

import de.sesqa.ase.page_controller.BaseController;
import de.sesqa.ase.repositories.ConversationRepository;
import de.sesqa.ase.repositories.MessageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BaseControllerTest {

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private ConversationRepository conversationRepository;

    private BaseController baseController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        baseController = new BaseController(messageRepository, conversationRepository);
    }

    @Test
    void testPing() {
        String response = baseController.ping();
        assertEquals("pong", response);
    }
}