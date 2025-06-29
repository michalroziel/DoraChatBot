package de.sesqa.ase.entities;

import jakarta.persistence.*;

import java.util.List;

@Entity
public class Conversation {
    @Id
    private Long id;

    @OneToMany(mappedBy = "conversation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Message> messages;


    public Conversation() {
    }

    public Conversation(long id) {
        this.id = id;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void addMessages(Message message){
        messages.add(message);
    }

    public long getId() {
        return id;
    }
}
