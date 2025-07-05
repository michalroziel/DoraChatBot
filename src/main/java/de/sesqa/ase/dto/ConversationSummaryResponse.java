package de.sesqa.ase.dto;

/**
 * Represents the summary of a conversation.
 * This DTO is used to provide a brief overview of a conversation, typically for lists.
 */
public class ConversationSummaryResponse {
    /**
     * The unique identifier of the conversation.
     */
    private Long id;
    /**
     * The title of the conversation.
     */
    private String title;

    /**
     * Constructs a new ConversationSummaryResponse with the specified ID and title.
     * @param id The ID of the conversation.
     * @param title The title of the conversation.
     */
    public ConversationSummaryResponse(Long id, String title) {
        this.id = id;
        this.title = title;
    }

    /**
     * Gets the ID of the conversation.
     * @return The conversation ID.
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the ID of the conversation.
     * @param id The conversation ID to set.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Gets the title of the conversation.
     * @return The conversation title.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the title of the conversation.
     * @param title The conversation title to set.
     */
    public void setTitle(String title) {
        this.title = title;
    }
}