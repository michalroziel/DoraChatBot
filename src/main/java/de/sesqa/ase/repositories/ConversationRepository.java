package de.sesqa.ase.repositories;

import de.sesqa.ase.entities.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConversationRepository extends JpaRepository<Conversation, Long> {
  // Zusätzliche Query-Methoden können hier definiert werden
}
