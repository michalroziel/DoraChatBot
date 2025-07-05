package de.sesqa.ase;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {
        "de.sesqa.ase.controller",
        "de.sesqa.ase.api",
        "de.sesqa.ase.repositories",   // <-- Add this line
        "de.sesqa.ase.entities"        // (optional, for completeness)
})
public class DoraChatBot {
    public static void main(final String[] args) {
        SpringApplication.run(DoraChatBot.class, args);
    }
}