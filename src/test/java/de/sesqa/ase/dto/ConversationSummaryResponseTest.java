package de.sesqa.ase.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ConversationSummaryResponseTest {
    @Test
    @DisplayName("constructor sets id and title correctly")
    void constructorSetsIdAndTitleCorrectly() {
        ConversationSummaryResponse response = new ConversationSummaryResponse(42L, "Konversation");
        assertThat(response.getId()).isEqualTo(42L);
        assertThat(response.getTitle()).isEqualTo("Konversation");
    }

    @Test
    @DisplayName("setId updates id field")
    void setIdUpdatesIdField() {
        ConversationSummaryResponse response = new ConversationSummaryResponse(1L, "Test");
        response.setId(99L);
        assertThat(response.getId()).isEqualTo(99L);
    }

    @Test
    @DisplayName("setTitle updates title field")
    void setTitleUpdatesTitleField() {
        ConversationSummaryResponse response = new ConversationSummaryResponse(1L, "Alt");
        response.setTitle("Neu");
        assertThat(response.getTitle()).isEqualTo("Neu");
    }

    @Test
    @DisplayName("allows null id and title")
    void allowsNullIdAndTitle() {
        ConversationSummaryResponse response = new ConversationSummaryResponse(null, null);
        assertThat(response.getId()).isNull();
        assertThat(response.getTitle()).isNull();
    }
}
