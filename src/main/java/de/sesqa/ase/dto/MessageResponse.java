package de.sesqa.ase.dto;

public class MessageResponse {
    private String content;
    private String role;

    public MessageResponse(String content, String role) {
        this.content = content;
        this.role = role;
    }

    // Getters and setters
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}