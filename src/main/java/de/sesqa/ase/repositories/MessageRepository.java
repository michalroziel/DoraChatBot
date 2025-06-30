package de.sesqa.ase.repositories;


import de.sesqa.ase.entities.Message;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, Long> {
    // Zusätzliche Query-Methoden können hier definiert werden
}