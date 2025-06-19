package de.sesqa.ase;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class VersionController {

    @Value("${version:unknown}")
    private String version;

    @Value("${buildHash:unknown}")
    private String buildNumber;

    @GetMapping("/version")
    public String getVersion() {
        return "Version: " + version + ", Commit: " + buildNumber;
    }
}