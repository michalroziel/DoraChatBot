package de.sesqa.ase;

import org.junit.jupiter.api.BeforeEach;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BaseControllerTest {



    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }
}