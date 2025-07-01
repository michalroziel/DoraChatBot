package de.sesqa.ase;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

 class DoraChatBotTest {


   @Test
    void testDoraChatBotInstantiation() {
        DoraChatBotTest bot = new DoraChatBotTest();
        assertNotNull(bot);
    }

}
