package de.sesqa.ase.repositories;

import de.sesqa.ase.entities.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for {@link Conversation} entities.
 *
 * <p>Extends {@link JpaRepository} to provide standard CRUD operations and query method support for
 * the Conversation entity.
 *
 * <p>Additional custom query methods can be defined here as needed.
 */
public interface ConversationRepository extends JpaRepository<Conversation, Long> {
  // Additional query methods can be defined here
}
