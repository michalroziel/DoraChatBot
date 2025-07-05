package de.sesqa.ase;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {
        "de.sesqa.ase.controller",
        "de.sesqa.ase.api",
        "de.sesqa.ase.repositories",
        "de.sesqa.ase.entities",
        "de.sesqa.ase.services",

})
public class DoraChatBot {
    public static void main(final String[] args) {
        SpringApplication.run(DoraChatBot.class, args);
    }
}