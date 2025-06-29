package de.sesqa.ase;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication(scanBasePackages = {"de.sesqa.ase.page_controller", "de.sesqa.ase.api"})
public class DoraChatBot {
    public static void main(final String[] args) {

        /*try {
            Application.createQuery("Hello from the Sunny HTW Saar !");
        } catch (Exception e){
            e.printStackTrace();
        }*/
        SpringApplication.run(DoraChatBot.class, args);
    }
}
