package de.sesqa.ase.page_controller;

import de.sesqa.ase.api.APIWrapper;
import de.sesqa.ase.objects.Message;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class BaseController {
    @Value("${version:unknown}")
    private String version;

    @Value("${buildHash:unknown}")
    private String buildNumber;

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("version", version);
        model.addAttribute("buildNumber", buildNumber);
        return "index";
    }

    @PostMapping("/message")
    @ResponseBody
    public String handleMessage(@RequestBody String message) {
        try {
            Message resp = APIWrapper.query(new Message(Message.MessageType.USER, message));
            if (!resp.isEmpty()) {
                return resp.getContent();
            } else {
                return "No response from the AI model.";
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return "";
    }
}
