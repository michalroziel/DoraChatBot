package de.sesqa.ase;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class DoraChatBot {
    public static void main(final String[] args) {

        try {
            Application.createQuery("Hello from the Sunny HTW Saar !");
        } catch (Exception e){
            e.printStackTrace();
        }
        //SpringApplication.run(DoraChatBot.class, args);
    }
}
