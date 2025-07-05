package de.sesqa.ase.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Main controller for handling web requests. This includes serving the main page, handling chat
 * history, and processing chat messages.
 */
@Controller
public class IndexController {
  private static final Logger logger = LoggerFactory.getLogger(IndexController.class);

  /** The application version, injected from application properties. */
  @Value("${version:unknown}")
  private String version;

  /** The build hash, injected from application properties. */
  @Value("${buildHash:unknown}")
  private String buildNumber;

  /** Constructs the BaseController with necessary repositories. */
  public IndexController() {
    logger.info("BaseController initialized:\nversion: {}\nbuildNumber: {}", version, buildNumber);
  }

  /**
   * Serves the main index page.
   *
   * @param model The Spring UI model to add attributes to.
   * @return The name of the view template to render ("index").
   */
  @GetMapping("/")
  public String index(Model model) {
    model.addAttribute("version", version);
    model.addAttribute("buildNumber", buildNumber);

    return "index";
  }
}
