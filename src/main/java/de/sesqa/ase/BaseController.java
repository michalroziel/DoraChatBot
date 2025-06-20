package de.sesqa.ase;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
