package de.sesqa.ase.dto;

public class ConversationSummaryResponse {
    private Long id;
    private String title;

    public ConversationSummaryResponse(Long id, String title) {
        this.id = id;
        this.title = title;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}