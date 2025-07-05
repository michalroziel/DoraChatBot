package de.sesqa.ase.repositories;

import de.sesqa.ase.entities.Message;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for managing {@link Message} entities.
 *
 * <p>Extends {@link JpaRepository} to provide CRUD operations and query method support for the
 * Message entity.
 *
 * <p>Additional query methods can be defined here following Spring Data JPA conventions.
 */
public interface MessageRepository extends JpaRepository<Message, Long> {
  // Additional query methods can be defined here
}
