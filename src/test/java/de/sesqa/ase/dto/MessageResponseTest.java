package de.sesqa.ase.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class MessageResponseTest {

    @Test
    @DisplayName("constructor sets content and role correctly")
    void constructorSetsContentAndRoleCorrectly() {
        MessageResponse response = new MessageResponse("Hallo", "user");
        assertThat(response.getContent()).isEqualTo("Hallo");
        assertThat(response.getRole()).isEqualTo("user");
    }

    @Test
    @DisplayName("setContent updates content field")
    void setContentUpdatesContentField() {
        MessageResponse response = new MessageResponse("alt", "bot");
        response.setContent("neu");
        assertThat(response.getContent()).isEqualTo("neu");
    }

    @Test
    @DisplayName("setRole updates role field")
    void setRoleUpdatesRoleField() {
        MessageResponse response = new MessageResponse("msg", "user");
        response.setRole("bot");
        assertThat(response.getRole()).isEqualTo("bot");
    }

    @Test
    @DisplayName("allows null content and role")
    void allowsNullContentAndRole() {
        MessageResponse response = new MessageResponse(null, null);
        assertThat(response.getContent()).isNull();
        assertThat(response.getRole()).isNull();
    }
}