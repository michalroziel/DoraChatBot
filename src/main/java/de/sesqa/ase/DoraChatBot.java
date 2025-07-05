package de.sesqa.ase;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main entry point for the DoraChatBot Spring Boot application.
 *
 * <p>This class bootstraps the application and configures component scanning for the specified base
 * packages.
 *
 * <p>The following packages are scanned for Spring components:
 *
 * <ul>
 *   <li>de.sesqa.ase.controller
 *   <li>de.sesqa.ase.api
 *   <li>de.sesqa.ase.repositories
 *   <li>de.sesqa.ase.entities
 *   <li>de.sesqa.ase.services
 * </ul>
 */
@SpringBootApplication(
    scanBasePackages = {
      "de.sesqa.ase.controller",
      "de.sesqa.ase.api",
      "de.sesqa.ase.repositories",
      "de.sesqa.ase.entities",
      "de.sesqa.ase.services",
    })
public class DoraChatBot {
  /**
   * Starts the DoraChatBot Spring Boot application.
   *
   * @param args command-line arguments passed to the application
   */
  public static void main(final String[] args) {
    SpringApplication.run(DoraChatBot.class, args);
  }
}
