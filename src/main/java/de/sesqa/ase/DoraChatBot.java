package de.sesqa.ase;

import jakarta.annotation.PostConstruct;
import java.io.File;
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
   * Ensures that the `data` directory exists in the application root.
   *
   * <p>This method is executed after the Spring context is initialized, but before the application
   * starts serving requests. It creates the `data` directory if it does not already exist, which is
   * required for the SQLite database file. If the directory cannot be created, an exception is
   * thrown.
   */
  @PostConstruct
  public void ensureDataDirectoryExists() {
    File dataDir = new File("data");
    if (!dataDir.exists()) {
      if (!dataDir.mkdirs()) {
        throw new IllegalStateException(
            "Failed to create required data directory: " + dataDir.getAbsolutePath());
      }
    }
  }

  /**
   * Main method to launch the Spring Boot application.
   *
   * @param args command-line arguments passed to the application
   */
  public static void main(final String[] args) {
    SpringApplication.run(DoraChatBot.class, args);
  }
}
