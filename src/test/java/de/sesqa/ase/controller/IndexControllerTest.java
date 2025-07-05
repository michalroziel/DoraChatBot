package de.sesqa.ase.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.ui.Model;

@ExtendWith(MockitoExtension.class)
class IndexControllerTest {

  @Mock private Model model;

  @BeforeEach
  void setUp() {
    // Manually set the fields since @Value is not processed by Mockito
    ReflectionTestUtils.setField(indexController, "version", "unknown");
    ReflectionTestUtils.setField(indexController, "buildNumber", "unknown");
  }

  @InjectMocks private IndexController indexController;

  @Test
  @DisplayName("Should add version and buildNumber to model and return index view")
  void returnsIndexViewAndAddsAttributes() {
    String viewName = indexController.index(model);

    verify(model).addAttribute("version", "unknown");
    verify(model).addAttribute("buildNumber", "unknown");
    assertThat(viewName).isEqualTo("index");
  }

  @Test
  @DisplayName("Should handle null model gracefully")
  void handlesNullModelGracefully() {
    // Should throw a NullPointerException if model is null
    org.junit.jupiter.api.Assertions.assertThrows(
        NullPointerException.class,
        () -> {
          indexController.index(null);
        });
  }
}
